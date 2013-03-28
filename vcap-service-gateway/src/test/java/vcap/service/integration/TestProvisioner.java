package vcap.service.integration;

import vcap.service.BindRequest;
import vcap.service.BindResponse;
import vcap.service.CreateRequest;
import vcap.service.CreateResponse;
import vcap.service.Provisioner;

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
	public CreateResponse create(CreateRequest request) {
		System.out.println("Let's create a service!");
		final Integer id = createInvocationCount.getAndIncrement();
		lastCreateId = id;
		return new CreateResponse(id.toString(), new HashMap<>(), new HashMap<>());
	}

	@Override
	public void delete(String serviceInstanceId) {
		System.out.println("Deleting service! " + serviceInstanceId);
		lastDeleteId = Integer.valueOf(serviceInstanceId);
		deleteInvocationCount.getAndIncrement();
	}

	@Override
	public BindResponse bind(BindRequest request) {
		System.out.println("Binding a service!");
		Integer id = bindInvocationCount.getAndIncrement();
		return new BindResponse(id.toString(), new HashMap<>(), new HashMap<>());
	}

	@Override
	public void unbind(String serviceInstanceId, String handleId) {
		System.out.println("Unbinding service! " + handleId);
	}

	@Override
	public Iterable<String> services() {
		return null;
	}

	@Override
	public Iterable<String> handles(String serviceInstanceId) {
		return null;
	}

	@Override
	public void removeOrphanedHandle(String handleId) {
	}

	@Override
	public void removeOrphanedService(String serviceId) {
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
