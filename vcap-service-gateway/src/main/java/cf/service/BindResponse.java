package cf.service;

import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BindResponse extends JsonObject {

	private final String handleId;
	private final Object configuration; // TODO Figure out what this is used for, it shows up in CC REST services but nowhere else
	private final Object credentials;

	public BindResponse(
			@JsonProperty("service_id") String handleId,
			@JsonProperty("configuration") Object configuration,
			@JsonProperty("credentials") Object credentials) {
		this.handleId = handleId;
		this.configuration = configuration;
		this.credentials = credentials;
	}

	@JsonProperty("service_id")
	public String getHandleId() {
		return handleId;
	}

	public Object getConfiguration() {
		return configuration;
	}

	public Object getCredentials() {
		return credentials;
	}
}
