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
import cf.client.model.Info;
import cf.client.model.Service;
import cf.client.model.ServiceAuthToken;
import cf.client.model.ServiceBinding;
import cf.client.model.ServiceInstance;
import cf.client.model.ServicePlan;

import java.util.Map;
import java.util.UUID;

/**
 * Provides methods for invoking REST end-points on the Cloud Controller. This is not an attempt to create a general
 * purpose Cloud Foundry client. Rather, it fills some holes not addresses by the Cloud Foundry Java client namely
 * support for the service APIs.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface CloudController {

	/**
	 * Returns information about the cloud controller this interface is associated with.
	 *
	 * @return information about the cloud controller this interface is associated with.
	 */
	Info getInfo();

	/**
	 * Returns a UAA object for the UAA used by this cloud controller.
	 *
	 * @return a UAA object for the UAA used by this cloud controller.
	 */
	Uaa getUaa();

	Map<String, ApplicationInstance> getApplicationInstances(Token token, UUID applicationGuid);

	/**
	 * Creates a new service type.
	 *
	 * @param token the token used to authenticate the request.
	 * @param service the service to be created.
	 * @return the guid of the newly created service.
	 */
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
	 * Returns the service.
	 *
	 * @param token the token used to authenticate the request.
	 * @param serviceGuid the guid of the service.
	 * @return the service with the specified guid.
	 */
	Service getService(Token token, UUID serviceGuid);

	void deleteService(Token token, UUID serviceGuid);

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
	 * Deletes a service instances.
	 *
	 * @param token the token used to authenticate the request.
	 * @param instanceGuid the guid of service instance to delete.
	 */
	void deleteServiceInstance(Token token, UUID instanceGuid);

	public interface QueryAttribute {}

	public enum ServiceQueryAttribute implements QueryAttribute {
		SERVICE_PLAN_GUID {
			@Override
			public String toString() {
				return "service_plan_guid";
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

}
