package vcap.service.integration;

import vcap.service.BindRequest;
import vcap.service.BindResponse;
import vcap.service.Binding;
import vcap.service.CreateRequest;
import vcap.service.CreateResponse;
import vcap.service.Provisioner;
import vcap.service.ServiceInstance;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class TestProvisioner implements Provisioner {

	private final AtomicInteger createInvocationCount = new AtomicInteger();
	private final AtomicInteger deleteInvocationCount = new AtomicInteger();
	private final AtomicInteger bindInvocationCount = new AtomicInteger();

	private volatile int lastCreateId;
	private volatile int lastDeleteId;

	@Override
	public ServiceInstance create(CreateRequest request) {
		System.out.println("Let's create a service!");
		final Integer id = createInvocationCount.getAndIncrement();
		lastCreateId = id;
		final ServiceInstance serviceInstance = new ServiceInstance(id.toString());
		serviceInstance.addGatewayDataField("config", "value");
		serviceInstance.addCredential("user", "yourmom");
		return serviceInstance;
	}

	@Override
	public void delete(String instanceId) {
		System.out.println("Deleting service! " + instanceId);
		lastDeleteId = Integer.valueOf(instanceId);
		deleteInvocationCount.getAndIncrement();
	}

	@Override
	public Binding bind(BindRequest request) {
		System.out.println("Binding a service!");
		Integer id = bindInvocationCount.getAndIncrement();
		return new Binding(request.getServiceInstanceId(), id.toString());
	}

	@Override
	public void unbind(String instanceId, String handleId) {
		System.out.println("Unbinding service! " + handleId);
	}

	@Override
	public Iterable<String> services() {
		return null;
	}

	@Override
	public Iterable<String> handles(String instanceId) {
		return null;
	}

	@Override
	public void removeOrphanedBinding(String handleId) {
	}

	@Override
	public void removeOrphanedService(String instanceId) {
	}

	public int getCreateInvocationCount() {
		return createInvocationCount.get();
	}

	public int getDeleteInvocationCount() {
		return deleteInvocationCount.get();
	}

	public int getBindInvocationCount() {
		return bindInvocationCount.get();
	}

	public int getLastCreateId() {
		return lastCreateId;
	}

	public int getLastDeleteId() {
		return lastDeleteId;
	}
}
