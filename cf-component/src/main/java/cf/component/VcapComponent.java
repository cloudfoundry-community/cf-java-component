/*
 *   Copyright (c) 2012 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.component;

import cf.nats.CfNats;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import cf.nats.Publication;
import cf.nats.PublicationHandler;
import cf.nats.message.ComponentAnnounce;
import cf.nats.message.ComponentDiscover;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import cf.component.http.JsonTextResponseRequestHandler;
import cf.component.http.RequestException;
import cf.component.http.SimpleHttpServer;
import cf.component.util.Common;
import cf.component.util.ProcessUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VcapComponent {

	private final CfNats nats;
	private final String type;

	private final long startTime = System.currentTimeMillis();

	private final String uuid = Common.generateUniqueId();
	private final String username = Common.generateCredential();
	private final String password = Common.generateCredential();

	private final List<VarzUpdater> varzUpdaters;

	private final ObjectMapper mapper = new ObjectMapper();

	public VcapComponent(CfNats nats, SimpleHttpServer httpServer, String type, List<VarzUpdater> varzUpdaters) {
		this.nats = nats;
		this.type = type;
		this.varzUpdaters = varzUpdaters;

		nats.subscribe(ComponentDiscover.class, new PublicationHandler<ComponentDiscover, ComponentAnnounce>() {
			@Override
			public void onMessage(Publication<ComponentDiscover, ComponentAnnounce> publication) {
				publication.reply(buildComponentAnnounce());
			}
		});

		nats.publish(buildComponentAnnounce());

		httpServer.addHandler(Pattern.compile("/healthz"), new AuthenticatedJsonTextResponseRequestHandler() {
			@Override
			public String handleAuthenticatedRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				if (!request.getMethod().equals(HttpMethod.GET)) {
					throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
				}
				return "ok\n";
			}
		});

		httpServer.addHandler(Pattern.compile("/varz"), new AuthenticatedJsonTextResponseRequestHandler() {
			@Override
			protected String handleAuthenticatedRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				if (!request.getMethod().equals(HttpMethod.GET)) {
					throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
				}
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
		// TODO Instead of getting the address from NATS, use the NATS host address to open a socket and use the local address
		//final InetSocketAddress localAddress = (InetSocketAddress) nats.getNats().getConnectionStatus().getLocalAddress();
		//return localAddress.getHostString();
		return null;
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
		public String handle(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
			final String encodedAuthorization = request.headers().get(HttpHeaders.Names.AUTHORIZATION);
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
			return handleAuthenticatedRequest(request, uriMatcher, body);
		}

		protected abstract String handleAuthenticatedRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException;
	}
}
