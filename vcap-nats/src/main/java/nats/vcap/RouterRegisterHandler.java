/*
 *   Copyright (c) 2013 Mike Heath.  All rights reserved.
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
package nats.vcap;

import nats.client.Subscription;
import nats.vcap.message.RouterRegister;
import nats.vcap.message.RouterStart;
import nats.vcap.message.RouterUnregister;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class RouterRegisterHandler implements AutoCloseable {

	private final NatsVcap natsVcap;
	private final RouterRegister routerRegister;
	private final Subscription subscription;

	public RouterRegisterHandler(NatsVcap natsVcap, String host, Integer port, List<String> uris, Map<String,String> tags) {
		this(natsVcap, new RouterRegister(host, port, null, null, uris, tags));
	}

	public RouterRegisterHandler(final NatsVcap natsVcap, final RouterRegister routerRegister) {
		this.natsVcap = natsVcap;
		this.routerRegister = routerRegister;

		natsVcap.publish(routerRegister);
		subscription = natsVcap.subscribe(RouterStart.class, new VcapPublicationHandler<RouterStart, Void>() {
					@Override
					public void onMessage(VcapPublication<RouterStart, Void> publication) {
						natsVcap.publish(routerRegister);
					}
				});
	}

	@Override
	public void close() {
		natsVcap.publish(new RouterUnregister(routerRegister));
		subscription.close();
	}
}
