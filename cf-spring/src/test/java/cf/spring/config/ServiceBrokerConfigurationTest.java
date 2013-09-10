package cf.spring.config;

import cf.service.BindRequest;
import cf.service.CreateRequest;
import cf.service.Provisioner;
import cf.service.ServiceBinding;
import cf.service.ServiceInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBrokerConfigurationTest {

	public void brokerAnnotationConfiguration() throws Exception {
		try (final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) new SpringApplication(Config.class, TestProvisioner.class).run()) {
			Thread.sleep(30000);
		}
	}

	@Configuration
	@ServiceBroker
	@EnableAutoConfiguration
	public static class Config {
	}

	@Service("provisioner")
	public static class TestProvisioner implements Provisioner {

		@Override
		public ServiceInstance create(CreateRequest request) {
			return null;
		}

		@Override
		public void delete(String instanceId) {

		}

		@Override
		public ServiceBinding bind(BindRequest request) {
			return null;
		}

		@Override
		public void unbind(String instanceId, String bindingId) {

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
	}

}
