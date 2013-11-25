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

/**
 * Service providers must implement this interface.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface Provisioner {

	/**
	 * When the user types 'cf create-service ...' this method will get called.
	 *
	 * @param request the create request sent by the Cloud Controller
	 * @return
	 */
	ServiceInstance create(CreateRequest request) throws ServiceBrokerException;

	/**
	 * When the user types 'cf delete-service ...' this method will probably get called.
	 *
	 * @param instanceId the id of the service instance to be deleted. The value of this is the instance id that is
	 *                   returned by {@link #create(CreateRequest)}.
	 */
	void delete(String instanceId) throws ServiceBrokerException;

	/**
	 * When the user types 'cf bind-service ...' this method will get called.
	 */
	ServiceBinding bind(BindRequest request) throws ServiceBrokerException;

	/**
	 * When the user types 'cf unbind-service ...' this method will probably get called.
	 *
	 * @param instanceId
	 * @param bindingId
	 */
	void unbind(String instanceId, String bindingId) throws ServiceBrokerException;

	/**
	 * Returns iterable for each service ids for the services the gateway is aware of.
	 *
	 * @return service ids for the services the gateway is aware of, or {@code null} if this service gateway does not
	 *         track service instances.
	 */
	Iterable<String> serviceInstanceIds();

	/**
	 * Returns binding ids for the given service instance id.
	 *
	 * @return binding ids for the given service instance id, or {@code null} if this service broker does not track
	 *         bindings.
	 */
	Iterable<String> bindingIds(String instanceId);

	/**
	 * This gets called when the cc deletes a service binding but the delete request didn't make it to the broker.
	 */
	void removeOrphanedBinding(String instanceId, String bindingId);

	/**
	 * This gets called when the cc deletes a service instance but the delete request didn't make it to the broker.
	 * @param instanceId
	 */
	void removeOrphanedServiceInstance(String instanceId);
}
