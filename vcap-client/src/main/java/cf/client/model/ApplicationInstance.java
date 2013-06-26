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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ApplicationInstance {

	public enum State {
		RUNNING,
		STARTING,
		STOPPED,
		FLAPPING,
		UNKNOWN
	}

	private final State state;
	private final Date since;
	private InetSocketAddress debugAddress;
	private InetSocketAddress consoleAddress;

	public ApplicationInstance(State state, Date since, InetSocketAddress debugAddress, InetSocketAddress consoleAddress) {
		this.state = state;
		this.since = since;
		this.debugAddress = debugAddress;
		this.consoleAddress = consoleAddress;
	}

	@JsonCreator
	public ApplicationInstance(
			@JsonProperty("state") String state,
			@JsonProperty("since") double since,
			@JsonProperty("debug_ip") String debugIp,
			@JsonProperty("debug_port") Integer debugPort,
			@JsonProperty("console_ip") String consoleIp,
			@JsonProperty("console_port") Integer consolePort
	) {
		State stateValue = State.valueOf(state.toUpperCase());
		if (stateValue == null) {
			stateValue = State.UNKNOWN;
		}
		this.state = stateValue;
		this.since = new Date((long)Math.floor(since * 1000));
		this.debugAddress = (debugIp == null || debugPort == null) ? null : new InetSocketAddress(debugIp, debugPort);
		this.consoleAddress = (consoleIp == null || consolePort == null) ? null : new InetSocketAddress(consoleIp, consolePort);
	}

	public State getState() {
		return state;
	}

	@JsonIgnore
	public Date getSince() {
		return since;
	}

	@JsonIgnore
	public InetSocketAddress getDebugAddress() {
		return debugAddress;
	}

	@JsonIgnore
	public InetSocketAddress getConsoleAddress() {
		return consoleAddress;
	}

	@JsonProperty("since")
	public double getSinceDouble() {
		return ((double)since.getTime()) / 1000;
	}

	@JsonProperty("debug_ip")
	public String getDebugIp() {
		return debugAddress == null ? null : debugAddress.getHostString();
	}

	@JsonProperty("debug_port")
	public Integer getDebugPort() {
		return debugAddress == null ? null : debugAddress.getPort();
	}

	@JsonProperty("console_ip")
	public String getConsoleIp() {
		return consoleAddress == null ? null : consoleAddress.getHostString();
	}

	@JsonProperty("console_port")
	public Integer getConsolePort() {
		return consoleAddress == null ? null : consoleAddress.getPort();
	}
}
