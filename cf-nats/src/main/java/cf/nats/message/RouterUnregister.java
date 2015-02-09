/*
 *   Copyright (c) 2012,2015 Intellectual Reserve, Inc.  All rights reserved.
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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("router.unregister")
public class RouterUnregister extends AbstractRouterRegister {

	public static RouterUnregister toRouterUnregister(AbstractRouterRegister routerRegister) {
		return  new RouterUnregister(
				routerRegister.getHost(),
				routerRegister.getPort(),
				routerRegister.getUris(),
				routerRegister.getApp(),
				routerRegister.getPrivateInstanceId(),
				routerRegister.getIndex(),
				routerRegister.getDea(),
				routerRegister.getTags()
		);
	}

	public RouterUnregister(String host, int port, String... uris) {
		super(host, port, uris);
	}

	@JsonCreator
	public RouterUnregister(
			@JsonProperty(JSON_HOST_ATTRIBUTE) String host,
			@JsonProperty(JSON_PORT_ATTRIBUTE) int port,
			@JsonProperty(JSON_URIS_ATTRIBUTE) List<String> uris,
			@JsonProperty(JSON_APP_ATTRIBUTE) String app,
			@JsonProperty(JSON_PRIVATE_INSTANCE_ID_ATTRIBUTE) String privateInstanceId,
			@JsonProperty(JSON_INDEX_ATTRIBUTE) Integer index,
			@JsonProperty(JSON_DEA_ATTRIBUTE) String dea,
			@JsonProperty(JSON_TAGS_ATTRIBUTE) Map<String, String> tags) {
		super(host, port, uris, app, index, privateInstanceId, dea, tags);
	}

}
