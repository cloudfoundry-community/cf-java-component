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

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.client.model.AppUsageEvent;
import cf.client.model.Application;
import cf.client.model.ApplicationInstance;
import cf.client.model.ApplicationInstanceStats;
import cf.client.model.Event;
import cf.client.model.Info;
import cf.client.model.Organization;
import cf.client.model.PrivateDomain;
import cf.client.model.Route;
import cf.client.model.SecurityGroup;
import cf.client.model.Service;
import cf.client.model.ServiceAuthToken;
import cf.client.model.ServiceBinding;
import cf.client.model.ServiceInstance;
import cf.client.model.ServicePlan;
import cf.client.model.SharedDomain;
import cf.client.model.Space;
import cf.client.model.User;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Mike Heath
 */
public class DefaultCloudController implements CloudController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCloudController.class);

	private static final String APP_INSTANCES = "/instances";
	private static final String V2_APPS = "/v2/apps";
	private static final String V2_PRIVATE_DOMAINS = "/v2/private_domains";
	private static final String V2_SHARED_DOMAINS = "/v2/shared_domains";
	private static final String V2_ROUTES = "/v2/routes";
	private static final String V2_SERVICES = "/v2/services";
	private static final String V2_SERVICE_AUTH_TOKENS = "/v2/service_auth_tokens";
	private static final String V2_SERVICE_BINDINGS = "/v2/service_bindings";
	private static final String V2_SERVICE_INSTANCES = "/v2/service_instances";
	private static final String V2_SERVICE_PLANS = "/v2/service_plans";
	private static final String V2_SPACES = "/v2/spaces";
	private static final String V2_ORGANIZATIONS = "/v2/organizations";
	private static final String V2_USER_PROVIDED_SERVICE_INSTANCES = "/v2/user_provided_service_instances";
	
	private static final String V2_EVENTS = "/v2/events";
	private static final String V2_APP_USAGE_EVENTS = "/v2/app_usage_events";

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
	public URI getTarget() {
		return target;
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
		final JsonNode jsonNode = fetchResource(token, V2_APPS + "/" + applicationGuid.toString() + "/stats");
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
	public Map<String, ApplicationInstanceStats> getApplicationInstanceStats(Token token, UUID applicationGuid) {
		final JsonNode jsonNode = fetchResource(token, V2_APPS + "/" + applicationGuid.toString() + "/stats");
		final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
		final Map<String, ApplicationInstanceStats> instanceStats = new HashMap<>();
		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> field = fields.next();
			try {
				instanceStats.put(field.getKey(), mapper.readValue(field.getValue().traverse(), ApplicationInstanceStats.class));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instanceStats;
	}
	
	

	
	
	@Override
	public Application getApplication(Token token, UUID applicationGuid) {
		JsonNode jsonNode = fetchResource(token, V2_APPS +"/"+ applicationGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Application.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public RestCollection<Application> getApplication(Token token, ApplicationQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<Application> iterator = new ResultIterator<>(
				token,
				V2_APPS,
				Application.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<Application> getApplications(Token token) {
		final ResultIterator<Application> iterator = new ResultIterator<>(
				token,
				V2_APPS,
				Application.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public Application updateApplication(Token token, UUID applicationGuid, Application application) {
		JsonNode jsonNode = putJsonToUri(token, application, V2_APPS, applicationGuid);
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Application.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		final ResultIterator<Service> iterator = new ResultIterator<>(
				token,
				V2_SERVICES,
				Service.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<Service> getServices(Token token, UUID servicePlanGuid) {
		return getServices(token, ServiceQueryAttribute.SERVICE_PLAN_GUID, servicePlanGuid.toString());
	}

	@Override
	public RestCollection<Service> getServices(Token token, ServiceQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<Service> iterator = new ResultIterator<>(
				token,
				V2_SERVICES,
				Service.class,
				queryAttribute,
				queryValue);
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
	public ServicePlan getServicePlan(Token token, UUID servicePlanGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SERVICE_PLANS +"/"+ servicePlanGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), ServicePlan.class);
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
	public ServiceInstance getServiceInstance(Token token, UUID instanceGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SERVICE_INSTANCES +"/"+ instanceGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), ServiceInstance.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public Space getSpace(Token token, UUID spaceGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SPACES +"/"+ spaceGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Space.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public RestCollection<Space> getSpace(Token token, SpaceQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<Space> iterator = new ResultIterator<>(
				token,
				V2_SPACES,
				Space.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public RestCollection<Space> getSpaces(Token token) {
		final ResultIterator<Space> iterator = new ResultIterator<>(
				token,
				V2_SPACES,
				Space.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);				

	}	
	
	@Override
	public RestCollection<User> getManagersInOrg(Token token, UUID orgGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_ORGANIZATIONS +"/"+orgGuid.toString()+"/managers",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	
	@Override
	public RestCollection<User> getAuditorsInOrg(Token token, UUID orgGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_ORGANIZATIONS +"/"+orgGuid.toString()+"/auditors",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	
	@Override
	public RestCollection<User> getUsersInOrg(Token token, UUID orgGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_ORGANIZATIONS +"/"+orgGuid.toString()+"/users",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	
	@Override
	public RestCollection<User> getManagersInSpace(Token token, UUID spaceGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_SPACES+"/"+spaceGuid.toString()+"/managers",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	public RestCollection<User> getAuditorsInSpace(Token token, UUID spaceGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_SPACES+"/"+spaceGuid.toString()+"/auditors",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	public RestCollection<User> getDevelopersInSpace(Token token, UUID spaceGuid) {
		final ResultIterator<User> iterator = new ResultIterator<>(
				token,
				V2_SPACES+"/"+spaceGuid.toString()+"/developers",
				User.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<SecurityGroup> getSecurityGroupsForSpace(Token token, UUID spaceGuid) {
		//Hack until this issue is fixed: https://www.pivotaltracker.com/story/show/82055042
		JsonNode node = fetchResource(token, V2_SPACES+"/"+spaceGuid.toString()+"?inline-relations-depth=1");
		JsonNode entity = node.get("entity");
		final ResultIterator<SecurityGroup> iterator = new ResultIterator<>(SecurityGroup.class, (ArrayNode)entity.get("security_groups"));
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public Organization getOrganization(Token token, UUID organizationGuid) {
		JsonNode jsonNode = fetchResource(token, V2_ORGANIZATIONS +"/"+ organizationGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Organization.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public RestCollection<Organization> getOrganizations(Token token) {
		final ResultIterator<Organization> iterator = new ResultIterator<>(
				token,
				V2_ORGANIZATIONS,
				Organization.class,
				null,
				null);
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
	
	@Override
	public ServiceBinding getServiceBinding(Token token, UUID serviceBindingGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SERVICE_BINDINGS +"/"+ serviceBindingGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), ServiceBinding.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void deleteServiceBinding(Token token, UUID serviceBindingGuid) {
		deleteUri(token, V2_SERVICE_BINDINGS + "/" + serviceBindingGuid);
	}
	
	@Override
	public UUID createServiceBinding(Token token, UUID appGuid, UUID serviceInstanceGuid) {
		return postJsonToUri(token, new ServiceBinding(appGuid, serviceInstanceGuid, null, null, null), V2_SERVICE_BINDINGS);
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
	public Service updateService(Token token, UUID serviceGuid, Service service) {
		JsonNode jsonNode = putJsonToUri(token, service, V2_SERVICES, serviceGuid);
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), Service.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UUID createServicePlan(Token token, ServicePlan request) {
		return postJsonToUri(token, request, V2_SERVICE_PLANS);
	}
	
	@Override
	public ServicePlan updateServicePlan(Token token, UUID servicePlanGuid, ServicePlan service) {
		JsonNode jsonNode = putJsonToUri(token, service, V2_SERVICE_PLANS, servicePlanGuid);
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), ServicePlan.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		return createServiceInstance(token, name, planGuid, spaceGuid, mapper.createObjectNode());
	}

	@Override
	public UUID createServiceInstance(Token token, String name, UUID planGuid, UUID spaceGuid, ObjectNode params) {
		try {
			final ObjectNode json = mapper.createObjectNode();
			json.put("name", name);
			json.put("service_plan_guid", planGuid.toString());
			json.put("space_guid", spaceGuid.toString());
			json.put("parameters", params);
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
	public UUID createUserProvidedServiceInstance(Token token, String name, UUID spaceGuid, ObjectNode params) {
		final ObjectNode json = mapper.createObjectNode();
		json.put("name", name);
		json.put("space_guid", spaceGuid.toString());
		json.put("credentials", params);

		final HttpPost post = new HttpPost(target.resolve(V2_USER_PROVIDED_SERVICE_INSTANCES));
		post.addHeader(token.toAuthorizationHeader());

		try {
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
	public ServiceInstance updateServiceInstance(Token token, UUID serviceInstanceGuid, ServiceInstance serviceInstance) {
		JsonNode jsonNode = putJsonToUri(token, serviceInstance, V2_SERVICE_INSTANCES, serviceInstanceGuid);
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), ServiceInstance.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteServiceInstance(Token token, UUID instanceGuid) {
		deleteUri(token, V2_SERVICE_INSTANCES + "/" + instanceGuid);
	}
	
	@Override
	public void purgeServiceInstance(Token token, UUID instanceGuid) {
		deleteUri(token, V2_SERVICE_INSTANCES + "/" + instanceGuid+"?purge=true");
	}
	
	@Override
	public RestCollection<PrivateDomain> getPrivateDomains(Token token) {
		final ResultIterator<PrivateDomain> iterator = new ResultIterator<>(
				token,
				V2_PRIVATE_DOMAINS,
				PrivateDomain.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public PrivateDomain getPrivateDomain(Token token, UUID privateDomainGuid) {
		JsonNode jsonNode = fetchResource(token, V2_PRIVATE_DOMAINS +"/"+ privateDomainGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), PrivateDomain.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public RestCollection<SharedDomain> getSharedDomains(Token token) {
		final ResultIterator<SharedDomain> iterator = new ResultIterator<>(
				token,
				V2_SHARED_DOMAINS,
				SharedDomain.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public SharedDomain getSharedDomain(Token token, UUID sharedDomainGuid) {
		JsonNode jsonNode = fetchResource(token, V2_SHARED_DOMAINS +"/"+ sharedDomainGuid.toString());
		try {
			return mapper.readValue(jsonNode.get("entity").traverse(), SharedDomain.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RestCollection<Route> getRoutes(Token token) {
		final ResultIterator<Route> iterator = new ResultIterator<>(
				token,
				V2_ROUTES,
				Route.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public RestCollection<Application> getAppsForRoute(Token token, UUID routeGuid) {
		final ResultIterator<Application> iterator = new ResultIterator<>(
				token,
				V2_ROUTES+"/"+routeGuid+"/apps",
				Application.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	@Override
	public RestCollection<Route> getRoutes(Token token, RouteQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<Route> iterator = new ResultIterator<>(
				token,
				V2_ROUTES,
				Route.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<Route> getRoutesForApp(Token token, UUID appGuid) {
		final ResultIterator<Route> iterator = new ResultIterator<>(
				token,
				V2_ROUTES+"/"+appGuid+"/routes",
				Route.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	//Events
	
	@Override
	public RestCollection<Event> getEvents(Token token, EventQueryAttribute queryAttribute, String queryValue) {
		final ResultIterator<Event> iterator = new ResultIterator<>(
				token,
				V2_EVENTS,
				Event.class,
				queryAttribute,
				queryValue);
		return new RestCollection<>(iterator.getSize(), iterator);
	}
	
	
	@Override
	public RestCollection<Event> getEvents(Token token, String url) {
		final ResultIterator<Event> iterator = new ResultIterator<>(
				token,
				url,
				Event.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<Event> getEvents(Token token) {
		final ResultIterator<Event> iterator = new ResultIterator<>(
				token,
				V2_EVENTS,
				Event.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<AppUsageEvent> getAppUsageEvents(Token token,
			String url) {
		final ResultIterator<AppUsageEvent> iterator = new ResultIterator<>(
				token,
				url,
				AppUsageEvent.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
	}

	@Override
	public RestCollection<AppUsageEvent> getAppUsageEvents(Token token) {
		final ResultIterator<AppUsageEvent> iterator = new ResultIterator<>(
				token,
				V2_APP_USAGE_EVENTS,
				AppUsageEvent.class,
				null,
				null);
		return new RestCollection<>(iterator.getSize(), iterator);
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

	private JsonNode putJsonToUri(Token token, Object json, String uri, UUID guid) {
		try {
			final String requestString = mapper.writeValueAsString(json);
			final HttpPut put = new HttpPut(target.resolve(uri+"/"+ guid.toString()));
			put.addHeader(token.toAuthorizationHeader());
			put.setEntity(new StringEntity(requestString, ContentType.APPLICATION_JSON));
			final HttpResponse response = httpClient.execute(put);
			try {
				validateResponse(response, 201);
				return mapper.readTree(response.getEntity().getContent());
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
			final HttpGet get = new HttpGet(target.resolve("/v2/info"));
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

			final JsonNode nextUrlNode = jsonNode.get("next_url");
			nextUri = nextUrlNode.isNull() ? null : nextUrlNode.asText();

			parseResources((ArrayNode)jsonNode.get("resources"));
		}

		private ResultIterator(Class<T> type, ArrayNode jsonNode) {
			this.type = type;

			this.token = null;

			this.size = jsonNode.size();

			parseResources(jsonNode);
		}

		private void parseResources(ArrayNode resourcesJsonNode) {
			final Iterator<JsonNode> resourceNodeIterator = resourcesJsonNode.elements();
			final ArrayList<Resource<T>> resources = new ArrayList<>();
			while (resourceNodeIterator.hasNext()) {
				final JsonNode node = resourceNodeIterator.next();
				final JsonNode metadata = node.get("metadata");
				final String guid = metadata.get("guid").asText();
				final URI uri = URI.create(metadata.get("url").asText());
				Date created;
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");					
					ZonedDateTime dateTime = ZonedDateTime.parse(metadata.get("created_at").asText(), formatter);					
					created = Date.from(dateTime.toInstant());
					
				} catch (Exception e) {
					created = null;
				}
				Date updated;
				if(metadata.get("updated_at")==null) {
					updated=null;
				}else {
				final String updatedAt = metadata.get("updated_at").asText();
				try {
					updated = updatedAt == null ? null : dateFormat.get().parse(updatedAt);
				} catch (ParseException e) {
					updated = null;
				}
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
			final JsonNode nextUrlNode = jsonNode.get("next_url");
			nextUri = nextUrlNode.isNull() ? null : nextUrlNode.asText();
			parseResources((ArrayNode)jsonNode.get("resources"));
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
