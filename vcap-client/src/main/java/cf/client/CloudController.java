package cf.client;

import cf.client.model.Info;
import cf.client.model.Service;
import cf.client.model.ServiceAuthToken;
import cf.client.model.ServiceBinding;
import cf.client.model.ServiceInstance;
import cf.client.model.ServicePlan;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface CloudController {
	Info getInfo();

	Uaa getUaa();

	UUID createService(Token token, Service service);

	RestCollection<Service> getServices(Token token);

	RestCollection<Service> getServices(Token token, UUID servicePlanGuid);

	RestCollection<ServicePlan> getServicePlans(Token token);

	RestCollection<ServicePlan> getServicePlans(Token token, ServicePlanQueryAttribute queryAttribute, String queryValue);

	RestCollection<ServiceInstance> getServiceInstances(Token token);

	RestCollection<ServiceInstance> getServiceInstances(Token token, ServiceInstanceQueryAttribute queryAttribute, String queryValue);

	RestCollection<ServiceBinding> getServiceBindings(Token token);

	RestCollection<ServiceBinding> getServiceBindings(Token token, ServiceBindingQueryAttribute queryAttribute, String queryValue);

	void deleteService(Token token, UUID serviceGuid);

	UUID createServicePlan(Token token, ServicePlan request);

	RestCollection<ServiceAuthToken> getAuthTokens(Token token);

	UUID createAuthToken(Token token, ServiceAuthToken request);

	void deleteServiceAuthToken(Token token, UUID authTokenGuid);

	UUID createServiceInstance(Token token, String name, UUID planGuid, UUID spaceGuid);

	void deleteServiceInstance(Token token, UUID instanceGuid);

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

	public interface QueryAttribute {}
}
