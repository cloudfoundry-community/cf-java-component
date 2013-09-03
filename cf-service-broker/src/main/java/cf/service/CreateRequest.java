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

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateRequest extends JsonObject {
	private static final String SPACE_GUID = "space_guid";
	private static final String ORGANIZATION_GUID = "organization_guid";
	private static final String UNIQUE_ID = "unique_id";
	private final String label;
	private final String name;
	private final String email;
	private final String plan;
	private final String version;
	private final String provider;
	private final String spaceGuid;
	private final String organizationGuid;
	private final String uniqueId;

	public CreateRequest(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("email") String email,
			@JsonProperty("plan") String plan,
			@JsonProperty("version") String version,
			@JsonProperty("provider") String provider,
			@JsonProperty(SPACE_GUID) String spaceGuid,
			@JsonProperty(ORGANIZATION_GUID) String organizationGuid,
			@JsonProperty(UNIQUE_ID) String uniqueId) {
		this.label = label;
		this.name = name;
		this.email = email;
		this.plan = plan;
		this.version = version;
		this.provider = provider;
		this.spaceGuid = spaceGuid;
		this.organizationGuid = organizationGuid;
		this.uniqueId = uniqueId;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPlan() {
		return plan;
	}

	public String getVersion() {
		return version;
	}

	public String getProvider() {
		return provider;
	}

	@JsonProperty(SPACE_GUID)
	public String getSpaceGuid() {
		return spaceGuid;
	}

	@JsonProperty(ORGANIZATION_GUID)
	public String getOrganizationGuid() {
		return organizationGuid;
	}

	@JsonProperty(UNIQUE_ID)
	public String getUniqueId() {
		return uniqueId;
	}
}
