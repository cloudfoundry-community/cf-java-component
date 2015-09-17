package cf.spring;

import cf.component.VarzProducer;
import cf.nats.CfNats;
import cf.nats.DefaultCfNats;
import cf.nats.Publication;
import cf.nats.PublicationHandler;
import cf.nats.RequestResponseHandler;
import cf.nats.message.ComponentAnnounce;
import cf.nats.message.ComponentDiscover;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import nats.client.MockNats;
import nats.client.Nats;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Mike Heath
 */
public class CfComponentTest {

	@EnableAutoConfiguration
	@Configuration
	@CfComponent(type = "Test")
	static class TestConfiguration {
		final Queue<ComponentAnnounce> componentAnnouncements = new LinkedList<>();

		@Bean
		Nats mockNats() {
			return new MockNats();
		}

		@Bean
		CfNats nats() {
			final DefaultCfNats nats = new DefaultCfNats(mockNats());
			nats.subscribe(ComponentAnnounce.class, new PublicationHandler<ComponentAnnounce, Void>() {
				@Override
				public void onMessage(Publication<ComponentAnnounce, Void> publication) {
					componentAnnouncements.add(publication.getMessageBody());
				}
			});
			return nats;
		}
	}

	@Test
	public void nats() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
			final ComponentAnnounce natsAnnouncement = getComponentAnnounce(context);
			assertNotNull(natsAnnouncement);
			assertEquals(natsAnnouncement.getType(), "Test");

			context.getBean(CfNats.class).request(new ComponentDiscover(), 1, TimeUnit.SECONDS, new RequestResponseHandler<ComponentAnnounce>() {
				@Override
				public void onResponse(Publication<ComponentAnnounce, Void> response) {
					assertEquals(response.getMessageBody().getType(), "Test");
				}
			});
		}
	}

	@Test
	public void publishesHealthz() throws Exception {
		final SpringApplication application = new SpringApplication(TestConfiguration.class);
		try (ConfigurableApplicationContext context = application.run()) {
			final ComponentAnnounce natsAnnouncement = getComponentAnnounce(context);
			final URL url = new URL("http://" + natsAnnouncement.getHost() + "/healthz");
			final InputStream in = (InputStream) url.getContent();
			final String content = new BufferedReader(new InputStreamReader(in)).readLine();
			assertEquals("ok", content);
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void varzRequiresAuthentication() throws Exception {
		final SpringApplication application = new SpringApplication(TestConfiguration.class);
		try (ConfigurableApplicationContext context = application.run()) {
			final ComponentAnnounce natsAnnouncement = getComponentAnnounce(context);
			new URL("http://foo:bar@" + natsAnnouncement.getHost() + "/varz").getContent();
			fail("Should have thrown authentication exception.");
		}
	}

	@Test
	public void varz() throws Exception {
		final SpringApplication application = new SpringApplication(TestConfiguration.class);
		try (ConfigurableApplicationContext context = application.run()) {
			final CfComponentConfiguration componentConfiguration = context.getBean(CfComponentConfiguration.class);
			final ComponentAnnounce natsAnnouncement = getComponentAnnounce(context);

			final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(componentConfiguration.getUsername(), componentConfiguration.getPassword()));
			HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
			HttpGet get = new HttpGet("http://" + natsAnnouncement.getHost() + "/varz");
			final HttpResponse response = client.execute(get);
			assertEquals(200, response.getStatusLine().getStatusCode());
			final JsonNode node = new ObjectMapper().readTree(response.getEntity().getContent());
			assertEquals(node.get("type").asText(), "Test");
			assertEquals(node.get("index").asInt(), componentConfiguration.getIndex());
			assertEquals(node.get("uuid").asText(), componentConfiguration.getUuid());
			assertTrue(node.has("num_cores"));
			assertTrue(node.has("num_cores"));
		}
	}

	@Configuration
	static class ExtraVarzConfiguration {
		@Bean
		VarzProducer extraProducer() {
			return new VarzProducer() {
				@Override
				public Map<String, JsonNode> produceVarz() {
					final Map<String, JsonNode> varz = new HashMap<>();
					varz.put("test", JsonNodeFactory.instance.textNode("value"));
					return varz;
				}
			};
		}
	}

	@Test
	public void extraVarz() throws Exception {
		final SpringApplication application = new SpringApplication(TestConfiguration.class, ExtraVarzConfiguration.class);
		try (ConfigurableApplicationContext context = application.run()) {
			final CfComponentConfiguration componentConfiguration = context.getBean(CfComponentConfiguration.class);
			final ComponentAnnounce natsAnnouncement = getComponentAnnounce(context);

			final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(componentConfiguration.getUsername(), componentConfiguration.getPassword()));
			HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
			HttpGet get = new HttpGet("http://" + natsAnnouncement.getHost() + "/varz");
			final HttpResponse response = client.execute(get);
			assertEquals(200, response.getStatusLine().getStatusCode());
			final JsonNode node = new ObjectMapper().readTree(response.getEntity().getContent());
			assertEquals(node.get("type").asText(), "Test");
			assertEquals(node.get("test").asText(), "value");
		}
	}

	private ComponentAnnounce getComponentAnnounce(ConfigurableApplicationContext context) {
		final TestConfiguration configuration = context.getBean(TestConfiguration.class);
		return configuration.componentAnnouncements.poll();
	}

}
