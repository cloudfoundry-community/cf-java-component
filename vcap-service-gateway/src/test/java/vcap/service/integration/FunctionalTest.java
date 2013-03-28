package vcap.service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vcap.component.http.SimpleHttpServer;
import vcap.service.Gateway;

import static org.testng.Assert.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class FunctionalTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTest.class);

	private static final int serverPort = 4280;

	public static void main(String[] args) throws Exception {
		final CfUtil.HostToken target = CfUtil.getTargetToken();

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

		LOGGER.info("Using Cloud Controller at: {}", target.getHost());

		final String label = "testService" + ThreadLocalRandom.current().nextInt();
		final String provider = "Some Service Provider";
		final String url = "http://" + localIp(target.getHost()) + ":" + serverPort;
		final String description = "A service used for testing the service framework.";
		final String version = "0.1";

		final String servicePlan = "ServicePlan";
		final String servicePlanDescription = "Finest service... ever.";

		final String authToken = "SsshhhThisIsASecret";
		final TestProvisioner provisioner = new TestProvisioner();
		final CloudControllerClient cloudControllerClient = new CloudControllerClient(target.getHost(), target.getToken());
		try (
				final SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(serverPort))
			) {
			new Gateway(server, provisioner, authToken);

			final String serviceGuid = cloudControllerClient.createService(new CreateServiceRequest(
					label, provider, url, description, version
			));
			LOGGER.debug("Created service with guid: {}", serviceGuid);

			try {
				final String servicePlanGuid = cloudControllerClient.createServicePlan(new CreateServicePlanRequest(servicePlan, servicePlanDescription, serviceGuid));
				LOGGER.debug("Created service plan with guid: {}", serviceGuid);

				// Do we need to delete auth tokens explicitly?
				final String authTokenGuid = cloudControllerClient.createAuthToken(new CreateAuthTokenRequest(label, provider, authToken));
				LOGGER.debug("Created service token with guid: {}", authTokenGuid);
				try {
					final String instanceName = "myservice";
					final String serviceInstaceGuid = cloudControllerClient.createServiceInstance(instanceName, servicePlanGuid, target.getSpaceGuid());
					int instanceId = -1;
					try {
						assertEquals(1, provisioner.getCreateInvocationCount());
						instanceId = provisioner.getLastCreateId();
					} finally {
						cloudControllerClient.deleteServiceInstance(serviceInstaceGuid);
						assertEquals(provisioner.getDeleteInvocationCount(), 1);
						assertEquals(provisioner.getLastDeleteId(), instanceId);
					}
				} finally {
					cloudControllerClient.deleteAuthToken(authTokenGuid);
				}
			} finally {
				cloudControllerClient.deleteService(serviceGuid);
			}
		}

		// TODO: Create service pointing to test gateway
		// TODO: Create service instance and validate

	}

	private static Object collectionToString(final List<String> command) {
		return new Object() {
			@Override
			public String toString() {
				final StringBuilder builder = new StringBuilder();
				for (final Iterator<String> iterator = command.iterator(); iterator.hasNext();) {
					builder.append(iterator.next());
					if (iterator.hasNext()) {
						builder.append(' ');
					}
				}
				return builder.toString();
			}
		};
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
