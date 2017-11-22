package cf.spring.servicebroker;

import org.apache.http.HttpResponse;
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
import org.springframework.util.StreamUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

/**
 * @author Mike Heath
 */
public class ConflictTest extends AbstractServiceBrokerTest {
	public static final String BROKER_ID = "a-broker-that-always-has-conflicts";
	private static final String PLAN_ID = "bad-plan";
	private static final String EXPECTED_CONFLICT_BODY = "{}";

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
			throw new ConflictException();
		}

		@Bind
		public BindResponse bind(BindRequest bind) {
			throw new ConflictException();
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
	public void provisionConflict() throws Exception {
		final ServiceBrokerHandler.ProvisionBody provisionBody = new ServiceBrokerHandler.ProvisionBody(BROKER_ID, PLAN_ID, UUID.randomUUID(), UUID.randomUUID(), Collections.emptyMap());
		final HttpUriRequest provisionRequest = RequestBuilder.put()
				.setUri("http://localhost:8080/v2/service_instances/" + UUID.randomUUID())
				.setEntity(new StringEntity(mapper.writeValueAsString(provisionBody), ContentType.APPLICATION_JSON))
				.build();
		try (final CloseableHttpResponse provisionResponse = client.execute(provisionRequest)) {
			assertEquals(provisionResponse.getStatusLine().getStatusCode(), 409);
			final String body = readBody(provisionResponse);
			assertEquals(body, EXPECTED_CONFLICT_BODY);
		}
	}

	@Test
	public void bindConflict() throws Exception {
		final UUID applicationGuid = UUID.randomUUID();
		final ServiceBrokerHandler.BindBody bindBody = new ServiceBrokerHandler.BindBody(BROKER_ID, PLAN_ID, applicationGuid, new ServiceBrokerHandler.BindResource(applicationGuid.toString(), null, null), Collections.emptyMap());
		final HttpUriRequest provisionRequest = RequestBuilder.put()
				.setUri("http://localhost:8080/v2/service_instances/" + UUID.randomUUID() + "/service_bindings/" + UUID.randomUUID())
				.setEntity(new StringEntity(mapper.writeValueAsString(bindBody), ContentType.APPLICATION_JSON))
				.build();
		try (final CloseableHttpResponse bindResponse = client.execute(provisionRequest)) {
			assertEquals(bindResponse.getStatusLine().getStatusCode(), 409);
			final String body = readBody(bindResponse);
			assertEquals(body, EXPECTED_CONFLICT_BODY);
		}
	}

	public String readBody(HttpResponse response) throws IOException {
		return StreamUtils.copyToString(response.getEntity().getContent(), StandardCharsets.UTF_8);
	}

}
