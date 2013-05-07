/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
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

import cf.nats.MessageBody;
import cf.nats.NatsSubject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * See http://apidocs.cloudfoundry.com/router/subscribe-router-register
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("router.register")
public class RouterRegister extends JsonObject implements MessageBody<Void> {
	private final String host;
	private final Integer port;
	private final String app;
	private final String dea;
	private final List<String> uris;
	private final Map<String, String> tags;

	public RouterRegister(String host, int port, String... uris) {
		this(host, port, null, null, Arrays.asList(uris), null);
	}

	@JsonCreator
	public RouterRegister(
			@JsonProperty("host") String host,
			@JsonProperty("port") Integer port,
			@JsonProperty("app") String app,
			@JsonProperty("dea") String dea,
			@JsonProperty("uris") List<String> uris,
			@JsonProperty("tags") Map<String, String> tags) {
		this.host = host;
		this.port = port;
		this.app = app;
		this.dea = dea;
		this.uris = Collections.unmodifiableList(new ArrayList<>(uris));
		this.tags = tags == null ? null : Collections.unmodifiableMap(new HashMap<>(tags));
	}

	public String getApp() {
		return app;
	}

	public String getDea() {
		return dea;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public List<String> getUris() {
		return uris;
	}
}
