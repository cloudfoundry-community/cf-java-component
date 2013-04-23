package vcap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vcap.client.CloudController;
import vcap.client.Resource;
import vcap.client.RestCollection;
import vcap.client.Token;
import vcap.client.model.Service;
import vcap.client.model.ServiceAuthToken;
import vcap.client.model.ServicePlan;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Bootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

	private final CloudController cloudController;
	private final Token clientToken;

	public Bootstrap(CloudController cloudController, Token clientToken) {
		this.cloudController = cloudController;
		this.clientToken = clientToken;
	}

	/**
	 * Check to see if the service is already registered with the Cloud Controller, if not the service is created.
	 *
	 * @return the guid of the service
	 */
	// TODO It would be nice to update the service in case the URL, description, etc change.
	public UUID registerService(String label, String provider, String version, String url, String description, String infoUrl) {
		final RestCollection<Service> services = cloudController.getServices(clientToken);
		for (Resource<Service> service : services) {
			final Service serviceEntity = service.getEntity();
			if (label.equals(serviceEntity.getLabel())
					&& provider.equals(serviceEntity.getProvider())
					&& version.equals(serviceEntity.getVersion())) {
				final UUID serviceGuid = service.getGuid();
				LOGGER.debug("Using existing service with guid {}", serviceGuid);
				return serviceGuid;
			}
		}

		LOGGER.info("Registering service with Cloud Controller");
		final Service service = new Service(label, provider, url, description, version, infoUrl, true, null);
		return cloudController.createService(clientToken, service);
	}

	public UUID registerPlan(UUID serviceGuid, String planName, String planDescription) {
		final RestCollection<ServicePlan> servicePlans = cloudController.getServicePlans(clientToken, CloudController.ServicePlanQueryAttribute.SERVICE_GUID, serviceGuid.toString());
		for (Resource<ServicePlan> servicePlan : servicePlans) {
			final ServicePlan servicePlanEntity = servicePlan.getEntity();
			if (servicePlanEntity.getName().equals(planName)) {
				final UUID servicePlanGuid = servicePlan.getGuid();
				LOGGER.debug("Using existing service plan with name {} and guid {}", planName, servicePlanGuid);
				return servicePlanGuid;
			}
		}
		LOGGER.info("Registering service plan {} with Cloud Controller", planName);
		final ServicePlan servicePlan = new ServicePlan(planName, planDescription, serviceGuid.toString(), true, null);
		return cloudController.createServicePlan(clientToken, servicePlan);
	}

	public UUID registerAuthToken(String label, String provider, String authToken) {
		final RestCollection<ServiceAuthToken> authTokens = cloudController.getAuthTokens(clientToken);
		for (Resource<ServiceAuthToken> serviceAuthToken : authTokens) {
			final ServiceAuthToken authTokenEntity = serviceAuthToken.getEntity();
			if (authTokenEntity.getLabel().equals(label) && authTokenEntity.getProvider().equals(provider)) {
				LOGGER.debug("Found service authentication token on Cloud Controller");
				return serviceAuthToken.getGuid();
			}
		}
		LOGGER.info("Registering service authentication token with Cloud Controller");
		final ServiceAuthToken serviceAuthToken = new ServiceAuthToken(label, provider, authToken);
		return cloudController.createAuthToken(clientToken, serviceAuthToken);
	}
}
