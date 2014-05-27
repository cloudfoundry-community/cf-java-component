/*
 *   Copyright (c) 2012 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.nats.message;

import cf.common.JsonObject;
import cf.nats.MessageBody;
import cf.nats.NatsSubject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Listens for staging messages.  Useful when you want to apply certain changes to an application only when it re-stages/restarts with service binding changes.
 * @author youngstrommj
 */

@NatsSubject("dea.*.start")
public class DeaStart extends JsonObject implements MessageBody<Void> {
	public static class VcapApplication extends JsonObject {
		private final String spaceName;
		private final String spaceId;
		public VcapApplication(
				@JsonProperty("space_name") String spaceName,
				@JsonProperty("space_id") String spaceId) {
			super();
			this.spaceName = spaceName;
			this.spaceId = spaceId;
		}
		public String getSpaceName() {
			return spaceName;
		}
		public String getSpaceId() {
			return spaceId;
		}
	}

	public static class Service extends JsonObject {
		private final String label;
		private final String name;
		private final JsonObject credentials;
		
		public Service(
				@JsonProperty("label") String label,
				@JsonProperty("name") String name,
				@JsonProperty("credentials") JsonObject credentials) {
			this.label = label;
			this.name = name;
			this.credentials = credentials;
		}
		
		public String getLabel() {
			return label;
		}
		public String getName() {
			return name;
		}
		public JsonObject getCredentials() {
			return credentials;
		}
	}

	private final String droplet;
	private final String name;
	private final Service[] services;
	private final VcapApplication vcapApplication;
	private final Integer index;

	@JsonCreator
	public DeaStart(
			@JsonProperty("droplet") String droplet,
			@JsonProperty("services") Service[] services,
			@JsonProperty("name") String name,
			@JsonProperty("vcap_application") VcapApplication vcapApplication,
			@JsonProperty("index") Integer index) {
		this.droplet = droplet;
		this.name = name;
		this.services = services;
		this.vcapApplication = vcapApplication;
		this.index = index;
	}

	public String getDroplet() {
		return droplet;
	}

	public String getName() {
		return name;
	}

	public Service[] getServices() {
		return services;
	}

	public VcapApplication getVcapApplication() {
		return vcapApplication;
	}

	public Integer getIndex() {
		return index;
	}
}
