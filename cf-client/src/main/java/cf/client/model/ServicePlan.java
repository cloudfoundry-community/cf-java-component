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

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServicePlan extends JsonObject {

	private static final String SERVICE_GUID = "service_guid";
	private static final String UNIQUE_ID = "unique_id";
	private final String name;
	private final String description;
	private final UUID serviceGuid;
	private final Boolean free;
	private final String uniqueId;
	private final Boolean publicPlan;

	
	@Deprecated
	public ServicePlan(
			String name,
			String description,
			UUID serviceGuid,
			Boolean free,
			String uniqueId) {
		this(name, description, serviceGuid, free, uniqueId, true);
	}

	public ServicePlan(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty(SERVICE_GUID) UUID serviceGuid,
			@JsonProperty("free") Boolean free,
			@JsonProperty(UNIQUE_ID) String uniqueId,
			@JsonProperty("public") Boolean publicPlan) {
		this.name = name;
		this.description = description;
		this.serviceGuid = serviceGuid;
		this.free = free;
		this.uniqueId = uniqueId;
		this.publicPlan = publicPlan;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean isFree() {
		return free;
	}

	@JsonProperty("public")
	public Boolean isPublicPlan() {
		return publicPlan;
	}


	@JsonProperty(SERVICE_GUID)
	public UUID getServiceGuid() {
		return serviceGuid;
	}

	@JsonProperty(UNIQUE_ID)
	public String getUniqueId() {
		return uniqueId;
	}
}
