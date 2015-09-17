/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.client.model;

import cf.common.JsonObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

/**
 * @author Mike Heath
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
