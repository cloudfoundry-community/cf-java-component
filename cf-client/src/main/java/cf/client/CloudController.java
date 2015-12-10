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

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import cf.client.model.AppUsageEvent;
import cf.client.model.Application;
import cf.client.model.ApplicationInstance;
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
import cf.client.model.Space;
import cf.client.model.User;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Provides methods for invoking REST end-points on the Cloud Controller. This is not an attempt to create a general
 * purpose Cloud Foundry client. Rather, it fills some holes not addresses by the Cloud Foundry Java client namely
 * support for the service APIs.
 *
 * @author Mike Heath
 */
public interface CloudController {

	/**
	 * Returns information about the cloud controller this interface is associated with.
	 *
	 * @return information about the cloud controller this interface is associated with.
	 */
	Info getInfo();
	
	/**
	 * Returns the URI this cloud controller is associated with.
	 * @return
	 */
	URI getTarget();

	/**
	 * Returns a UAA object for the UAA used by this cloud controller.
	 *
	 * @return a UAA object for the UAA used by this cloud controller.
	 */
	Uaa getUaa();

	Map<String, ApplicationInstance> getApplicationInstances(Token token, UUID applicationGuid);
	
	/**
	 * Get an application
	 * @param token
	 * @param applicationGuid
	 * @return
	 */
	Application getApplication(Token token, UUID applicationGuid);
	
	
	RestCollection<Application> getApplication(Token token, ApplicationQueryAttribute queryAttribute, String queryValue);

	/**
	 * Update an application.
	 * 
	 * @param token
	 * @param applicationGuid
	 * @param application
	 * @return
	 */
	Application updateApplication(Token token, UUID applicationGuid, Application application);
	/**
	 * Creates a new service type.
	 *
	 * @param token the token used to authenticate the request.
	 * @param service the service to be created.
	 * @return the guid of the newly created service.
	 */
	
	RestCollection<Event> getEvents(Token token,String url);
	RestCollection<Event> getEvents(Token token);
	
	RestCollection<AppUsageEvent> getAppUsageEvents(Token token,String url);
	RestCollection<AppUsageEvent> getAppUsageEvents(Token token);
	
	
	UUID createService(Token token, Service service);

	/**
	 * Returns all the services.
	 *
	 * @param token the token used to authenticate the request.
	 * @return a collection containing all the services
	 */
	RestCollection<Service> getServices(Token token);

	/**
	 * Returns all the services using the specified service plan.
	 *
	 * @param token the token used to authenticate the request.
	 * @param servicePlanGuid the guid of the service plan used to filter the request.
	 * @return a collection of all the services using the specified service plan.
	 */
	RestCollection<Service> getServices(Token token, UUID servicePlanGuid);

	/**
	 * Returns all the services that match the given query.
	 *
	 * @param token the token used to authenticate the request.
	 * @return a collection of all the services using the specified service plan.
	 */
	RestCollection<Service> getServices(Token token, ServiceQueryAttribute queryAttribute, String queryValue);

	/**
	 * Returns the service.
	 *
	 * @param token the token used to authenticate the request.
	 * @param serviceGuid the guid of the service.
	 * @return the service with the specified guid.
	 */
	Service getService(Token token, UUID serviceGuid);

	void deleteService(Token token, UUID serviceGuid);
	
	Service updateService(Token token, UUID serviceGuid, Service service);

	/**
	 * Returns a service plan given the Guid.
	 * 
	 * @param token
	 * @param servicePlanGuid
	 * @return
	 */
	ServicePlan getServicePlan(Token token, UUID servicePlanGuid);
	
	/**
	 * Returns all the service plans.
	 *
	 * @param token the token used to authenticate the request.
	 * @return a collection of all the service plans.
	 */
	RestCollection<ServicePlan> getServicePlans(Token token);

	/**
	 * Creates a service plan.
	 *
	 * @param token the token used to authenticate the request.
	 * @param servicePlan the service plan to create
	 * @return the UUID of the newly created service plan
	 */
	UUID createServicePlan(Token token, ServicePlan servicePlan);

	ServicePlan updateServicePlan(Token token, UUID servicePlanGuid, ServicePlan servicePlan);
	
	/**
	 * Returns a list of service plans filtered by the query arguments.
	 *
	 * @param token the token used to authenticate the request.
	 * @param queryAttribute the attribute to query on
	 * @param queryValue the expected value of the attribute
	 * @return a filtered list of service plans.
	 */
	RestCollection<ServicePlan> getServicePlans(Token token, ServicePlanQueryAttribute queryAttribute, String queryValue);

	/**
	 * Returns the service instance.
	 *
	 * @param token the token used to authenticate the request.
	 * @param serviceGuid the guid of the service.
	 * @return the service with the specified guid.
	 */
	ServiceInstance getServiceInstance(Token token, UUID instanceGuid);

