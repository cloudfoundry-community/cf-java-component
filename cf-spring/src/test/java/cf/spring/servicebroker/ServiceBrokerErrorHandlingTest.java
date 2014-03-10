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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.AssertJUnit.*;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBrokerErrorHandlingTest extends AbstractServiceBrokerTest {

	public static final String BROKER_ID = "something-something-broker-id-something";
	private static final String PLAN_ID = "this-plan-id-is-the-best";

	public static final int HTTP_RESPONSE_CODE = 412;
	public static final String HTTP_RESPONSE_MESSAGE = "Dude, you're doing it wrong.";
	private static final String BINDING_ERROR = "An unexpected error occurred";

	@Configuration
	@EnableAutoConfiguration
	@EnableServiceBroker(username = USERNAME, password = PASSWORD)
	@ServiceBroker(
			@Service(id = BROKER_ID, name="test-broker", description = "This is for testing", plans = {
					@ServicePlan(id = PLAN_ID, name = "test-plan", description = "Some test plan for testing.")
	}))
	static class ServiceBrokerConfiguration {

		@Provision
		public ProvisionResponse provision(ProvisionRequest request) {
			throw new ServiceBrokerException(HTTP_RESPONSE_CODE, HTTP_RESPONSE_MESSAGE);
		}

		@Bind
		public BindResponse bind(BindRequest bind) {
			throw new RuntimeException(BINDING_ERROR);
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


	@Test
	public void returnsErrorMessage() throws Exception {
		final ServiceBrokerHandler.ProvisionBody provisionBody = new ServiceBrokerHandler.ProvisionBody(BROKER_ID, PLAN_ID, UUID.randomUUID(), UUID.randomUUID());
		final HttpUriRequest provisionRequest = RequestBuilder.put()
				.setUri("http://localhost:8080/v2/service_instances/" + UUID.randomUUID())
				.setEntity(new StringEntity(mapper.writeValueAsString(provisionBody), ContentType.APPLICATION_JSON))
				.build();
		final CloseableHttpResponse provisionResponse = client.execute(provisionRequest);
		assertEquals(provisionResponse.getStatusLine().getStatusCode(), HTTP_RESPONSE_CODE);
		final JsonNode responseJson = mapper.readTree(provisionResponse.getEntity().getContent());

		assertTrue(responseJson.has("description"));
		assertEquals(responseJson.get("description").asText(), HTTP_RESPONSE_MESSAGE);
	}

	@Test
	public void handlesUnexpectedException() throws Exception {
		final ServiceBrokerHandler.BindBody bindBody = new ServiceBrokerHandler.BindBody(BROKER_ID, PLAN_ID, UUID.randomUUID());
		final HttpUriRequest bindRequest = RequestBuilder.put()
				.setUri("http://localhost:8080/v2/service_instances/" + UUID.randomUUID() + "/service_bindings/" + UUID.randomUUID())
				.setEntity(new StringEntity(mapper.writeValueAsString(bindBody), ContentType.APPLICATION_JSON))
				.build();
		final CloseableHttpResponse bindResponse = client.execute(bindRequest);
		assertEquals(bindResponse.getStatusLine().getStatusCode(), 500);
		final JsonNode responseJson = mapper.readTree(bindResponse.getEntity().getContent());

		assertTrue(responseJson.has("description"));
		assertEquals(responseJson.get("description").asText(), BINDING_ERROR);
	}
}
