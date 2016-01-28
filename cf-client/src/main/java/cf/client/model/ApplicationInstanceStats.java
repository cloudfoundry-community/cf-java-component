package cf.client.model;

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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mike Heath
 */
public class ApplicationInstanceStats {

	public enum State {
		RUNNING,
		STARTING,
		STOPPED,
		FLAPPING,
		UNKNOWN
	}

	private final State state;
	private final Stats stats;


	public ApplicationInstanceStats(State state, Stats stats) {
		this.state = state;
		this.stats = stats;
		
	}

	@JsonCreator
	public ApplicationInstanceStats(
			@JsonProperty("state") String state,
			@JsonProperty("stats") Stats stats
	) {
		State stateValue = null;
		try {
			stateValue = State.valueOf(state.toUpperCase());
		} catch (IllegalArgumentException e) {
			stateValue = State.UNKNOWN;
		}
		this.state = stateValue;
		this.stats = stats;
	
	}

	public State getState() {
		return state;
	}

	public Stats getStats() {
		return stats;
	}

}
