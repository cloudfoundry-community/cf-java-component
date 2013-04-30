package cf.client.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceInstance extends JsonObject {

	private static final String SERVICE_PLAN_GUID = "service_plan_guid";
	private static final String SPACE_GUID = "space_guid";
	private static final String GATEWAY_DATA = "gateway_data";
	private final String name;
	private final JsonNode credentials;
	private final String servicePlanGuid;
	private final String spaceGuid;
	private final JsonNode gatewayData;

	public ServiceInstance(
			@JsonProperty("name") String name,
			@JsonProperty("credentials") JsonNode credentials,
			@JsonProperty(SERVICE_PLAN_GUID) String servicePlanGuid,
			@JsonProperty(SPACE_GUID) String spaceGuid,
			@JsonProperty(GATEWAY_DATA) JsonNode gatewayData) {
		this.name = name;
		this.credentials = credentials;
		this.servicePlanGuid = servicePlanGuid;
		this.spaceGuid = spaceGuid;
		this.gatewayData = gatewayData;
	}

	public String getName() {
		return name;
	}

	public JsonNode getCredentials() {
		return credentials;
	}

	@JsonProperty(SERVICE_PLAN_GUID)
	public String getServicePlanGuid() {
		return servicePlanGuid;
	}

	@JsonProperty(SPACE_GUID)
	public String getSpaceGuid() {
		return spaceGuid;
	}

	@JsonProperty(GATEWAY_DATA)
	public JsonNode getGatewayData() {
		return gatewayData;
	}
}
