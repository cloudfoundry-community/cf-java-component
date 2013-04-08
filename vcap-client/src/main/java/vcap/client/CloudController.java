package vcap.client;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vcap.client.model.Info;
import vcap.client.model.Service;
import vcap.client.model.ServiceAuthToken;
import vcap.client.model.ServicePlan;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
// TODO Add getter methods
public class CloudController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudController.class);

	private static final String V2_SERVICES = "/v2/services";
	private static final String V2_SERVICE_INSTANCES = "/v2/service_instances";
	private static final String V2_SERVICE_PLANS = "/v2/service_plans";
	private static final String V2_SERVICE_AUTH_TOKENS = "/v2/service_auth_tokens";

	private final HttpClient httpClient;

	private final URI target;

	private final ObjectMapper mapper;

	private final Object lock = new Object();

	// Access to the following fields needs to be done holding the #lock monitor
	private Info info;
	private Uaa uaa;

	public CloudController(HttpClient httpClient, URI target) {
		this.httpClient = httpClient;
		this.target = target;

		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public CloudController(DefaultHttpClient httpClient, String uri) {
		this(httpClient, URI.create(uri));
	}

	public Info getInfo() {
		synchronized (lock) {
			if (info == null) {
				fetchInfo();
			}
			return info;
		}
	}

	public Uaa getUaa() {
		synchronized (lock) {
			if (uaa == null) {
				uaa = new Uaa(httpClient, getInfo().getAuthorizationEndpoint());
			}
			return uaa;
		}
	}

	public String createService(Token token, Service service) {
		try {
			final String requestString = mapper.writeValueAsString(service);
			final HttpPost post = new HttpPost(target.resolve(V2_SERVICES));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(requestString, ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode json = mapper.readTree(response.getEntity().getContent());
				return json.get("metadata").get("guid").asText();
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO Figure out how to add query parameters
	public RestCollection<Service> getServices(Token token) {
		final ResultIterator<Service> iterator = new ResultIterator<>(token, V2_SERVICES, Service.class);
		return new RestCollection<>(iterator.getSize(), iterator);
	}



	private void validateResponse(HttpResponse response, int... expectedStatusCodes) {
		final StatusLine statusLine = response.getStatusLine();
		final int statusCode = statusLine.getStatusCode();
		for (int code : expectedStatusCodes) {
			if (code == statusCode) {
				return;
			}
		}
		throw new UnexpectedResponseException(response);
	}

	public void deleteService(Token token, String serviceGuid) {
		deleteUri(token, V2_SERVICES + "/" + serviceGuid);
	}

	public String createServicePlan(Token token, ServicePlan request) {
		return postJsonToUri(token, request, V2_SERVICE_PLANS);
	}

	public String createAuthToken(Token token, ServiceAuthToken request) {
		return postJsonToUri(token, request, V2_SERVICE_AUTH_TOKENS);
	}

	public void deleteAuthToken(Token token, String authTokenGuid) {
		deleteUri(token, V2_SERVICE_AUTH_TOKENS + "/" + authTokenGuid);
	}

	public String createServiceInstance(Token token, String name, String planGuid, String spaceGuid) {
		try {
			final ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("space_guid", spaceGuid);
			json.put("service_plan_guid", planGuid);
			final HttpPost post = new HttpPost(target.resolve(V2_SERVICE_INSTANCES));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode jsonResponse = mapper.readTree(response.getEntity().getContent());
				return jsonResponse.get("metadata").get("guid").asText();
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteServiceInstance(Token token, String instanceGuid) {
		deleteUri(token, V2_SERVICE_INSTANCES + "/" + instanceGuid);
	}

	private String postJsonToUri(Token token, Object json, String uri) {
		try {
			final String requestString = mapper.writeValueAsString(json);
			final HttpPost post = new HttpPost(target.resolve(uri));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(requestString, ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode responseJson = mapper.readTree(response.getEntity().getContent());
				return responseJson.get("metadata").get("guid").asText();
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteUri(Token token, String uri) {
		try {
			final HttpDelete delete = new HttpDelete(target.resolve(uri));
			delete.addHeader(token.toAuthorizationHeader());
			final HttpResponse response = httpClient.execute(delete);
			try {
				validateResponse(response, 204);
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void fetchInfo() {
		try {
			final HttpGet get = new HttpGet(target.resolve("/info"));
			// TODO Standardize on error handling
			// TODO Throw exception if non version 2 Cloud Controller
			final HttpResponse response = httpClient.execute(get);
			try {
				synchronized (lock) {
					info = mapper.readValue(response.getEntity().getContent(), Info.class);
				}
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private class ResultIterator<T> implements Iterator<Resource<T>> {

		private ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
			}
		};

		private final Token token;

		private final int size;

		private final Class<T> type;

		private String nextUri;
		private Iterator<Resource<T>> iterator;

		private ResultIterator(Token token, String uri, Class<T> type) {
			this.type = type;

			this.token = token;
			final JsonNode jsonNode = fetchResource(uri);

			size = jsonNode.get("total_results").asInt();

			parseResources(jsonNode);
		}

		private void parseResources(JsonNode jsonNode) {
			final JsonNode nextUrlNode = jsonNode.get("next_url");
			nextUri = nextUrlNode.isNull() ? null : nextUrlNode.asText();
			final Iterator<JsonNode> resourceNodeIterator = jsonNode.get("resources").getElements();
			final ArrayList<Resource<T>> resources = new ArrayList<>();
			while (resourceNodeIterator.hasNext()) {
				final JsonNode node = resourceNodeIterator.next();
				final JsonNode metadata = node.get("metadata");
				final UUID guid = UUID.fromString(metadata.get("guid").asText());
				final URI uri = URI.create(metadata.get("url").asText());
				Date created;
				try {
					created = dateFormat.get().parse(metadata.get("created_at").asText());
				} catch (ParseException e) {
					created = null;
				}
				Date updated;
				final String updatedAt = metadata.get("updated_at").asText();
				try {
					updated = updatedAt == null ? null : dateFormat.get().parse(updatedAt);
				} catch (ParseException e) {
					updated = null;
				}
				final T entity;
				try {
					entity = mapper.readValue(node.get("entity"), type);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				resources.add(new Resource<T>(entity, guid, uri, created, updated));
			}
			iterator = resources.iterator();
		}

		private JsonNode fetchResource(String uri) {
			try {
				final HttpGet httpGet = new HttpGet(target.resolve(uri));
				httpGet.setHeader(token.toAuthorizationHeader());
				final HttpResponse response = httpClient.execute(httpGet);
				try {
					validateResponse(response, 200);
					return mapper.readTree(response.getEntity().getContent());
				} finally {
					HttpClientUtils.closeQuietly(response);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean fetchNextPage() {
			if (nextUri == null) {
				return false;
			}
			final JsonNode jsonNode = fetchResource(nextUri);
			parseResources(jsonNode);
			return true;
		}

		@Override
		public boolean hasNext() {
			if (iterator.hasNext()) {
				return true;
			}
			if (!fetchNextPage()) {
				return false;
			}
			return iterator.hasNext();
		}

		@Override
		public Resource<T> next() {
			if (iterator.hasNext()) {
				return iterator.next();
			}
			if (!fetchNextPage()) {
				return null;
			}
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private int getSize() {
			return size;
		}

	}
}
