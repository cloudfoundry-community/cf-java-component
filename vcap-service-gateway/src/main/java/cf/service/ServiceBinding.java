package cf.service;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBinding extends ServiceInstance {

	private final String bindingId;

	public ServiceBinding(String id, String bindingId) {
		super(id);
		this.bindingId = bindingId;
	}

	public String getBindingId() {
		return bindingId;
	}
}
