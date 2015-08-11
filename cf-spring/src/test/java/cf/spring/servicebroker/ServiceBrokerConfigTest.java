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

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Mike Heath
 */
public class ServiceBrokerConfigTest {

	@Configuration
	@EnableServiceBroker(password = "somepassword")
	@ServiceBroker(@Service(id = "id", name = "name", description = "Test", plans = {}, bindable = "false"))
	static class MissingProvisionAnnotationServiceBrokerConfig {}

	@Test
	public void serviceBrokerRequiresProvisionAnnotation() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(MissingProvisionAnnotationServiceBrokerConfig.class)) {
			fail("Should have thrown " + BeanCreationException.class.getName());
		} catch(BeanCreationException e) {
			assertTrue(e.getMessage().contains("must have method with @cf.spring.servicebroker.Provision"));
		}
	}

	@Configuration
	@EnableServiceBroker(password = "somepassword")
	@ServiceBroker(@Service(id = "id", name = "name", description = "Test", plans = {}, bindable = "false"))
	static class DoubleProvisionAnnotationServiceBrokerConfig {
		@Provision
		ProvisionResponse provision1(ProvisionRequest request) { return null; }
		@Provision
		ProvisionResponse provision2(ProvisionRequest request) { return null; }
	}

	@Test
	public void serviceBrokerRequiresSingleProvisionAnnotation() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(DoubleProvisionAnnotationServiceBrokerConfig.class)) {
			fail("Should have thrown " + BeanCreationException.class.getName());
		} catch(BeanCreationException e) {
			assertTrue(e.getMessage().contains("ONE method with @cf.spring.servicebroker.Provision"));
		}
	}

	@Configuration
	@EnableServiceBroker(password = "somepassword")
	@ServiceBroker(@Service(id = "id", name = "name", description = "Test", plans = {}, bindable = "false"))
	static class WrongReturnTypeProvisionAnnotationServiceBrokerConfig {
		@Provision
		void provision(ProvisionRequest request) { }
	}

	@Test
	public void serviceBrokerWrongReturnType() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(WrongReturnTypeProvisionAnnotationServiceBrokerConfig.class)) {
			fail("Should have thrown " + BeanCreationException.class.getName());
		} catch(BeanCreationException e) {
			assertTrue(e.getMessage().contains("must have a return type of " + ProvisionResponse.class.getName()));
		}
	}

	@Configuration
	@EnableServiceBroker(password = "somepassword")
	@ServiceBroker(@Service(id = "id", name = "name", description = "Test", plans = {}, bindable = "false"))
	static class WrongParameterTypeProvisionAnnotationServiceBrokerConfig {
		@Provision
		ProvisionResponse provision(Object request) { return null; }
	}

	@Test
	public void serviceBrokerWrongParameterType() {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(WrongParameterTypeProvisionAnnotationServiceBrokerConfig.class)) {
			fail("Should have thrown " + BeanCreationException.class.getName());
		} catch(BeanCreationException e) {
			assertTrue(e.getMessage().contains("MUST take a single argument of type " + ProvisionRequest.class.getName()));
		}
	}


}
