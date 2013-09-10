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
package cf.service;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cf.client.CfTokens;
import cf.client.CloudController;
import cf.client.DefaultCloudController;
import cf.component.http.SimpleHttpServer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used for reverse engineering REST calls from Cloud Controller.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class TestBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestBroker.class);

	public static void main(String[] args) throws Exception {
		final CfTokens cfTokens = new CfTokens();
		final CfTokens.CfToken target = cfTokens.getCurrentTargetToken();

		if (target == null) {
			System.err.println("It appears you haven't logged into a Cloud Foundry instance with cf.");
			return;
		}
		if (target.getVersion() == null || target.getVersion() != 2) {
			System.err.println("You must target a v2 Cloud Controller using cf.");
			return;
		}
		if (target.getSpaceGuid() == null) {
			System.err.println("You must select a space to use using cf.");
			return;
		}

		LOGGER.info("Using Cloud Controller at: {}", target.getTarget());

		final int serverPort = 8000;

		final String label = "testbroker";
		final String provider = "Tester";
		final String url = "http://" + localIp(target.getTarget()) + ":" + serverPort;
		final String description = "A service used for testing the service framework.";
		final String version = "0.1";

		final String servicePlan = "ServicePlan";
		final String servicePlanDescription = "Finest service... ever.";

		final String authToken = "SsshhhThisIsASecret";
		final CloudController cloudController = new DefaultCloudController(new DefaultHttpClient(), target.getTarget());

			final UUID serviceGuid = UUID.randomUUID(); // We need to keep track of the services GUID.
//			final String serviceGuid = cloudControllerClient.createService(new CreateServiceRequest(
//					label, provider, url, description, version
//			));
//			LOGGER.debug("Created service with guid: {}", serviceGuid);
//

		try (
				final SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(serverPort))
			) {
			new NettyBrokerServer(server, new Provisioner() {

				private final AtomicInteger id = new AtomicInteger();

				@Override
				public ServiceInstance create(CreateRequest request) {
					LOGGER.info("Creating service");

					final Integer i = id.getAndIncrement();
					final ServiceInstance serviceInstance = new ServiceInstance(i.toString());
					serviceInstance.addGatewayDataField("key", "value");
					serviceInstance.addCredential("user", "test");
					return serviceInstance;
				}

				@Override
				public void delete(String instanceId) {
				}

				@Override
				public ServiceBinding bind(BindRequest request) {
					LOGGER.info("Binding service");

					final Integer i = id.getAndIncrement();
					final ServiceBinding serviceBinding = new ServiceBinding(request.getServiceInstanceId(), i.toString());
					serviceBinding.addGatewayDataField("bindkey", "bind value");
					serviceBinding.addCredential("binduser", "test");
					return serviceBinding;
				}

				@Override
				public void unbind(String instanceId, String handleId) {
				}

				@Override
				public Iterable<String> serviceInstanceIds() {
					return null;
				}

				@Override
				public Iterable<String> bindingIds(String instanceId) {
					return null;
				}

				@Override
				public void removeOrphanedBinding(String instanceId, String bindingId) {
				}

				@Override
				public void removeOrphanedServiceInstance(String instanceId) {
				}
			}, authToken);

//			final String serviceGuid = cloudControllerClient.createService(new CreateServiceRequest(
//					label, provider, url, description, version
//			));
//			LOGGER.debug("Created service with guid: {}", serviceGuid);
//
//			final String servicePlanGuid = cloudControllerClient.createServicePlan(new CreateServicePlanRequest(servicePlan, servicePlanDescription, serviceGuid));
//			LOGGER.debug("Created service plan with guid: {}", serviceGuid);
//
//			final String authTokenGuid = cloudControllerClient.createAuthToken(new CreateAuthTokenRequest(label, provider, authToken));
//			LOGGER.debug("Created service token with guid: {}", authTokenGuid);
//
//			final String instanceName = "testservice";
//			final String serviceInstaceGuid = cloudControllerClient.createServiceInstance(instanceName, servicePlanGuid, target.getSpaceGuid());

			System.in.read();
		}
	}

	private static String localIp(String cloudControllerUri) {
		final URI uri = URI.create(cloudControllerUri);
		final int port = uri.getPort() == -1 ? 80 : uri.getPort();
		try (Socket socket = new Socket(uri.getHost(), port)) {
			return socket.getLocalAddress().getHostAddress();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
