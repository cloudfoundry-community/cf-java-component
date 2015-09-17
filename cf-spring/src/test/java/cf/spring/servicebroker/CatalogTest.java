/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Mike Heath
 */
public class CatalogTest extends AbstractServiceBrokerTest {

	@EnableAutoConfiguration
	@Configuration
	@EnableServiceBroker(username = USERNAME, password = PASSWORD)
	static class EmptyServiceBrokerCatalog {
	}

	private static final String BROKER_ID = "1";
	private static final String BROKER_NAME = "testbroker";
	private static final String BROKER_DESCRIPTION = "This is one heck of a cool broker";

	@Component
	@ServiceBroker(@Service(id = BROKER_ID, name = BROKER_NAME, description = BROKER_DESCRIPTION, bindable = "false", plans = {}))
	static class EmptyPlanServiceBrokerConfiguration {
		@Provision ProvisionResponse provision(ProvisionRequest request) { return null; }
	}

	public static final String PLAN_ID = "plan-1";
	public static final String PLAN_NAME = "plan name";
	public static final String PLAN_DESCRIPTION = "This is the plan.";

	@Component
	@ServiceBroker(@Service(id = BROKER_ID, name = BROKER_NAME, description = BROKER_DESCRIPTION, plans = {
			@ServicePlan(id = PLAN_ID, name = PLAN_NAME, description = PLAN_DESCRIPTION)
	}))
	static class ServiceBrokerSinglePlanConfiguration {
		@Provision ProvisionResponse provision(ProvisionRequest request) { return null; }
		@Bind BindResponse bind(BindRequest request) { return null; }
	}

	/*
	 * A request to the catalog with invalid credentials MUST return a 401.
	 */
	@Test
	public void badAuthentication() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = HttpClients.createDefault()
		) {
			final HttpUriRequest catalogRequest = RequestBuilder.get()
					.setUri("http://localhost:8080" + Constants.CATALOG_URI)
					.build();
			final CloseableHttpResponse response = client.execute(catalogRequest);
			assertEquals(response.getStatusLine().getStatusCode(), 401);
		}
	}

	@Test
	public void postToCatalog() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = HttpClients.createDefault()
		) {
			final HttpUriRequest catalogRequest = RequestBuilder.post()
					.setUri("http://localhost:8080" + Constants.CATALOG_URI)
					.build();
			final CloseableHttpResponse response = client.execute(catalogRequest);
			assertEquals(response.getStatusLine().getStatusCode(), 405);
		}
	}

	@Test
	public void goodAuthentication() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = buildAuthenticatingClient()
		) {
			final HttpUriRequest catalogRequest = RequestBuilder.get()
					.setUri("http://localhost:8080" + Constants.CATALOG_URI)
					.build();
			final CloseableHttpResponse response = client.execute(catalogRequest);
			assertEquals(response.getStatusLine().getStatusCode(), 200);
		}
	}

	private static final String CONFIGURABLE_USERNAME = "configurable-username";
	private static final String CONFIGURABLE_PASSWORD = "configurable-password";

	@EnableAutoConfiguration
	@Configuration
	@EnableServiceBroker(username = "#{username}", password = "#{password}")
	static class SpelServiceBrokerCatalog {

		@Bean
		String username() { return CONFIGURABLE_USERNAME; }

		@Bean
		String password() { return CONFIGURABLE_PASSWORD; }

	}

	@Test
	public void spelCatalogCredentials() throws Exception {
		final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(CONFIGURABLE_USERNAME, CONFIGURABLE_PASSWORD));
		final SpringApplication application = new SpringApplication(SpelServiceBrokerCatalog.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
		) {
			final HttpUriRequest catalogRequest = RequestBuilder.get()
					.setUri("http://localhost:8080" + Constants.CATALOG_URI)
					.build();
			final CloseableHttpResponse response = client.execute(catalogRequest);
			assertEquals(response.getStatusLine().getStatusCode(), 200);
		}
	}

	@Test
	public void emptyCatalog() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class);
		try (
				ConfigurableApplicationContext context = application.run();
				CloseableHttpClient client = buildAuthenticatingClient()
		) {
			JsonNode catalog = loadCatalog(client);
			assertNotNull(catalog);
			assertTrue(catalog.has("services"));
			final JsonNode services = catalog.get("services");
			assertTrue(services.isArray());
			assertEquals(services.size(), 0);
		}
	}

	@Test
	public void serviceBrokerWithNoPlansInCatalog() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class, EmptyPlanServiceBrokerConfiguration.class);
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

			// Required "id" field
			assertTrue(serviceBroker.has("id"));
			assertEquals(serviceBroker.get("id").asText(), BROKER_ID);

			// Required "name" field
			assertTrue(serviceBroker.has("name"));
			assertEquals(serviceBroker.get("name").asText(), BROKER_NAME);

			// Required "description" field
			assertTrue(serviceBroker.has("description"));
			assertEquals(serviceBroker.get("description").asText(), BROKER_DESCRIPTION);

			// Required "bindable" field
			assertTrue(serviceBroker.has("bindable"));
			assertFalse(serviceBroker.get("bindable").asBoolean());

			// Required "plans" array field
			assertTrue(serviceBroker.has("plans"));
			final JsonNode plans = serviceBroker.get("plans");
			assertTrue(plans.isArray());
			assertEquals(plans.size(), 0);
		}
	}

	@Test
	public void serviceBrokerWithPlansInCatalog() throws Exception {
		final SpringApplication application = new SpringApplication(EmptyServiceBrokerCatalog.class, ServiceBrokerSinglePlanConfiguration.class);
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

			assertTrue(serviceBroker.has("plans"));
			final JsonNode plans = serviceBroker.get("plans");
			assertTrue(plans.isArray());
			assertEquals(plans.size(), 1);

			final JsonNode plan = plans.get(0);
			assertTrue(plan.has("id"));
			assertEquals(plan.get("id").asText(), PLAN_ID);
			assertTrue(plan.has("name"));
			assertEquals(plan.get("name").asText(), PLAN_NAME);
			assertTrue(plan.has("description"));
			assertEquals(plan.get("description").asText(), PLAN_DESCRIPTION);
			assertTrue(plan.has("free"));
			assertTrue(plan.get("free").asBoolean());
		}
	}

}
