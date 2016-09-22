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

import cf.spring.servicebroker.Catalog.CatalogService;
import cf.spring.servicebroker.Catalog.Plan;
import cf.spring.servicebroker.ServiceBrokerHandler.ProvisionBody;
import cf.spring.servicebroker.ServiceBrokerHandler.UpdateBody;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Mike Heath
 */
public class ServiceBrokerTest extends AbstractServiceBrokerTest {

	private static final UUID ORG_GUID = UUID.randomUUID();
	private static final UUID SPACE_GUID = UUID.randomUUID();
	private static final UUID SERVICE_INSTANCE_GUID = UUID.randomUUID();
	private static final UUID APPLICATION_GUID = UUID.randomUUID();
	private static final String ROUTE = "www.google.com";
	private static final String ROUTE_SERVICE_URL = "https://someproxy.com";
	private static final UUID BINDING_GUID = UUID.randomUUID();

	private static final Map<String, Object> PARAMETERS;

	private static final String PARAM_KEY = "user_provided_param";
	private static final Object PARAM_VALUE = "finally";

	static {
		PARAMETERS = new HashMap<>();
		PARAMETERS.put(PARAM_KEY, PARAM_VALUE);
	}

	private static final String DASHBOARD_URL = "http:/some.url/yourservice/" + SERVICE_INSTANCE_GUID;

	private static final String BROKER_ID_STATIC = "some-broker-id-1";
	private static final String BROKER_ID_STATIC_OTHER = "some-broker-id-1-other";
	private static final String BROKER_ID_DYNAMIC = "some-broker-id-2";
	private static final String PLAN_ID = "plan-id-2";
	private static final String PLAN_ID_OTHER = "plan-id-2-other";

	private static final String SOME_USERNAME = "some-username";
	private static final String SOME_PASSWORD = "some-password";

	static class Credentials {
		private final String username;
		private final String password;

		Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableServiceBroker(username = USERNAME, password = PASSWORD)
	@ServiceBroker({
			@Service(id = BROKER_ID_STATIC, name="test-broker", description = "This is for testing", plans = {
					@ServicePlan(id = PLAN_ID, name = "test-plan", description = "Some test plan for testing.")
			}, requires=Permission.ROUTE_FORWARDING),
			@Service(id = BROKER_ID_STATIC_OTHER, name="test-broker-other", description = "This is for testing", plans = {
					@ServicePlan(id = PLAN_ID_OTHER, name = "test-plan-other", description = "Some test plan for testing.")
			})
	})
	static class ServiceBrokerConfiguration {

		@DynamicCatalog
		public Catalog getDynamicCatalog(){
			final Plan plan = new Plan(PLAN_ID, "test-plan", "Some test plan for testing.", true,
				  Collections.<String, Object>emptyMap());

			final CatalogService service = new CatalogService(BROKER_ID_DYNAMIC, "test-broker-dynamic",
				  "Dynamic service", true, Collections.<String>emptyList(), Collections.<String,
				  Object>emptyMap(), Collections.<String>emptyList(),  Collections.singletonList(plan), null);

			return new Catalog(Collections.singletonList(service));
		}

		@Provision
		public ProvisionResponse provision(ProvisionRequest request) {
			assertEquals(request.getInstanceGuid(), SERVICE_INSTANCE_GUID);
			assertEquals(request.getPlanId(), PLAN_ID);
			assertEquals(request.getOrganizationGuid(), ORG_GUID);
			assertEquals(request.getSpaceGuid(), SPACE_GUID);
			assertEquals(request.getParameters().size(), 1);
			assertEquals(request.getParameters().get(PARAM_KEY), PARAM_VALUE);
			provisionCounter().incrementAndGet();
			return new ProvisionResponse(DASHBOARD_URL);
		}

		@Update
		public void update(UpdateRequest request) {
			assertEquals(request.getPlanId(), PLAN_ID);
			assertEquals(request.getParameters().size(), 1);
			assertEquals(request.getParameters().get(PARAM_KEY), PARAM_VALUE);
			assertEquals(request.getPreviousValues().getOrganizationId(), ORG_GUID);
			assertEquals(request.getPreviousValues().getSpaceId(), SPACE_GUID);
			assertEquals(request.getPreviousValues().getPlanId(), PLAN_ID_OTHER);
			assertEquals(request.getPreviousValues().getServiceId(), BROKER_ID_STATIC_OTHER);
			updateCounter().incrementAndGet();
		}

		@Bind
		public BindResponse bind(BindRequest request) {
			assertEquals(request.getPlanId(), PLAN_ID);
			if(request.getBoundResource().getType() == BindRequest.BindingType.APPLICATION) {
				assertEquals(request.getApplicationGuid(), APPLICATION_GUID);
				assertEquals(request.getBoundResource().getResource(), APPLICATION_GUID.toString());
			}
			if(request.getBoundResource().getType() == BindRequest.BindingType.ROUTE) {
				assertEquals(request.getBoundResource().getResource(), ROUTE);
			}
			assertEquals(request.getBindingGuid(), BINDING_GUID);
			assertEquals(request.getServiceInstanceGuid(), SERVICE_INSTANCE_GUID);
			bindCounter().incrementAndGet();
			return new BindResponse(new Credentials(SOME_USERNAME, SOME_PASSWORD), null, ROUTE_SERVICE_URL, true);
		}

