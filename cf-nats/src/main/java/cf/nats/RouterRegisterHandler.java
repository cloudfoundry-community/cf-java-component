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
package cf.nats;

import cf.nats.message.RouterGreet;
import nats.client.Registration;
import nats.client.Subscription;
import cf.nats.message.RouterRegister;
import cf.nats.message.RouterStart;
import cf.nats.message.RouterUnregister;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath
 */
public class RouterRegisterHandler implements AutoCloseable {

	private final CfNats cfNats;
	private final RouterRegister routerRegister;
	private final Subscription subscription;

	private long updateInterval = TimeUnit.SECONDS.toMillis(30);
	private Registration routerRegisterPublication;

	public RouterRegisterHandler(CfNats nats, String host, int port, String... uris) {
		this(nats, host, port, Arrays.asList(uris), null);
	}

	public RouterRegisterHandler(CfNats cfNats, String host, Integer port, List<String> uris, Map<String,String> tags) {
		this(cfNats, new RouterRegister(host, port, uris, null, null, null, null, tags));
	}

	public RouterRegisterHandler(final CfNats cfNats, final RouterRegister routerRegister) {
		this.cfNats = cfNats;
		this.routerRegister = routerRegister;

		cfNats.request(new RouterGreet(), 1, TimeUnit.MINUTES, new RequestResponseHandler<RouterStart>() {
			@Override
			public void onResponse(Publication<RouterStart, Void> response) {
				updateRouterRegisterInterval(response.getMessageBody());
			}
		});
		subscription = cfNats.subscribe(RouterStart.class, new PublicationHandler<RouterStart, Void>() {
					@Override
					public void onMessage(Publication<RouterStart, Void> publication) {
						updateRouterRegisterInterval(publication.getMessageBody());
						cfNats.publish(routerRegister);
					}
				});
		publish();
	}

	private void publish() {
		routerRegisterPublication = cfNats.publish(routerRegister, updateInterval, TimeUnit.MILLISECONDS);
	}

	private void updateRouterRegisterInterval(RouterStart routerStart) {
		if (routerStart.getMinimumRegisterIntervalInSeconds() == null) {
			return;
		}
		final long newInterval = TimeUnit.SECONDS.toMillis(routerStart.getMinimumRegisterIntervalInSeconds());
		if (newInterval < updateInterval) {
			if (routerRegisterPublication != null) {
				routerRegisterPublication.remove();
			}
			updateInterval = newInterval;
			publish();
		}
	}

	@Override
	public void close() {
		routerRegisterPublication.remove();
		cfNats.publish(RouterUnregister.toRouterUnregister(routerRegister));
		subscription.close();
	}
}
