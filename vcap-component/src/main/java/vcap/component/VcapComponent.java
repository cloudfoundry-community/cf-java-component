package vcap.component;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import nats.vcap.NatsVcap;
import nats.vcap.VcapPublication;
import nats.vcap.VcapPublicationHandler;
import nats.vcap.message.ComponentAnnounce;
import nats.vcap.message.ComponentDiscover;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import vcap.component.http.JsonTextResponseRequestHandler;
import vcap.component.http.RequestException;
import vcap.component.http.SimpleHttpServer;
import vcap.component.util.Common;
import vcap.component.util.ProcessUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VcapComponent {

	private final NatsVcap nats;
	private final String type;

	private final long startTime = System.currentTimeMillis();

	private final String uuid = Common.generateUniqueId();
	private final String username = Common.generateCredential();
	private final String password = Common.generateCredential();

	private final List<VarzUpdater> varzUpdaters;

	private final ObjectMapper mapper = new ObjectMapper();

	public VcapComponent(NatsVcap nats, SimpleHttpServer httpServer, String type, List<VarzUpdater> varzUpdaters) {
		this.nats = nats;
		this.type = type;
		this.varzUpdaters = varzUpdaters;

		nats.subscribe(ComponentDiscover.class, new VcapPublicationHandler<ComponentDiscover, ComponentAnnounce>() {
			@Override
			public void onMessage(VcapPublication<ComponentDiscover, ComponentAnnounce> publication) {
				publication.reply(buildComponentAnnounce());
			}
		});

		nats.publish(buildComponentAnnounce());

		httpServer.addHandler("/healthz", new AuthenticatedJsonTextResponseRequestHandler() {
			@Override
			public String handleAuthenticatedRequest(HttpRequest request) throws RequestException {
				return "ok\n";
			}
		});

		httpServer.addHandler("/varz", new AuthenticatedJsonTextResponseRequestHandler() {
			@Override
			protected String handleAuthenticatedRequest(HttpRequest request) {
				final Varz varz = buildVarz();
				try {
					return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(varz);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	protected Varz buildVarz() {
		ProcessUtils.ProcessStats processStats;
		try {
			processStats = ProcessUtils.getProcessStats();
		} catch (IOException e) {
			processStats = null;
		}
		Varz varz = new Varz(
				type,
				0,
				uuid,
				getLocalAddress(),
				Arrays.asList(username, password),
				formatStartTime(),
				formatUptime(),
				Runtime.getRuntime().availableProcessors(),
				(processStats == null) ? 0l : processStats.residentSetSize,
				(processStats == null) ? 0f : processStats.cpuUtilization
				);
		if (varzUpdaters != null) {
			for (VarzUpdater updater : varzUpdaters) {
				varz = updater.update(varz);
			}
		}
		return varz;
	}

	protected ComponentAnnounce buildComponentAnnounce() {
		return new ComponentAnnounce(
				type,
				0, // What is the index?
				uuid,
				getLocalAddress(),
				Arrays.asList(username, password),
				formatStartTime(),
				formatUptime());
	}

	protected String getLocalAddress() {
		// Use the address Nats is using to increase the likelihood that the host that we advertise can be routed to from other hosts.
		final InetSocketAddress localAddress = (InetSocketAddress) nats.getNats().getConnectionStatus().getLocalAddress();
		return localAddress.getHostString();
	}

	private String formatStartTime() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date(startTime));
	}

	private String formatUptime() {
		long delta = System.currentTimeMillis() - startTime;
		delta /= 1000; // Drop the milliseconds
		final long days = delta / TimeUnit.DAYS.toSeconds(1);
		delta -= TimeUnit.DAYS.toSeconds(days);
		final long hours = delta / TimeUnit.HOURS.toSeconds(1);
		delta -= TimeUnit.HOURS.toSeconds(hours);
		final long minutes = delta / TimeUnit.MINUTES.toSeconds(1);
		delta -= TimeUnit.MINUTES.toSeconds(1);
		final long seconds = delta;
		return String.format("%dd:%dh:%dm:%ds", days, hours, minutes, seconds);
	}

	private abstract class AuthenticatedJsonTextResponseRequestHandler extends JsonTextResponseRequestHandler {
		@Override
		public String handle(HttpRequest request) throws RequestException {
			final String encodedAuthorization = request.getHeader(HttpHeaders.Names.AUTHORIZATION);
			if (encodedAuthorization == null) {
				throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
			}
			final String authorization = new String(Base64.decodeBase64(encodedAuthorization));
			final String[] credentials = authorization.split(":");
			if (credentials.length != 2) {
				throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
			}
			if (!username.equals(credentials[0]) && !password.equals(password)) {
				throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
			}
			return handleAuthenticatedRequest(request);
		}

		protected abstract String handleAuthenticatedRequest(HttpRequest request) throws RequestException;
	}
}
