package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BindRequest {

	private final String serviceInstanceId;
	private final String label;
	private final String email;
	// TODO What is the binding_options field?

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
