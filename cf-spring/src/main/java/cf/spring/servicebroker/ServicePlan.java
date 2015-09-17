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
 * Describes the attributes of a service plan that get published in the /v2/catalog endpoint.
 * @author Mike Heath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface ServicePlan {
	/**
	 * The unique id of the plan.
	 */
	String id();

	/**
	 * The name of the plan.
	 */
	String name();

	/**
	 * A single-line description of the plan.
	 */
	String description();

	/**
	 * llows the plan to be limited by the non_basic_services_allowed field in a Cloud Foundry Quota. See
	 * http://docs.cloudfoundry.org/running/managing-cf/quota-plans.html.
	 * @return
	 */
	String free() default "true";

	/**
	 * Metadata associated with this service plan.
	 */
	Metadata[] metadata() default {};
}
