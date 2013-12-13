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

import java.util.UUID;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BindRequest extends JsonObject {

	private final String serviceInstanceId;
	private final String label;
	private final String email;
	private final UUID appId;
	// Ignore bind_options field, it is always empty in v2

	public BindRequest(
			@JsonProperty("service_id") String serviceInstanceId,
			@JsonProperty("label") String label,
			@JsonProperty("email") String email,
			@JsonProperty("app_id") UUID appId) {
		this.serviceInstanceId = serviceInstanceId;
		this.label = label;
		this.email = email;
		this.appId = appId;
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
	
	public UUID getAppId() {
		return appId;
	}
}
