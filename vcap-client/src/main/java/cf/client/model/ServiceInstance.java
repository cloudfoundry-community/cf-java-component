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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceInstance extends JsonObject {

	private static final String SERVICE_PLAN_GUID = "service_plan_guid";
	private static final String SPACE_GUID = "space_guid";
	private static final String GATEWAY_DATA = "gateway_data";
	private final String name;
	private final JsonNode credentials;
	private final UUID servicePlanGuid;
	private final UUID spaceGuid;
	private final JsonNode gatewayData;

	public ServiceInstance(
			@JsonProperty("name") String name,
			@JsonProperty("credentials") JsonNode credentials,
			@JsonProperty(SERVICE_PLAN_GUID) UUID servicePlanGuid,
			@JsonProperty(SPACE_GUID) UUID spaceGuid,
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
	public UUID getServicePlanGuid() {
		return servicePlanGuid;
	}

	@JsonProperty(SPACE_GUID)
	public UUID getSpaceGuid() {
		return spaceGuid;
	}

	@JsonProperty(GATEWAY_DATA)
	public JsonNode getGatewayData() {
		return gatewayData;
	}
}
