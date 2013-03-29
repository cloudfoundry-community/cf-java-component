package vcap.service;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Binding extends ServiceInstance {

	private final String bindingId;

	public Binding(String id, String bindingId) {
		super(id);
		this.bindingId = bindingId;
	}

	public String getBindingId() {
		return bindingId;
	}
}