	/**
	 * Returns the space.
	 *
	 * @param token the token used to authenticate the request.
	 * @param space guid.
	 * @return the space for the given guid
	 */
	Space getSpace(Token token, UUID spaceGuid);

	/**
	 * Returns spaces that match the query.
	 *
	 * @param token the token used to authenticate the request.
	 * @param queryAttribute: the attribute to query on
	 * @param queryValue: the expected value of the attribute
	 * @return the space for the given guid
	 */
	RestCollection<Space> getSpace(Token token, SpaceQueryAttribute queryAttribute, String queryValue);
	
	/**
	 * Returns spaces.
	 *
	 * @param token the token used to authenticate the request.
	 * @return all the spaces for the given org guid
	 */	
	RestCollection<Space> getSpaces(Token token);
	
	/**
	 * Returns the managers of an organization.
	 *
	 * @param token 
	 * @param orgGuid
	 * @return managers
	 */
	RestCollection<User> getManagersInOrg(Token token, UUID orgGuid);
	
	
	/**
	 * Returns the auditors of an organization.
	 *
	 * @param token 
	 * @param orgGuid
	 * @return managers
	 */
	RestCollection<User> getAuditorsInOrg(Token token, UUID orgGuid);
	
	
	/**
	 * Returns the users in an organization.
	 *
	 * @param token 
	 * @param orgGuid
	 * @return users
	 */
	RestCollection<User> getUsersInOrg(Token token, UUID orgGuid);
	
	
	/**
	 * Returns the managers of a space.
	 *
	 * @param token 
	 * @param spaceGuid
	 * @return managers
	 */
	RestCollection<User> getManagersInSpace(Token token, UUID spaceGuid);
	
	
	/**
	 * Returns the auditors of a space.
	 *
	 * @param token 
	 * @param spaceGuid
	 * @return auditors
	 */
	RestCollection<User> getAuditorsInSpace(Token token, UUID spaceGuid);
	
	
	/**
	 * Returns the developers of a space.
	 *
	 * @param token 
	 * @param spaceGuid
	 * @return developers
	 */
	RestCollection<User> getDevelopersInSpace(Token token, UUID spaceGuid);

	/**
	 * Get Security Groups for a space 
	 *
	 * @param token the token used to authenticate the request.
	 * @param spaceGuid The space to get the security groups from
	 * @return all the security groups for a space.
	 */
	RestCollection<SecurityGroup> getSecurityGroupsForSpace(Token token, UUID spaceGuid);

	/**
	 * Returns the org.
	 *
	 * @param token the token used to authenticate the request.
	 * @param org guid.
	 * @return the org for the given guid
	 */
	Organization getOrganization(Token token, UUID organizationGuid);

	/**
	 * Returns the org.
	 *
	 * @param token the token used to authenticate the request.
	 * @return the orgs,
	 */
	RestCollection<Organization> getOrganizations(Token token);
	
	/**
	 * Returns all the service instances.
	 *
	 * @param token the token used to authenticate the request.
	 * @return all the service instances.
	 */
	RestCollection<ServiceInstance> getServiceInstances(Token token);

	/**
	 * Returns a list of service instances filtered by the query arguments.
	 *
	 * @param token the token used to authenticate the request.
	 * @param queryAttribute the attribute to query on
	 * @param queryValue the expected value of the attribute
	 * @return a filtered list of service instances
	 */
	RestCollection<ServiceInstance> getServiceInstances(Token token, ServiceInstanceQueryAttribute queryAttribute, String queryValue);

	/**
	 * Returns all the service bindings.
	 *
	 * @param token the token used to authenticate the request.
	 * @return all the service bindings.
	 */
	RestCollection<ServiceBinding> getServiceBindings(Token token);

	/**
	 * Returns a list of service bindings filtered by the query arguments.
	 *
	 * @param token the token used to authenticate the request.
	 * @param queryAttribute the attribute to query on
	 * @param queryValue the expected value of the attribute
	 * @return a filtered list of service bindings.
	 */
	RestCollection<ServiceBinding> getServiceBindings(Token token, ServiceBindingQueryAttribute queryAttribute, String queryValue);

	/**
	 * Returns a service binding.
	 * @param token
	 * @param serviceBindingGuid
	 * @return
	 */
	ServiceBinding getServiceBinding(Token token, UUID serviceBindingGuid);

	/**
	 * Delete a serviceBinding
	 * @param token
	 * @param serviceBindingGuid
	 * @return
	 */
	void deleteServiceBinding(Token token, UUID serviceBindingGuid);

	/**
	 * Create a serviceBinding
	 * @param token
	 * @return
	 */
	UUID createServiceBinding(Token token, UUID appGuid, UUID serviceInstanceGuid);

	/**
	 * Returns all the service auth tokens.
	 *
	 * @param token the token used to authenticate the request.
	 * @return all the service auth tokens.
	 */
	RestCollection<ServiceAuthToken> getAuthTokens(Token token);

