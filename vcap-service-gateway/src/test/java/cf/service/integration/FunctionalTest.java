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
package cf.service.integration;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cf.client.CloudController;
import cf.client.DefaultCloudController;
import cf.client.CfTokens;
import cf.client.model.Service;
import cf.client.model.ServiceAuthToken;
import cf.client.model.ServicePlan;
import cf.component.http.SimpleHttpServer;
import cf.service.GatewayServer;

import static org.testng.Assert.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class FunctionalTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTest.class);

	private static final int serverPort = 4280;

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

		final String label = "testService" + ThreadLocalRandom.current().nextInt();
		final String provider = "core";
		final URI url = URI.create("http://" + localIp(target.getTarget()) + ":" + serverPort);
		final String description = "A service used for testing the service framework.";
		final String version = "0.1";

		final String servicePlan = "ServicePlan";
		final String servicePlanDescription = "Finest service... ever.";

		final String authToken = "SsshhhThisIsASecret";
		final CloudController cloudControllerClient = new DefaultCloudController(new DefaultHttpClient(), target.getTarget());
		try (
				final SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(serverPort))
			) {

			final UUID serviceGuid = cloudControllerClient.createService(target.getToken(), new Service(
					label, provider, url, description, version, null, true, null
			));
			LOGGER.debug("Created service with guid: {}", serviceGuid);

			final TestProvisioner provisioner = new TestProvisioner();
			new GatewayServer(server, provisioner, authToken);

			try {
				final UUID servicePlanGuid = cloudControllerClient.createServicePlan(target.getToken(), new ServicePlan(servicePlan, servicePlanDescription, serviceGuid, true, null));
				LOGGER.debug("Created service plan with guid: {}", serviceGuid);

				final UUID authTokenGuid = cloudControllerClient.createAuthToken(target.getToken(), new ServiceAuthToken(label, provider, authToken));
				LOGGER.debug("Created service token with guid: {}", authTokenGuid);
				try {
					final String instanceName = "myservice";
					final UUID serviceInstanceGuid = cloudControllerClient.createServiceInstance(target.getToken(), instanceName, servicePlanGuid, target.getSpaceGuid());
					int instanceId = -1;
					try {
						assertEquals(1, provisioner.getCreateInvocationCount());
						instanceId = provisioner.getLastCreateId();
					} finally {
						cloudControllerClient.deleteServiceInstance(target.getToken(), serviceInstanceGuid);
						assertEquals(provisioner.getDeleteInvocationCount(), 1);
						assertEquals(provisioner.getLastDeleteId(), instanceId);
					}
				} finally {
					cloudControllerClient.deleteServiceAuthToken(target.getToken(), authTokenGuid);
				}
			} finally {
				cloudControllerClient.deleteService(target.getToken(), serviceGuid);
			}
		}

		// TODO: Create service pointing to test gateway
		// TODO: Create service instance and validate

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
