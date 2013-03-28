package vcap.service;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface Provisioner {

	/**
	 * When the user types 'vmc create-service ...' this method will get called.
	 *
	 * @param request
	 * @return
	 */
	CreateResponse create(CreateRequest request);

	/**
	 * When the user types 'vmc delete-service ...' this method will probably get called.
	 *
	 * @param serviceInstanceId
	 */
	void delete(String serviceInstanceId);

	/**
	 * When the user types 'vmc bind-service ...' this method will get called.
	 */
	BindResponse bind(BindRequest request);

	/**
	 *
	 *
	 * @param request
	 */
	void unbind(UnbindRequest request);

	/**
	 * Returns iterable for each service id for the services the gateway is aware of.
	 *
	 * @return
	 */
	Iterable<String> services();

	/**
	 * Returns all the handle ids (binds) for a service.
	 *
	 * @param serviceInstanceId
	 * @return
	 */
	Iterable<String> handles(String serviceInstanceId);

	/**
	 * This gets called when the cc deletes a service handle but the delete request didn't make it to the gateway.
	 *
	 * @param handleId
	 */
	void removeOrphanedHandle(String handleId);

	/**
	 * This gets called when the cc deletes a service instance but the delete request didn't make it to the gateway.
	 * @param serviceId
	 */
	void removeOrphanedService(String serviceId);
}
