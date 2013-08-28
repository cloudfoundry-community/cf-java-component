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
package cf.service;

import cf.common.JsonObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateResponse extends JsonObject {
	private final String serviceInstanceId;
	private final JsonNode configuration; // TODO Figure out what this is used for, it shows up in CC REST services but nowhere else
	private final JsonNode credentials;

	public CreateResponse(
			@JsonProperty("service_id") String serviceInstanceId,
			@JsonProperty("configuration") JsonNode configuration,
			@JsonProperty("credentials") JsonNode credentials) {
		this.serviceInstanceId = serviceInstanceId;
		this.configuration = configuration;
		this.credentials = credentials;
	}

	@JsonProperty("service_id")
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public JsonNode getConfiguration() {
		return configuration;
	}

	public JsonNode getCredentials() {
		return credentials;
	}
}
