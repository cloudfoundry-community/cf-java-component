package vcap.service.integration;

import static org.testng.Assert.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CloudControllerClient {

	private static final String V2_SERVICES = "/v2/services";
	private static final String V2_SERVICE_INSTANCES = "/v2/service_instances";
	private static final String V2_SERVICE_PLANS = "/v2/service_plans";
	private static final String V2_SERVICE_AUTH_TOKENS = "/v2/service_auth_tokens";

	private final ObjectMapper mapper = new ObjectMapper();
	private final HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());
	private final URI cloudControllerUri;
	private final String oauthToken;

	public CloudControllerClient(String cloudControllerUri, String oauthToken) {
		this.cloudControllerUri = URI.create(cloudControllerUri);
		this.oauthToken = oauthToken;
	}

	public String createService(CreateServiceRequest request) {
		try {
			final String requestString = mapper.writeValueAsString(request);
			final HttpPost post = new HttpPost(cloudControllerUri.resolve(V2_SERVICES));
			post.addHeader("Authorization", oauthToken);
			post.setEntity(new StringEntity(requestString, "application/json", "UTF-8"));
			final HttpResponse response = client.execute(post);
			assertEquals(response.getStatusLine().getStatusCode(), 201);
			final JsonNode json = mapper.readTree(readResponseBody(response));
			return json.get("metadata").get("guid").asText();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteService(String serviceGuid) {
		deleteUri(V2_SERVICES + "/" + serviceGuid);
	}

	public String createServicePlan(CreateServicePlanRequest request) {
		return postJsonToUri(request, V2_SERVICE_PLANS);
	}

	public String createAuthToken(CreateAuthTokenRequest request) {
		return postJsonToUri(request, V2_SERVICE_AUTH_TOKENS);
	}

	public void deleteAuthToken(String authTokenGuid) {
		deleteUri(V2_SERVICE_AUTH_TOKENS + "/" + authTokenGuid);
	}

	public String createServiceInstance(String name, String planGuid, String spaceGuid) {
		try {
			final ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("space_guid", spaceGuid);
			json.put("service_plan_guid", planGuid);
			final HttpPost post = new HttpPost(cloudControllerUri.resolve(V2_SERVICE_INSTANCES));
			post.addHeader("Authorization", oauthToken);
			post.setEntity(new StringEntity(json.toString(), "application/json", "UTF-8"));
			final HttpResponse response = client.execute(post);
			final String responseBody = readResponseBody(response);
			assertEquals(response.getStatusLine().getStatusCode(), 201, responseBody);
			final JsonNode jsonResponse = mapper.readTree(responseBody);
			return jsonResponse.get("metadata").get("guid").asText();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteServiceInstance(String instanceGuid) {
		deleteUri(V2_SERVICE_INSTANCES + "/" + instanceGuid);
	}

	private String postJsonToUri(Object json, String uri) {
		try {
			final String requestString = mapper.writeValueAsString(json);
			final HttpPost post = new HttpPost(cloudControllerUri.resolve(uri));
			post.addHeader("Authorization", oauthToken);
			post.setEntity(new StringEntity(requestString, "application/json", "UTF-8"));
			final HttpResponse response = client.execute(post);
			final String body = readResponseBody(response);
			assertEquals(response.getStatusLine().getStatusCode(), 201, body);
			final JsonNode responseJson = mapper.readTree(body);
			return responseJson.get("metadata").get("guid").asText();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteUri(String uri) {
		try {
			final HttpDelete delete = new HttpDelete(cloudControllerUri.resolve(uri));
			delete.addHeader("Authorization", oauthToken);
			final HttpResponse response = client.execute(delete);
			final String body = readResponseBody(response);
			assertEquals(response.getStatusLine().getStatusCode(), 204, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readResponseBody(HttpResponse response) {
		final HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		byte[] body = new byte[(int) entity.getContentLength()];
		try (InputStream in = entity.getContent()) {
			in.read(body);
			return new String(body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
