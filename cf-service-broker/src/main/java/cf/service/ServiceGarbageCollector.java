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
package cf.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cf.client.CloudController;
import cf.client.Resource;
import cf.client.RestCollection;
import cf.client.TokenProvider;
import cf.client.model.ServiceBinding;
import cf.client.model.ServiceInstance;
import cf.client.model.ServicePlan;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath
 * @deprecated Use the V2 services.
 */
@Deprecated
public class ServiceGarbageCollector {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceGarbageCollector.class);

	public static final long COLLECTION_RATE = TimeUnit.HOURS.toMillis(1);

	private final UUID serviceGuid;
	private final CloudController cloudController;
	private final TokenProvider token;
	private final Provisioner provisioner;

	public ServiceGarbageCollector(ScheduledExecutorService executorService, UUID serviceGuid, CloudController cloudController, TokenProvider token, Provisioner provisioner) {
		this.serviceGuid = serviceGuid;
		this.cloudController = cloudController;
		this.token = token;
		this.provisioner = provisioner;
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				collect();
			}
		}, COLLECTION_RATE, COLLECTION_RATE, TimeUnit.MILLISECONDS);
	}

	protected void collect() {
		// Get all service instances known by service gateway
		final Iterable<String> knownInstanceIds = provisioner.serviceInstanceIds();
		if (knownInstanceIds == null) {
			return;
		}
		LOGGER.info("Checking for orphaned service instances");
		final Set<String> serviceInstanceIds = new HashSet<>();
		for (String serviceId : knownInstanceIds) {
			serviceInstanceIds.add(serviceId);
		}

		// Iterate over all service instances and remove ids known by Cloud Controller
		final RestCollection<ServicePlan> servicePlans = cloudController.getServicePlans(
				token.get(),
				CloudController.ServicePlanQueryAttribute.SERVICE_GUID,
				serviceGuid.toString());
		for (Resource<ServicePlan> servicePlan : servicePlans) {
			LOGGER.debug("Loading service instances under service plan '{}'", servicePlan.getEntity().getName());
			final RestCollection<ServiceInstance> serviceInstances = cloudController.getServiceInstances(
					token.get(),
					CloudController.ServiceInstanceQueryAttribute.SERVICE_PLAN_GUID,
					servicePlan.getGuid().toString());
			for (Resource<ServiceInstance> serviceInstance : serviceInstances) {
				final JsonNode gatewayData = serviceInstance.getEntity().getGatewayData();
				final String serviceInstanceId = gatewayData.get(ServiceBroker.SERVICE_INSTANCE_ID).asText();

				final boolean removed = serviceInstanceIds.remove(serviceInstanceId);
				if (removed) {
					collectBindings(serviceInstance, serviceInstanceId);
				} else {
					LOGGER.warn(
							"Service instance {} in space {} is in the Cloud Controller but is not known by the broker",
							serviceInstance.getEntity().getName(),
							serviceInstance.getEntity().getSpaceGuid());
				}
			}
		}

		if (serviceInstanceIds.size() > 0) {
			LOGGER.info("Found {} orphaned service instance(s) to be removed.");
			for (String instanceId : serviceInstanceIds) {
				try {
					provisioner.removeOrphanedServiceInstance(instanceId);
				} catch (Exception e) {
					LOGGER.error("Error removing service instance with id " + instanceId, e);
				}
			}
		}
	}

	private void collectBindings(Resource<ServiceInstance> serviceInstance, String serviceInstanceId) {
		final Iterable<String> knownBindingIds = provisioner.bindingIds(serviceInstanceId);
		if (knownBindingIds == null) {
			return;
		}
		final Set<String> bindingIds = new HashSet<>();
		for (String bindingId : knownBindingIds) {
			bindingIds.add(bindingId);
		}
		final RestCollection<ServiceBinding> serviceBindings = cloudController.getServiceBindings(
				token.get(),
				CloudController.ServiceBindingQueryAttribute.SERVICE_INSTANCE_GUID,
				serviceInstance.getGuid().toString()
		);
		for (Resource<ServiceBinding> serviceBinding : serviceBindings) {
			final String bindingId = serviceBinding.getEntity().getGatewayData().get(ServiceBroker.SERVICE_BINDING_ID).asText();
			final boolean removed = bindingIds.remove(bindingId);
			if (!removed) {
				LOGGER.warn("Service binding {} for service {} is in the Cloud Controller but is not known by the broker", bindingId, serviceInstanceId);
			}
		}

		if (bindingIds.size() > 0) {
			LOGGER.info("Found {} orphaned binding(s) for service instance {}", bindingIds, serviceInstanceId);
			for (String bindingId : bindingIds) {
				try {
					LOGGER.debug("Removing binding {} for service instance {}", bindingId, serviceInstanceId);
					provisioner.removeOrphanedBinding(serviceInstanceId, bindingId);
				} catch (Exception e) {
					LOGGER.error("Error removing orphaned binding with id '" + bindingId + "' for service instance " + serviceInstanceId, e);
				}
			}
		}

	}

}
