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

@NatsSubject("dea.heartbeat")
public class DeaHeartbeat extends JsonObject implements MessageBody<Void> {
	public static class Droplet extends JsonObject {
		public static final String RUNNING = "RUNNING";

		private final String droplet;
		private final String instance;
		private final Integer index;
		private final String state;

		@JsonCreator
		public Droplet(
				@JsonProperty("droplet") String droplet,
				@JsonProperty("instance") String instance,
				@JsonProperty("index") Integer index,
				@JsonProperty("state") String state) {
			this.droplet = droplet;
			this.instance = instance;
			this.index = index;
			this.state = state;
		}

		public String getDroplet() {
			return droplet;
		}
		public String getInstance() {
			return instance;
		}
		public Integer getIndex() {
			return index;
		}
		public String getState() {
			return state;
		}
	}

	private final String dea;
	private final Droplet[] droplets;

	@JsonCreator
	public DeaHeartbeat(
			@JsonProperty("dea") String dea,
			@JsonProperty("droplets") Droplet[] droplets) {
		this.dea = dea;
		this.droplets = droplets;
	}

	public String getDea() {
		return dea;
	}

	public Droplet[] getDroplets() {
		return droplets;
	}
}
