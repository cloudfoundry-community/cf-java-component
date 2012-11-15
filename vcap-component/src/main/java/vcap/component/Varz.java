package vcap.component;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Varz {

	private final String type;
	private final Integer index;
	private final String uuid;
	private final String host;
	private final List<String> credentials;
	private final String start;
	private final String uptime;

	private final int numCores;
	private final long memory;
	private final float cpuUtilzation;

	public Varz(
			@JsonProperty("type") String type,
			@JsonProperty("index") Integer index,
			@JsonProperty("uuid") String uuid,
			@JsonProperty("host") String host,
			@JsonProperty("credentials") List<String> credentials,
			@JsonProperty("start") String start,
			@JsonProperty("uptime") String uptime,
			@JsonProperty("num_cores") int numCores,
			@JsonProperty("mem") long memory,
			@JsonProperty("cpu") float cpuUtilzation) {
		this.type = type;
		this.index = index;
		this.uuid = uuid;
		this.host = host;
		this.credentials = Collections.unmodifiableList(new ArrayList<String>(credentials));
		this.start = start;
		this.uptime = uptime;
		this.numCores = numCores;
		this.memory = memory;
		this.cpuUtilzation = cpuUtilzation;
	}

	public List<String> getCredentials() {
		return credentials;
	}

	public String getHost() {
		return host;
	}

	public Integer getIndex() {
		return index;
	}

	public String getStart() {
		return start;
	}

	public String getType() {
		return type;
	}

	public String getUptime() {
		return uptime;
	}

	public String getUuid() {
		return uuid;
	}
}
