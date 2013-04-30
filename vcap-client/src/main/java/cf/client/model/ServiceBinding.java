package cf.client.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBinding extends JsonObject {
	private static final String APP_GUID = "app_guid";
	private static final String SERVICE_INSTANCE_GUID = "service_instance_guid";
	private static final String GATEWAY_DATA = "gateway_data";
	private static final String GATEWAY_NAME = "gateway_name";
	private final UUID appGuid;
	private final UUID serviceInstanceGuid;
	private final JsonNode credentials;
	private final JsonNode gatewayData;
	private final String gatewayName;

	public ServiceBinding(
			@JsonProperty(APP_GUID) UUID appGuid,
			@JsonProperty(SERVICE_INSTANCE_GUID) UUID serviceInstanceGuid,
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
	public UUID getAppGuid() {
		return appGuid;
	}

	@JsonProperty(SERVICE_INSTANCE_GUID)
	public UUID getServiceInstanceGuid() {
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
