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

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for Cloud Foundry v2 service brokers. Applying this annotation to a Spring config class will
 * automatically register a Spring MVC handler to publish service broker information to the Cloud Controller.
 *
 * @author Mike Heath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ServiceBrokerConfiguration.class)
public @interface EnableServiceBroker {
	/**
	 * The username the Cloud Controller uses to authenticate service broker catalog requests. This value may be a SpEL
	 * expression.
	 *
	 * @return the username this service broker uses for authenticating requests from the Cloud Controller.
	 */
	String username() default "servicebroker";

	/**
	 * The password the Cloud Controller uses to authenticates service broker catalog requests. This value may be a
	 * SpEL expression.
	 *
	 * @return the password this service broker uses for authenticating requests from the Cloud Controller.
	 */
	String password();
}
