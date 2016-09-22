/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

import static cf.spring.servicebroker.AccessorUtils.*;

import org.springframework.beans.factory.BeanCreationException;

import java.lang.reflect.Method;

/**
 * Hold information about a service broker.
 *
 * @author Mike Heath
 */
class ServiceBrokerMethods {

	private final String beanName;
	private final Method provision;
	private final Method update;
	private final Method deprovision;
	private final Method bind;
	private final Method unbind;

	ServiceBrokerMethods(String beanName, boolean bindable, Method provision, Method update, Method deprovision, Method bind, Method unbind) {
		this.beanName = beanName;
		this.provision = provision;
		this.update = update;
		this.deprovision = deprovision;
		this.bind = bind;
		this.unbind = unbind;

		validateProvisionMethod(provision);
		validateUpdateMethod(update);
		validateDeprovisionMethod(deprovision);
		validateBindMethod(bind, bindable);
		validateUnbindMethod(unbind, bindable);
	}

	public String getBeanName() {
		return beanName;
	}

	public Method getProvision() {
		return provision;
	}

	public Method getUpdate() {
		return update;
	}

	public Method getDeprovision() {
		return deprovision;
	}

	public Method getBind() {
		return bind;
	}

	public Method getUnbind() {
		return unbind;
	}

	private void validateProvisionMethod(Method provisionMethod) {
		if (provisionMethod == null) {
			throw new BeanCreationException("A bean with @" + ServiceBroker.class.getName()
					+ " must have method with @" + Provision.class.getName());
		}
		validateReturnType(provisionMethod, Provision.class, ProvisionResponse.class);
		validateArgument(provisionMethod, Provision.class, ProvisionRequest.class);

	}

	private void validateUpdateMethod(Method updateMethod) {
		if (updateMethod == null) {
			return;
		}
		validateReturnType(updateMethod, Update.class, void.class);
		validateArgument(updateMethod, Update.class, UpdateRequest.class);
	}

	private void validateDeprovisionMethod(Method deprovisionMethod) {
		if (deprovisionMethod == null) {
			return;
		}
		validateReturnType(deprovisionMethod, Deprovision.class, void.class);
		validateArgument(deprovisionMethod, Deprovision.class, DeprovisionRequest.class);
	}

	private void validateBindMethod(Method bindMethod, boolean bindable) {
		if (bindable && bindMethod == null) {
			throw new BeanCreationException("Bindable service brokers must have a method with @" + Bind.class.getName());
		}
		if (!bindable && bindMethod != null) {
			throw new BeanCreationException("Service broker on class " + bindMethod.getDeclaringClass().getName()
				  + " is NOT bindable but has a method annotated with @" + Bind.class.getName());
		}
		if (bindMethod == null) {
			return;
		}
		validateReturnType(bindMethod, Bind.class, BindResponse.class);
		validateArgument(bindMethod, Bind.class, BindRequest.class);
	}

	private void validateUnbindMethod(Method unbindMethod, boolean bindable) {
		if (unbindMethod == null) {
			return;
		}
		if (!bindable) {
			throw new BeanCreationException("Service broker on class " + unbindMethod.getDeclaringClass().getName()
				  + " is NOT bindable but has a method annotated with @" + Unbind.class.getName());
		}
		validateReturnType(unbindMethod, Unbind.class, void.class);
		validateArgument(unbindMethod, Bind.class, UnbindRequest.class);
	}

}
