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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the method that should be invoked when the service broker receives a deprovision request. Methods annotated with
 * {@code @Deprovision} must take a single argument of type {@link cf.spring.servicebroker.DeprovisionRequest} and
 * have a return type of {@code void}.
 *
 * <p>If the specified service instance cannot be found, the method annotated with {@code @Deprovision} should throw a
 * {@link cf.spring.servicebroker.MissingResourceException}.</p>
 *
 * @author Mike Heath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Deprovision {
}
