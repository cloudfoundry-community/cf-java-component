package vcap.client.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import vcap.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBinding extends JsonObject {
	private static final String APP_GUID = "app_guid";
	private static final String SERVICE_INSTANCE_GUID = "service_instance_guid";
	private static final String GATEWAY_DATA = "gateway_data";
	private static final String GATEWAY_NAME = "gateway_name";
	private final String appGuid;
	private final String serviceInstanceGuid;
	private final JsonNode credentials;
	private final JsonNode gatewayData;
	private final String gatewayName;

	public ServiceBinding(
			@JsonProperty(APP_GUID) String appGuid,
			@JsonProperty(SERVICE_INSTANCE_GUID) String serviceInstanceGuid,
			@JsonProperty("credentials") JsonNode credentials,
			@JsonProperty(GATEWAY_DATA) JsonNode gatewayData,
			@JsonProperty(GATEWAY_NAME) String gatewayName) {
		this.appGuid = appGuid;
		this.serviceInstanceGuid = serviceInstanceGuid;
		this.credentials = credentials;
		this.gatewayData = gatewayData;
		this.gatewayName = gatewayName;
	}

	@JsonProperty(APP_GUID)
	public String getAppGuid() {
		return appGuid;
	}

	@JsonProperty(SERVICE_INSTANCE_GUID)
	public String getServiceInstanceGuid() {
		return serviceInstanceGuid;
	}

	public JsonNode getCredentials() {
		return credentials;
	}

	@JsonProperty(GATEWAY_DATA)
	public JsonNode getGatewayData() {
		return gatewayData;
	}

	@JsonProperty(GATEWAY_NAME)
	public String getGatewayName() {
		return gatewayName;
	}
}
