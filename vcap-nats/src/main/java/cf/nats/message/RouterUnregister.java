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

import cf.nats.NatsSubject;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("router.unregister")
public class RouterUnregister extends RouterRegister {
	public RouterUnregister(
			@JsonProperty("host") String host,
			@JsonProperty("port") Integer port,
			@JsonProperty("app") String app,
			@JsonProperty("dea") String dea,
			@JsonProperty("uris") List<String> uris,
			@JsonProperty("tags") Map<String, String> tags) {
		super(host, port, app, dea, uris, tags);
	}

	public RouterUnregister(RouterRegister routerRegister) {
		this(
				routerRegister.getHost(),
				routerRegister.getPort(),
				routerRegister.getApp(),
				routerRegister.getDea(),
				routerRegister.getUris(),
				routerRegister.getTags());
	}
}