		@Unbind
		public void unbind(UnbindRequest request) {
			assertEquals(request.getPlanId(), PLAN_ID);
			assertEquals(request.getServiceInstanceGuid(), SERVICE_INSTANCE_GUID);
			assertEquals(request.getBindingGuid(), BINDING_GUID);
			unbindCounter().incrementAndGet();
		}

		@Deprovision
		public void deprovision(DeprovisionRequest request) {
			assertEquals(request.getPlanId(), PLAN_ID);
			assertEquals(request.getInstanceGuid(), SERVICE_INSTANCE_GUID);
			deprovisionCounter().incrementAndGet();
		}

		@Bean
		AtomicInteger provisionCounter() {
			return new AtomicInteger();
		}

		@Bean
		AtomicInteger updateCounter() {
			return new AtomicInteger();
		}

		@Bean
		AtomicInteger deprovisionCounter() {
			return new AtomicInteger();
		}

		@Bean
		AtomicInteger bindCounter() {
			return new AtomicInteger();
		}

		@Bean
		AtomicInteger unbindCounter() {
			return new AtomicInteger();
		}

	}

	private ConfigurableApplicationContext context;
	private CloseableHttpClient client;

	@BeforeClass
	public void init() {
		final SpringApplication application = new SpringApplication(ServiceBrokerConfiguration.class);
		context = application.run();
		client = buildAuthenticatingClient();
	}

	@AfterClass
	public void cleanup() throws Exception {
		context.close();
		client.close();
	}


	final String instanceUri = "http://localhost:8080/v2/service_instances/" + SERVICE_INSTANCE_GUID;
	final String bindingUri = "http://localhost:8080/v2/service_instances/" + SERVICE_INSTANCE_GUID + "/service_bindings/" + BINDING_GUID;

	@Test
	public void provisionStaticService() throws Exception {
		final ServiceBrokerHandler.ProvisionBody provisionBody
			  = new ServiceBrokerHandler.ProvisionBody(BROKER_ID_STATIC, PLAN_ID, ORG_GUID, SPACE_GUID, PARAMETERS);

		doProvisionTest(provisionBody);
	}

	@Test
	public void provisionDynamicService() throws Exception {
		final ServiceBrokerHandler.ProvisionBody provisionBody
			  = new ServiceBrokerHandler.ProvisionBody(BROKER_ID_DYNAMIC, PLAN_ID, ORG_GUID, SPACE_GUID, PARAMETERS);

		doProvisionTest(provisionBody);
	}

	@Test
	public void update() throws Exception {
		final AtomicInteger updateCounter = context.getBean("updateCounter", AtomicInteger.class);
		updateCounter.set(0);

		// Do update
		final ServiceBrokerHandler.UpdateBody updateBody
		  = new ServiceBrokerHandler.UpdateBody(BROKER_ID_STATIC, PLAN_ID, PARAMETERS,
				new ServiceBrokerHandler.UpdateBody.PreviousValues(BROKER_ID_STATIC_OTHER, PLAN_ID_OTHER, ORG_GUID, SPACE_GUID));

		final HttpUriRequest updateRequest = RequestBuilder.create(HttpPatch.METHOD_NAME)
			  .setUri(instanceUri)
			  .setEntity(new StringEntity(mapper.writeValueAsString(updateBody), ContentType.APPLICATION_JSON))
			  .build();
		try (final CloseableHttpResponse updateResponse = client.execute(updateRequest)) {
			assertEquals(updateResponse.getStatusLine().getStatusCode(), 200);
			assertEquals(updateCounter.get(), 1);
		}
	}

	@Test
	public void bindApplication() throws Exception {
		final AtomicInteger bindCounter = context.getBean("bindCounter", AtomicInteger.class);
		bindCounter.set(0);
		// Do bind
		final ServiceBrokerHandler.BindBody bindBody = new ServiceBrokerHandler.BindBody(BROKER_ID_STATIC, PLAN_ID, APPLICATION_GUID, new ServiceBrokerHandler.BindResource(APPLICATION_GUID.toString(), null), Collections.emptyMap());
		final HttpUriRequest bindRequest = RequestBuilder.put()
				.setUri(bindingUri)
				.setEntity(new StringEntity(mapper.writeValueAsString(bindBody), ContentType.APPLICATION_JSON))
				.build();
		try(final CloseableHttpResponse bindResponse = client.execute(bindRequest)) {
			assertEquals(bindResponse.getStatusLine().getStatusCode(), 201);
			assertEquals(bindCounter.get(), 1);
			final JsonNode bindResponseJson = mapper.readTree(bindResponse.getEntity().getContent());
			assertTrue(bindResponseJson.has("credentials"));
			assertFalse(bindResponseJson.has("syslog_drain_url"));

			final JsonNode credentials = bindResponseJson.get("credentials");
			assertEquals(credentials.get("username").asText(), SOME_USERNAME);
			assertEquals(credentials.get("password").asText(), SOME_PASSWORD);
		}
	}

