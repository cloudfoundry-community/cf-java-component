package cf.service;

import org.codehaus.jackson.annotate.JsonProperty;

import cf.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BindRequest extends JsonObject {

	private final String serviceInstanceId;
	private final String label;
	private final String email;
	// Ignore bind_options field, it is always empty in v2

	public BindRequest(
			@JsonProperty("service_id") String serviceInstanceId,
			@JsonProperty("label") String label,
			@JsonProperty("email") String email) {
		this.serviceInstanceId = serviceInstanceId;
		this.label = label;
		this.email = email;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public String getLabel() {
		return label;
	}

	public String getEmail() {
		return email;
	}
}
