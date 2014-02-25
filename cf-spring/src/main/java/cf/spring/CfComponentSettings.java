/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CfComponentSettings {

	private final String host;
	private final int port;
	private final int index;
	private final String uuid;
	private final String username;
	private final String password;

	public static class Builder {
		private String host = "127.0.0.1";
		private int port = 8080;
		private int index = 0;
		private String uuid = UUID.randomUUID().toString();
		private String username = "varz";
		private String password = new BigInteger(256, ThreadLocalRandom.current()).toString(32);

		public Builder host(String host) {
			this.host = host;
			return this;
		}

		public Builder port(int port) {
			this.port = port;
			return this;
		}

		public Builder index(int index) {
			this.index = index;
			return this;
		}

		public Builder uuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public CfComponentSettings build() {
			return new CfComponentSettings(host, port, index, uuid, username, password);
		}
	}


	private CfComponentSettings(String host, int port, int index, String uuid, String username, String password) {
		this.host = host;
		this.port = port;
		this.index = index;
		this.uuid = uuid;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getIndex() {
		return index;
	}

	public String getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
