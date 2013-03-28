package vcap.service.integration;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateServicePlanRequest {

	private final String name;
	private final String description;
	private final String serviceGuid;

	public CreateServicePlanRequest(String name, String description, String serviceGuid) {
		this.name = name;
		this.description = description;
		this.serviceGuid = serviceGuid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isFree() {
		return true;
	}

	@JsonProperty("service_guid")
	public String getServiceGuid() {
		return serviceGuid;
	}
}

