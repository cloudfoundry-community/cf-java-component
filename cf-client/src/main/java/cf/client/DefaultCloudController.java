/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.client;

import cf.client.model.ApplicationInstance;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.client.model.Info;
import cf.client.model.Service;
import cf.client.model.ServiceAuthToken;
import cf.client.model.ServiceBinding;
import cf.client.model.ServiceInstance;
import cf.client.model.ServicePlan;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DefaultCloudController implements CloudController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCloudController.class);

	private static final String APP_INSTANCES = "/instances";
	private static final String V2_APPS = "/v2/apps";
	private static final String V2_SERVICES = "/v2/services";
	private static final String V2_SERVICE_AUTH_TOKENS = "/v2/service_auth_tokens";
	private static final String V2_SERVICE_BINDINGS = "/v2/service_bindings";
	private static final String V2_SERVICE_INSTANCES = "/v2/service_instances";
	private static final String V2_SERVICE_PLANS = "/v2/service_plans";

	private final HttpClient httpClient;
	private final URI target;

	private final ObjectMapper mapper;

	private final Object lock = new Object();

	// Access to the following fields needs to be done holding the #lock monitor
	private Info info;
	private Uaa uaa;

	public DefaultCloudController(HttpClient httpClient, URI target) {
		this.httpClient = httpClient;
		this.target = target;

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public DefaultCloudController(HttpClient httpClient, String uri) {
		this(httpClient, URI.create(uri));
	}

	@Override
	public Info getInfo() {
		synchronized (lock) {
			if (info == null) {
				fetchInfo();
			}
			return info;
		}
	}

	@Override
	public Uaa getUaa() {
		synchronized (lock) {
			if (uaa == null) {
				uaa = new DefaultUaa(httpClient, getInfo().getAuthorizationEndpoint());
			}
			return uaa;
		}
	}

	@Override
	public Map<String, ApplicationInstance> getApplicationInstances(Token token, UUID applicationGuid) {
		final JsonNode jsonNode = fetchResource(token, V2_APPS + "/" + applicationGuid.toString() + APP_INSTANCES);
		final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
		final Map<String, ApplicationInstance> instances = new HashMap<>();
		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> field = fields.next();
			try {
				instances.put(field.getKey(), mapper.readValue(field.getValue().traverse(), ApplicationInstance.class));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instances;
	}
	
	@Override
	public UUID createService(Token token, Service service) {
		try {
			final String requestString = mapper.writeValueAsString(service);
			final HttpPost post = new HttpPost(target.resolve(V2_SERVICES));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(requestString, ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode json = mapper.readTree(response.getEntity().getContent());
				return UUID.fromString(json.get("metadata").get("guid").asText());
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RestCollection<Service> getServices(Token token) {
		return getServices(token ,null);
	}

	@Override
	public RestCollection<Service> getServices(Token token, UUID servicePlanGuid) {
		final ResultIterator<Service> iterator = new ResultIterator<>(
				token,
				V2_SERVICES,
				Service.class,
				servicePlanGuid == null ? null : ServiceQueryAttribute.SERVICE_PLAN_GUID,
				servicePlanGuid == null ? null : servicePlanGuid.toString());
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public Service getService(Token token, UUID serviceGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SERVICES +"/"+ serviceGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Service.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RestCollection<ServicePlan> getServicePlans(Token token) {
		return getServicePlans(token, null, null);
	}

	@Override
	public RestCollection<ServicePlan> getServicePlans(Token token, ServicePlanQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<ServicePlan> iterator = new ResultIterator<>(
				token,
				V2_SERVICE_PLANS,
				ServicePlan.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<ServiceInstance> getServiceInstances(Token token) {
		return getServiceInstances(token, null, null);
	}

	@Override
	public RestCollection<ServiceInstance> getServiceInstances(Token token, ServiceInstanceQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<ServiceInstance> iterator = new ResultIterator<>(
				token,
				V2_SERVICE_INSTANCES,
				ServiceInstance.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public RestCollection<ServiceBinding> getServiceBindings(Token token) {
		return getServiceBindings(token, null, null);
	}
	
	@Override
	public RestCollection<ServiceBinding> getServiceBindings(Token token, ServiceBindingQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<ServiceBinding> iterator = new ResultIterator<>(
				token,
				V2_SERVICE_BINDINGS,
				ServiceBinding.class,
				queryAttribute,
				queryValue);
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

	@Override
	public void deleteService(Token token, UUID serviceGuid) {
		deleteUri(token, V2_SERVICES + "/" + serviceGuid);
	}

	@Override
	public UUID createServicePlan(Token token, ServicePlan request) {
		return postJsonToUri(token, request, V2_SERVICE_PLANS);
	}

	@Override
	public RestCollection<ServiceAuthToken> getAuthTokens(Token token) {
		final ResultIterator<ServiceAuthToken> iterator = new ResultIterator<>(
				token,
				V2_SERVICE_AUTH_TOKENS,
				ServiceAuthToken.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public UUID createAuthToken(Token token, ServiceAuthToken request) {
		return postJsonToUri(token, request, V2_SERVICE_AUTH_TOKENS);
	}

	@Override
	public void deleteServiceAuthToken(Token token, UUID authTokenGuid) {
		deleteUri(token, V2_SERVICE_AUTH_TOKENS + "/" + authTokenGuid);
	}

	@Override
	public UUID createServiceInstance(Token token, String name, UUID planGuid, UUID spaceGuid) {
		try {
			final ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("service_plan_guid", planGuid.toString());
			json.put("space_guid", spaceGuid.toString());
			final HttpPost post = new HttpPost(target.resolve(V2_SERVICE_INSTANCES));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode jsonResponse = mapper.readTree(response.getEntity().getContent());
				return UUID.fromString(jsonResponse.get("metadata").get("guid").asText());
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteServiceInstance(Token token, UUID instanceGuid) {
		deleteUri(token, V2_SERVICE_INSTANCES + "/" + instanceGuid);
	}

	private UUID postJsonToUri(Token token, Object json, String uri) {
		try {
			final String requestString = mapper.writeValueAsString(json);
			final HttpPost post = new HttpPost(target.resolve(uri));
			post.addHeader(token.toAuthorizationHeader());
			post.setEntity(new StringEntity(requestString, ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response, 201);
				final JsonNode responseJson = mapper.readTree(response.getEntity().getContent());
				return UUID.fromString(responseJson.get("metadata").get("guid").asText());
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

	private JsonNode fetchResource(Token token, String uri) {
		LOGGER.debug("GET {}", uri);
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

		private ResultIterator(Token token, String uri, Class<T> type, QueryAttribute queryAttribute, String queryValue) {
			this.type = type;

			this.token = token;
			if (queryAttribute != null) {
				uri += "?q=" + queryAttribute + ":" + queryValue;
			}
			final JsonNode jsonNode = fetchResource(token, uri);

			size = jsonNode.get("total_results").asInt();

			parseResources(jsonNode);
		}

		private void parseResources(JsonNode jsonNode) {
			final JsonNode nextUrlNode = jsonNode.get("next_url");
			nextUri = nextUrlNode.isNull() ? null : nextUrlNode.asText();
			final Iterator<JsonNode> resourceNodeIterator = jsonNode.get("resources").elements();
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
					entity = mapper.readValue(node.get("entity").traverse(), type);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				resources.add(new Resource<>(entity, guid, uri, created, updated));
			}
			iterator = resources.iterator();
		}

		public boolean fetchNextPage() {
			if (nextUri == null) {
				return false;
			}
			final JsonNode jsonNode = fetchResource(token, nextUri);
			parseResources(jsonNode);
			return true;
		}

		@Override
		public boolean hasNext() {
			// Check if current iterator has an element, if not load the next page and check again.
			return iterator.hasNext() || (fetchNextPage() && iterator.hasNext());
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
