package cf.spring;

import org.springframework.beans.factory.FactoryBean;

import java.util.UUID;

/**
 * @author Mike Heath
 */
public class DefaultServiceGuidFactoryBean implements FactoryBean<UUID> {

	private final UUID serviceGuid;

	public DefaultServiceGuidFactoryBean(UUID serviceGuid) {
		this.serviceGuid = serviceGuid;
	}

	@Override
	public UUID getObject() throws Exception {
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
