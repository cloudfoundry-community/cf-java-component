package vcap.service;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface Provisioner {

	/**
	 * Returns the guid for the service being provisioned by this class.
	 *
	 * @return the guid for the service being provisioned by this class.
	 */
	// TODO Move this. Provisioner shouldn't be responsible for tracking service UUID.
	UUID getServiceGuid();

	/**
	 * When the user types 'cf create-service ...' this method will get called.
	 *
	 * @param request the create request sent by the Cloud Controller
	 * @return
	 */
	ServiceInstance create(CreateRequest request);

	/**
	 * When the user types 'cf delete-service ...' this method will probably get called.
	 *
	 * @param instanceId the id of the service instance to be deleted. The value of this is the instance id that is
	 *                   returned by {@link #create(CreateRequest)}.
	 */
	void delete(String instanceId);

	/**
	 * When the user types 'cf bind-service ...' this method will get called.
	 */
	ServiceBinding bind(BindRequest request);

	/**
	 * When the user types 'cf unbind-service ...' this method will probably get called.
	 *
	 * @param instanceId
	 * @param bindingId
	 */
	void unbind(String instanceId, String bindingId);

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
	 * @return binding ids for the given service instance id, or {@code null} if this service gateway does not track
	 *         bindings.
	 */
	Iterable<String> bindingIds(String instanceId);

	/**
	 * This gets called when the cc deletes a service binding but the delete request didn't make it to the gateway.
	 */
	void removeOrphanedBinding(String instanceId, String bindingId);

	/**
	 * This gets called when the cc deletes a service instance but the delete request didn't make it to the gateway.
	 * @param instanceId
	 */
	void removeOrphanedServiceInstance(String instanceId);
}
