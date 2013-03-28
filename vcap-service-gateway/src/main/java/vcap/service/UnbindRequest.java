package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class UnbindRequest {
	private final String serviceInstanceId;
	private final String handleId;
	// TODO What are binding_options for

	public UnbindRequest(
			@JsonProperty("service__id") String serviceInstanceId,
			@JsonProperty("handle_id") String handleId) {
		this.serviceInstanceId = serviceInstanceId;
		this.handleId = handleId;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public String getHandleId() {
		return handleId;
	}
}
