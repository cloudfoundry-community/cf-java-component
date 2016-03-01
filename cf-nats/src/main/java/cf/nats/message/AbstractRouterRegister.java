/*
 *   Copyright (c) 2015 Intellectual Reserve, Inc.  All rights reserved.
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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mike Heath
 */
public class AbstractRouterRegister extends JsonObject implements MessageBody<Void> {
	static final String JSON_HOST_ATTRIBUTE = "host";
	static final String JSON_PORT_ATTRIBUTE = "port";
	static final String JSON_URIS_ATTRIBUTE = "uris";

	static final String JSON_APP_ATTRIBUTE = "app";
	static final String JSON_INDEX_ATTRIBUTE = "index";
	static final String JSON_PRIVATE_INSTANCE_ID_ATTRIBUTE = "private_instance_id";
	static final String JSON_DEA_ATTRIBUTE = "dea";

	static final String JSON_TAGS_ATTRIBUTE = "tags";

	// Required fields
	private final String host;
	private final int port;
	private final List<String> uris;

	// App instance fields
	private final String app;
	private final Integer index;
	private final String privateInstanceId;
	private final String dea;


	private final Map<String, String> tags;

	public AbstractRouterRegister(String host, int port, String... uris) {
		this(host, port, Arrays.asList(uris), null, null, null, null, null);
	}

	public AbstractRouterRegister(String host, int port, List<String> uris, String app, Integer index, String privateInstanceId, String dea, Map<String, String> tags) {
		Objects.requireNonNull(host, "Host is a required field");
		Objects.requireNonNull(uris, "uris is a required field");
		this.host = host;
		this.port = port;
		this.uris = Collections.unmodifiableList(new ArrayList<>(uris));
		this.app = app;
		this.index = index;
		this.privateInstanceId = privateInstanceId;
		this.dea = dea;
		this.tags = (tags == null) ?
				Collections.<String, String>emptyMap() : Collections.unmodifiableMap(new HashMap<>(tags));
	}

	@JsonProperty(JSON_HOST_ATTRIBUTE)
	public String getHost() {
		return host;
	}

	@JsonProperty(JSON_PORT_ATTRIBUTE)
	public int getPort() {
		return port;
	}

	@JsonProperty(JSON_URIS_ATTRIBUTE)
	public List<String> getUris() {
		return uris;
	}

	@JsonProperty(JSON_APP_ATTRIBUTE)
	public String getApp() {
		return app;
	}

	@JsonProperty(JSON_INDEX_ATTRIBUTE)
	public Integer getIndex() {
		return index;
	}

	@JsonProperty(JSON_PRIVATE_INSTANCE_ID_ATTRIBUTE)
	public String getPrivateInstanceId() {
		return privateInstanceId;
	}

	@JsonProperty(JSON_DEA_ATTRIBUTE)
	public String getDea() {
		return dea;
	}

	@JsonProperty(JSON_TAGS_ATTRIBUTE)
	public Map<String, String> getTags() {
		return tags;
	}
}