	@Test
	public void bindRoute() throws Exception {
		final AtomicInteger bindCounter = context.getBean("bindCounter", AtomicInteger.class);
		bindCounter.set(0);
		// Do bind
		final ServiceBrokerHandler.BindBody bindBody = new ServiceBrokerHandler.BindBody(BROKER_ID_STATIC, PLAN_ID, null, new ServiceBrokerHandler.BindResource(null, ROUTE), Collections.emptyMap());
		final HttpUriRequest bindRequest = RequestBuilder.put()
				.setUri(bindingUri)
				.setEntity(new StringEntity(mapper.writeValueAsString(bindBody), ContentType.APPLICATION_JSON))
				.build();
		try (final CloseableHttpResponse bindResponse = client.execute(bindRequest)) {
			assertEquals(bindResponse.getStatusLine().getStatusCode(), 201);
			assertEquals(bindCounter.get(), 1);
			final JsonNode bindResponseJson = mapper.readTree(bindResponse.getEntity().getContent());
			assertTrue(bindResponseJson.has("credentials"));
			assertFalse(bindResponseJson.has("syslog_drain_url"));
			assertEquals(bindResponseJson.get("route_service_url").asText(), ROUTE_SERVICE_URL);
		}
	}

	@Test
	public void unbind() throws Exception {
		final AtomicInteger unbindCounter = context.getBean("unbindCounter", AtomicInteger.class);
		assertEquals(unbindCounter.get(), 0);

		final HttpUriRequest unbindRequest = RequestBuilder.delete()
				.setUri(bindingUri + "?service_id=" + BROKER_ID_STATIC + "&" + "plan_id=" + PLAN_ID)
				.build();
		try (final CloseableHttpResponse unbindResponse = client.execute(unbindRequest)) {
			assertEquals(unbindResponse.getStatusLine().getStatusCode(), 200);
			assertEquals(unbindCounter.get(), 1);
		}
	}

	// Do unbind
	@Test
	public void deprovision() throws Exception {
		final AtomicInteger deprovisionCounter = context.getBean("deprovisionCounter", AtomicInteger.class);
		assertEquals(deprovisionCounter.get(), 0);

		// Do deprovision
		final HttpUriRequest deprovisionRequest = RequestBuilder.delete()
				.setUri(instanceUri + "?service_id=" + BROKER_ID_STATIC + "&" + "plan_id=" + PLAN_ID)
				.build();
		try (final CloseableHttpResponse deprovisionResponse = client.execute(deprovisionRequest)) {
			assertEquals(deprovisionResponse.getStatusLine().getStatusCode(), 200);
			assertEquals(deprovisionCounter.get(), 1);
		}
	}

	@Test
	public void errorWhenCallingUnknownService() throws Exception {
		final ServiceBrokerHandler.ProvisionBody provisionBody = new ServiceBrokerHandler.ProvisionBody("invalid-broker-id", PLAN_ID, ORG_GUID, SPACE_GUID, Collections.emptyMap());
		final HttpUriRequest provisionRequest = RequestBuilder.put()
				.setUri(instanceUri)
				.setEntity(new StringEntity(mapper.writeValueAsString(provisionBody), ContentType.APPLICATION_JSON))
				.build();
		try (final CloseableHttpResponse provisionResponse = client.execute(provisionRequest)) {
			assertEquals(provisionResponse.getStatusLine().getStatusCode(), 404);
			final JsonNode errorJson = mapper.readTree(provisionResponse.getEntity().getContent());
			assertTrue(errorJson.has("description"));
		}
	}

	private void doProvisionTest(ProvisionBody provisionBody) throws IOException {
		final AtomicInteger provisionCounter = context.getBean("provisionCounter", AtomicInteger.class);
		provisionCounter.set(0);

		// Do provision
		final HttpUriRequest provisionRequest = RequestBuilder.put()
			  .setUri(instanceUri)
			  .setEntity(new StringEntity(mapper.writeValueAsString(provisionBody), ContentType.APPLICATION_JSON))
			  .build();
		try (final CloseableHttpResponse provisionResponse = client.execute(provisionRequest)) {
			assertEquals(provisionResponse.getStatusLine().getStatusCode(), 201);
			assertEquals(provisionCounter.get(), 1);

			final JsonNode provisionResponseJson = mapper.readTree(provisionResponse.getEntity().getContent());
			assertTrue(provisionResponseJson.has("dashboard_url"));
			assertEquals(provisionResponseJson.get("dashboard_url").asText(), DASHBOARD_URL);
		}
	}

}
