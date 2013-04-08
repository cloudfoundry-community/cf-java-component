package vcap.service;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import vcap.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateResponse extends JsonObject {
	private final String serviceInstanceId;
	private final JsonNode configuration; // TODO Figure out what this is used for, it shows up in CC REST services but nowhere else
	private final JsonNode credentials;

	public CreateResponse(
			@JsonProperty("service_id") String serviceInstanceId,
			@JsonProperty("configuration") JsonNode configuration,
			@JsonProperty("credentials") JsonNode credentials) {
		this.serviceInstanceId = serviceInstanceId;
		this.configuration = configuration;
		this.credentials = credentials;
	}

	@JsonProperty("service_id")
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public JsonNode getConfiguration() {
		return configuration;
	}

	public JsonNode getCredentials() {
		return credentials;
	}
}