	/**
	 * Creates a service auth token.
	 *
	 * @param token the token used to authenticate the request.
	 * @param serviceAuthToken the service auth token to be created.
	 * @return the guid of the newly create service auth token.
	 */
	UUID createAuthToken(Token token, ServiceAuthToken serviceAuthToken);

	/**
	 * Deletes a service auth token.
	 *
	 * @param token the token used to authenticate the request.
	 * @param authTokenGuid the guid of the service auth token to delete
	 */
	void deleteServiceAuthToken(Token token, UUID authTokenGuid);

	/**
	 *Creates a new service instance.
	 *
	 * @param token the token used to authenticate the request.
	 * @param name the name of the service instance
	 * @param planGuid the guid of the service plan being used.
	 * @param spaceGuid the guid of the space in which the service will be created
	 * @return
	 */
	UUID createServiceInstance(Token token, String name, UUID planGuid, UUID spaceGuid);

	/**
	 *Creates a new service instance with params.
	 *
	 * @return
	 */
	UUID createServiceInstance(Token token, String name, UUID planGuid, UUID spaceGuid, ObjectNode params);

	ServiceInstance updateServiceInstance(Token token, UUID serviceInstanceGuid, ServiceInstance serviceInstance);

	/**
	 * Deletes a service instances.
	 *
	 * @param token the token used to authenticate the request.
	 * @param instanceGuid the guid of service instance to delete.
	 */
	void deleteServiceInstance(Token token, UUID instanceGuid);

	/**
	 * Purge service instances.
	 *
	 * @param token the token used to authenticate the request.
	 * @param instanceGuid the guid of service instance to delete.
	 */
	void purgeServiceInstance(Token token, UUID instanceGuid);

	/**
	 * Get all of the private Domains.
	 * @param token
	 * @return all the private domains this user can see
	 */
	RestCollection<PrivateDomain> getPrivateDomains(Token token);

	/**
	 * Get all of the routes this user can see.
	 * @param token
	 * @return 
	 */
	RestCollection<Route> getRoutes(Token token);

	/**
	 * Get all the applications for a given route.
	 * @param token
	 * @param routeGuid
	 * @return
	 */
	RestCollection<Application> getAppsForRoute(Token token, UUID routeGuid);

	/**
	 * Find routes with query attributes.
	 * @param token
	 * @param queryAttribute
	 * @param queryValue
	 * @return
	 */
	RestCollection<Route> getRoutes(Token token, RouteQueryAttribute queryAttribute, String queryValue);

	public interface QueryAttribute {}

	public enum ServiceQueryAttribute implements QueryAttribute {
		SERVICE_PLAN_GUID {
			@Override
			public String toString() {
				return "service_plan_guid";
			}
		}, LABEL {
			@Override
			public String toString() {
				return "label";
			}
		}
	}

	public enum ServicePlanQueryAttribute implements QueryAttribute {
		SERVICE_GUID {
			@Override
			public String toString() {
				return "service_guid";
			}
		},
		SERVICE_INSTANCE_GUID {
			@Override
			public String toString() {
				return "service_instance_guid";
			}
		}
	}

	public enum ServiceInstanceQueryAttribute implements QueryAttribute {
		NAME {
			@Override
			public String toString() {
				return "name";
			}
		},
		SPACE_GUID {
			@Override
			public String toString() {
				return "space_guid";
			}
		},
		SERVICE_PLAN_GUID {
			@Override
			public String toString() {
				return "service_plan_guid";
			}
		},
		SERVICE_BINDING_GUID {
			@Override
			public String toString() {
				return "service_binding_guid";
			}
		}
	}

	public enum ServiceBindingQueryAttribute implements QueryAttribute {
		APPLICATION_GUID {
			@Override
			public String toString() {
				return "app_guid";
			}
		},
		SERVICE_INSTANCE_GUID {
			@Override
			public String toString() {
				return "service_instance_guid";
			}
		}
	}
	
	public enum SpaceQueryAttribute implements QueryAttribute {
		NAME {
			@Override
			public String toString() {
				return "name";
			}
		},
		ORGANIZATION_GUID {
			@Override
			public String toString() {
				return "organization_guid";
			}
		},
		DEVELOPER_GUID {
			@Override
			public String toString() {
				return "developer_guid";
			}
		},
		APP_GUID {
			@Override
			public String toString() {
				return "app_guid";
			}
		}
		
	}
	
	public enum ApplicationQueryAttribute implements QueryAttribute {
		NAME {
			@Override
			public String toString() {
				return "name";
			}
		},
		SPACE_GUID {
			@Override
			public String toString() {
				return "space_guid";
			}
		},
		ORGANIZATION_GUID {
			@Override
			public String toString() {
				return "organization_guid";
			}
		}		
	}
	
	public enum RouteQueryAttribute implements QueryAttribute {
		DOMAIN_GUID {
			@Override
			public String toString() {
				return "domain_guid";
			}
		},
		SPACE_GUID {
			@Override
			public String toString() {
				return "space_guid";
			}
		}
	}


	



}
