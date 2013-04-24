package nats.vcap.message;

import nats.vcap.NatsSubject;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("router.unregister")
public class RouterUnregister extends RouterRegister {
	public RouterUnregister(
			@JsonProperty("host") String host,
			@JsonProperty("port") Integer port,
			@JsonProperty("app") String app,
			@JsonProperty("dea") String dea,
			@JsonProperty("uris") List<String> uris,
			@JsonProperty("tags") Map<String, String> tags) {
		super(host, port, app, dea, uris, tags);
	}

	public RouterUnregister(RouterRegister routerRegister) {
		this(
				routerRegister.getHost(),
				routerRegister.getPort(),
				routerRegister.getApp(),
				routerRegister.getDea(),
				routerRegister.getUris(),
				routerRegister.getTags());
	}
}
