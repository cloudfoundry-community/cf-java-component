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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableAutoConfiguration
@EnableServiceBroker(password = "password")
@ServiceBroker(@Service(id = "test-broker",name = "test-broker",description = "#{'This is a broker used for testing ' + counter.incrementAndGet()}",
		metadata = {
			@Metadata(field = "lds", value = "Is this thing on?"),
			@Metadata(field = Metadata.TAGS, value = {"meta-one", "meta-two"})
		},
		tags = "lds",
		plans = {
			@ServicePlan(id = "different-plan", name = "some-other-plan", description = "Plan plan plan.")
		}
))
public class TestBroker {

	@Provision
	public ProvisionResponse provision(ProvisionRequest request) {
		System.out.println("Provisioning instance " + request.getInstanceGuid());
		return new ProvisionResponse();
	}

	@Update
	public void update(UpdateRequest request) {
		System.out.println("Update instance " + request.getServiceInstanceGuid());
	}

	@Bind
	public BindResponse bind(BindRequest request) {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("secret", "This is your credentials. Be careful!");
		return new BindResponse(credentials);
	}

	@Unbind
	public void unbind(UnbindRequest request) {
		System.out.println("Removing binding " + request.getBindingGuid());
	}

	@Deprovision
	public void deprovision(DeprovisionRequest request) {
		System.out.println("Delete service instance: " + request.getInstanceGuid());
	}

	@Bean
	AtomicInteger counter() {
		return new AtomicInteger();
	}

	public static void main(String[] args) {
		new SpringApplication(TestBroker.class).run(args);
		System.out.println("Test service broker started.");
	}
}
