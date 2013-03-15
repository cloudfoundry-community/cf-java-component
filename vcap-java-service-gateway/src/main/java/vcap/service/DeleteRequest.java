package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DeleteRequest {

	private final String serviceId;

	public DeleteRequest(
			@JsonProperty("service_id") String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}
}
