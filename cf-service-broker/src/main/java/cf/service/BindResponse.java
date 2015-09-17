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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mike Heath
 * @deprecated Use the V2 services.
 */
@Deprecated
@JsonInclude(JsonInclude.Include.ALWAYS)
public class BindResponse extends JsonObject {

	private final String handleId;
	private final Object configuration; // TODO Figure out what this is used for, it shows up in CC REST services but nowhere else
	private final Object credentials;

	public BindResponse(
			@JsonProperty("service_id") String handleId,
			@JsonProperty("configuration") Object configuration,
			@JsonProperty("credentials") Object credentials) {
		this.handleId = handleId;
		this.configuration = configuration;
		this.credentials = credentials;
	}

	@JsonProperty("service_id")
	public String getHandleId() {
		return handleId;
	}

	public Object getConfiguration() {
		return configuration;
	}

	public Object getCredentials() {
		return credentials;
	}
}
