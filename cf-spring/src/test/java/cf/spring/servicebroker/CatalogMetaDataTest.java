package cf.spring.servicebroker;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Mike Heath
 */
public class CatalogMetaDataTest extends AbstractServiceBrokerTest {

	private static final String BROKER_ID = "my-cool-broker";
	private static final String BROKER_META_FIELD = "meta-field";
	private static final String BROKER_META_VALUE = "meta-value";

	private static final String DYNAMIC_FIELD = "SpEL";
	private static final String DYNAMIC_VALUE = "is cool.";

	private static final String PLAN_ID = "the-plan";
	private static final String PLAN_META_FIELD = "plan-meta-field";
	private static final String PLAN_META_VALUE = "meta-world-peace";

	@EnableAutoConfiguration
	@Configuration
	@EnableServiceBroker(username = USERNAME, password = PASSWORD)
	@ServiceBroker(@Service(id = BROKER_ID, name = "Service Brokers FTW", description = "Broker with metadata",
			metadata = @Metadata(field = BROKER_META_FIELD, value = BROKER_META_VALUE),
			plans = {
			@ServicePlan(id = PLAN_ID, name = "test", description = "Plan with metadata",
				metadata = @Metadata(field = PLAN_META_FIELD, value = PLAN_META_VALUE))
	}))
	static class ServiceBrokerSimpleMetaData {
		@Provision ProvisionResponse provision(ProvisionRequest request) { return null; }
		@Bind BindResponse bind(BindRequest request) { return null; }
	}

	@EnableAutoConfiguration
	@Configuration
	@EnableServiceBroker(username = USERNAME, password = PASSWORD)
	@ServiceBroker(@Service(id = BROKER_ID, name = "Service Brokers FTW", description = "Broker with metadata",
			metadata = @Metadata(field = "#{brokerField}", value = "#{dynamicValue}"),
			plans = {
			@ServicePlan(id = PLAN_ID, name = "test", description = "Plan with metadata",
				metadata = @Metadata(field = "#{planField}", value = "#{dynamicValue}"))
	}))
	static class ServiceBrokerSpelMetaData {
		@Provision ProvisionResponse provision(ProvisionRequest request) { return null; }
		@Bind BindResponse bind(BindRequest request) { return null; }

		@Bean
		public String brokerField() {
			return BROKER_META_FIELD;
		}

		@Bean
		public String planField() {
			return PLAN_META_FIELD;
		}

		@Bean
		Map<String, String> dynamicValue() {
			final Map<String, String> values = new HashMap<>();
			values.put(DYNAMIC_FIELD, DYNAMIC_VALUE);
			return values;
		}
	}

	@Test
	public void simpleMetaData() throws Exception {
		final SpringApplication application = new SpringApplication(ServiceBrokerSimpleMetaData.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = buildAuthenticatingClient()
		) {
			JsonNode catalog = loadCatalog(client);
			assertNotNull(catalog);

			assertTrue(catalog.has("services"));
			final JsonNode services = catalog.get("services");
			assertTrue(services.isArray());
			assertEquals(services.size(), 1);

			final JsonNode serviceBroker = services.get(0);

			assertTrue(serviceBroker.has("metadata"));
			final JsonNode brokerMetadata = serviceBroker.get("metadata");

			assertTrue(brokerMetadata.has(BROKER_META_FIELD));
			assertEquals(brokerMetadata.get(BROKER_META_FIELD).asText(), BROKER_META_VALUE);

			assertTrue(serviceBroker.has("plans"));
			final JsonNode plans = serviceBroker.get("plans");
			assertTrue(plans.isArray());
			assertEquals(plans.size(), 1);

			final JsonNode plan = plans.get(0);

			assertTrue(plan.has("metadata"));
			final JsonNode planMetadata = plan.get("metadata");
			assertTrue(planMetadata.has(PLAN_META_FIELD));
			assertEquals(planMetadata.get(PLAN_META_FIELD).asText(), PLAN_META_VALUE);
		}
	}

	@Test
	public void spelMetaData() throws Exception {
		final SpringApplication application = new SpringApplication(ServiceBrokerSpelMetaData.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = buildAuthenticatingClient()
		) {
			JsonNode catalog = loadCatalog(client);
			assertNotNull(catalog);

			assertTrue(catalog.has("services"));
			final JsonNode services = catalog.get("services");
			assertTrue(services.isArray());
			assertEquals(services.size(), 1);

			final JsonNode serviceBroker = services.get(0);

			assertTrue(serviceBroker.has("metadata"));
			final JsonNode brokerMetadata = serviceBroker.get("metadata");

			assertTrue(brokerMetadata.has(BROKER_META_FIELD));
			validateDynamicValue(brokerMetadata.get(BROKER_META_FIELD));

			assertTrue(serviceBroker.has("plans"));
			final JsonNode plans = serviceBroker.get("plans");
			assertTrue(plans.isArray());
			assertEquals(plans.size(), 1);

			final JsonNode plan = plans.get(0);

			assertTrue(plan.has("metadata"));
			final JsonNode planMetadata = plan.get("metadata");
			assertTrue(planMetadata.has(PLAN_META_FIELD));
			validateDynamicValue(planMetadata.get(PLAN_META_FIELD));
		}
	}

	private void validateDynamicValue(JsonNode value) {
		assertNotNull(value);
		assertTrue(value.has(DYNAMIC_FIELD));
		assertEquals(value.get(DYNAMIC_FIELD).asText(), DYNAMIC_VALUE);
	}
}
