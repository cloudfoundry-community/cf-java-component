package vcap.service;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface Provisioner {

	/**
	 * When the user types 'cf create-service ...' this method will get called.
	 *
	 * @param request
	 * @return
	 */
	ServiceInstance create(CreateRequest request);

	/**
	 * When the user types 'cf delete-service ...' this method will probably get called.
	 *
	 * @param instanceId
	 */
	void delete(String instanceId);

	/**
	 * When the user types 'cf bind-service ...' this method will get called.
	 */
	Binding bind(BindRequest request);

	/**
	 *
	 * @param instanceId
	 * @param bindingId
	 */
	void unbind(String instanceId, String bindingId);

	/**
	 * Returns iterable for each service id for the services the gateway is aware of.
	 *
	 * @return
	 */
	Iterable<String> services();

	/**
	 * Returns all the handle ids (binds) for a service.
	 *
	 * @param instanceId
	 * @return
	 */
	Iterable<String> handles(String instanceId);

	/**
	 * This gets called when the cc deletes a service binding but the delete request didn't make it to the gateway.
	 *
	 * @param bindingId
	 */
	void removeOrphanedBinding(String bindingId);

	/**
	 * This gets called when the cc deletes a service instance but the delete request didn't make it to the gateway.
	 * @param instanceId
	 */
	void removeOrphanedService(String instanceId);
}
