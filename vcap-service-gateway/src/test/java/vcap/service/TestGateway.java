package vcap.service;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vcap.client.CfTokens;
import vcap.client.CloudController;
import vcap.component.http.SimpleHttpServer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class TestGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestGateway.class);

	public static void main(String[] args) throws Exception {
		final CfTokens cfTokens = new CfTokens();
		final CfTokens.CfToken target = cfTokens.getTargetToken();

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

		final String label = "testgateway";
		final String provider = "Mike Heath";
		final String url = "http://" + localIp(target.getTarget()) + ":" + serverPort;
		final String description = "A service used for testing the service framework.";
		final String version = "0.1";

		final String servicePlan = "ServicePlan";
		final String servicePlanDescription = "Finest service... ever.";

		final String authToken = "SsshhhThisIsASecret";
		final CloudController cloudController = new CloudController(new DefaultHttpClient(), target.getTarget());

		try (
				final SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(serverPort))
			) {
			new Gateway(server, new Provisioner() {

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
				public Binding bind(BindRequest request) {
					LOGGER.info("Binding service");

					final Integer i = id.getAndIncrement();
					final Binding binding = new Binding(request.getServiceInstanceId(), i.toString());
					binding.addGatewayDataField("bindkey", "bind value");
					binding.addCredential("binduser", "test");
					return binding;
				}

				@Override
				public void unbind(String instanceId, String handleId) {
				}

				@Override
				public Iterable<String> services() {
					return null;
				}

				@Override
				public Iterable<String> handles(String instanceId) {
					return null;
				}

				@Override
				public void removeOrphanedBinding(String bindingId) {
				}

				@Override
				public void removeOrphanedService(String instanceId) {
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
