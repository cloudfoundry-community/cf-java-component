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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mike Heath
 */
public class User extends JsonObject {

	private final Boolean admin;
	private final Boolean active;
	private final UUID defaultSpaceGuid;
	private final String username;

	public User(
			@JsonProperty("admin") Boolean admin,
			@JsonProperty("active") Boolean active,
			@JsonProperty("default_space_guid") UUID defaultSpaceGuid,
			@JsonProperty("username") String username) {
		this.admin = admin;
		this.active = active;
		this.defaultSpaceGuid = defaultSpaceGuid;
		this.username = username;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public Boolean getActive() {
		return active;
	}
	
	public UUID getDefaultSpaceGuid() {
		return defaultSpaceGuid;
	}
	
	public String getUsername() {
		return username;
	}
}
