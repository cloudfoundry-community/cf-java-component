package cf.spring;

import org.springframework.beans.factory.FactoryBean;
import cf.client.CloudController;
import cf.client.TokenProvider;
import cf.service.Bootstrap;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BootstrappingServiceGuidProvider implements FactoryBean<UUID> {

	public static class ServicePlan {
		private final String name;
		private final String description;

		public ServicePlan(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}

	private final CloudController cloudController;
	private final TokenProvider clientToken;
	private final String label;
	private final String provider;
	private final String version;
	private final String url;
	private final String description;
	private final String infoUrl;
	private final String authToken;
	private final List<ServicePlan> servicePlans;

	// TODO Determine if Spring will automatically convert String to URI instances.
	public BootstrappingServiceGuidProvider(CloudController cloudController, TokenProvider clientToken, String label, String provider, String version, String url, String description, String infoUrl, String authToken, List<ServicePlan> servicePlans) {
		this.cloudController = cloudController;
		this.clientToken = clientToken;
		this.label = label;
		this.provider = provider;
		this.version = version;
		this.url = url;
		this.description = description;
		this.infoUrl = infoUrl;
		this.authToken = authToken;
		this.servicePlans = servicePlans;
	}

	@Override
	public UUID getObject() {
		final Bootstrap bootstrap = new Bootstrap(cloudController, clientToken);
		final UUID serviceGuid = bootstrap.registerService(label, provider, version, URI.create(url), description, URI.create(infoUrl));

		for (ServicePlan servicePlan : servicePlans) {
			bootstrap.registerPlan(serviceGuid, servicePlan.name, servicePlan.description);
		}

		bootstrap.registerAuthToken(label, provider, authToken);

		return serviceGuid;
	}

	@Override
	public Class<?> getObjectType() {
		return UUID.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
