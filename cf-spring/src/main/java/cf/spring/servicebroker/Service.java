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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes the attributes of a service that get published in the /v2/catalog endpoint.
 *
 * @author Mike Heath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Service {
	/**
	 * The unique id of the service.
	 */
	String id();

	/**
	 * The name of the service.
	 */
	String name();

	/**
	 * A single-line description of the service.
	 */
	String description();

	/**
	 * Indicates if this service is bindable or not.
	 */
	String bindable() default "true";

	/**
	 * Tags for the services. These tags are made available to applications in the "VCAP_SERVICES" environment variable.
	 */
	String[] tags() default {};

	/**
	 * The service plans associated with this service.
	 */
	ServicePlan[] plans();

	/**
	 * The permissions requires to use this service.
	 */
	Permission[] requires() default {};

	/**
	 * The credentials used by the dashboard to authenticate the current user
	 * on the UAA.
	 */
	DashboardClient dashboardClient() default @DashboardClient;

	/**
	 * Metadata associated with this service.
	 */
	Metadata[] metadata() default {};

	/**
	 * Does this service support updating plans.
	 */
	String planUpdatable() default "false";

}
