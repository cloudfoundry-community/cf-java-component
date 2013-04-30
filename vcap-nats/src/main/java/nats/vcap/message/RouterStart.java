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
package nats.vcap.message;

import nats.vcap.JsonMessageBody;
import nats.vcap.NatsSubject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.util.Collections;
import java.util.List;

/**
 * See http://apidocs.cloudfoundry.com/router/publish-router-start.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("router.start")
public class RouterStart extends JsonObject implements JsonMessageBody<Void> {
	private final String id;
	private final String version;
	private final List<String> hosts;

	@JsonCreator
	public RouterStart(@JsonProperty("id") String id, @JsonProperty("version") String version, @JsonProperty("hosts")List<String> hosts) {
		this.id = id;
		this.version = version;
		this.hosts = hosts == null ? null : Collections.unmodifiableList(hosts);
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public List<String> getHosts() {
		return hosts;
	}
}
