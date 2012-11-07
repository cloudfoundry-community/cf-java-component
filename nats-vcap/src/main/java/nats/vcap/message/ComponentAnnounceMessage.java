package nats.vcap.message;

import nats.vcap.NatsSubject;
import nats.vcap.VcapMessageBody;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * See http://apidocs.cloudfoundry.com/cloud-controller/publish-vcap-component-announce,
 * http://apidocs.cloudfoundry.com/dea/publish-vcap-component-announce,
 * http://apidocs.cloudfoundry.com/health-manager/publish-vcap-component-announce, etc.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("vcap.component.announce")
public class ComponentAnnounceMessage implements VcapMessageBody<Void> {

	public static final String TYPE_CLOUD_CONTROLLER = "CloudController";
	public static final String TYPE_DEA = "DEA";
	public static final String TYPE_HEALTH_MANAGER = "HealthManager";
	public static final String TYPE_ROUTER = "Router";

	private final String type;
	private final String index;
	private final String uuid;
	private final String host;
	private final List<String> credentials;
	private final String start;

	public ComponentAnnounceMessage(
			@JsonProperty("type") String type,
			@JsonProperty("index") String index,
			@JsonProperty("uuid") String uuid,
			@JsonProperty("host") String host,
			@JsonProperty("credentials") List<String> credentials,
			@JsonProperty("start") String start) {
		this.type = type;
		this.index = index;
		this.uuid = uuid;
		this.host = host;
		this.credentials = Collections.unmodifiableList(new ArrayList<String>(credentials));
		this.start = start;
	}

	public List<String> getCredentials() {
		return credentials;
	}

	public String getHost() {
		return host;
	}

	public String getIndex() {
		return index;
	}

	public String getStart() {
		return start;
	}

	public String getType() {
		return type;
	}

	public String getUuid() {
		return uuid;
	}
}
