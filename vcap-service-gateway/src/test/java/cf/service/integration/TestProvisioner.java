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
package cf.service.integration;

import cf.service.BindRequest;
import cf.service.ServiceBinding;
import cf.service.CreateRequest;
import cf.service.Provisioner;
import cf.service.ServiceInstance;

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
	public ServiceBinding bind(BindRequest request) {
		System.out.println("Binding a service!");
		Integer id = bindInvocationCount.getAndIncrement();
		return new ServiceBinding(request.getServiceInstanceId(), id.toString());
	}

	@Override
	public void unbind(String instanceId, String handleId) {
		System.out.println("Unbinding service! " + handleId);
	}

	@Override
	public Iterable<String> serviceInstanceIds() {
		return null;
	}

	@Override
	public Iterable<String> bindingIds(String instanceId) {
		return null;
	}

	@Override
	public void removeOrphanedBinding(String instanceId, String handleId) {
	}

	@Override
	public void removeOrphanedServiceInstance(String instanceId) {
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
