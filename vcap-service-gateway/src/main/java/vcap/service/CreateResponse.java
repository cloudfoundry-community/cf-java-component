package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateResponse extends JsonObject {
	private final String serviceInstanceId;
	private final Object configuration; // TODO Figure out what this is used for, it shows up in CC REST services but nowhere else
	private final Object credentials;

	public CreateResponse(
			@JsonProperty("service_id") String serviceInstanceId,
			@JsonProperty("configuration") Object configuration,
			@JsonProperty("credentials") Object credentials) {
		this.serviceInstanceId = serviceInstanceId;
		this.configuration = configuration;
		this.credentials = credentials;
	}

	@JsonProperty("service_id")
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Object getConfiguration() {
		return configuration;
	}

	public Object getCredentials() {
		return credentials;
	}
}
