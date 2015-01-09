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
 * Describes the credentials used by the dashboard (of a {@link Service}) to
 * authenticate the current user on the UAA.
 * <p/>
 * The dashboard client is optional, in this case all fields must be
 * empty. If a field is not empty, all fields must be not empty.
 *
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface DashboardClient {

    /**
     * The id of the Oauth2 client that the dashboard intends to use.
     */
    String id() default "";

    /**
     * The secret of the dashboard client
     */
    String secret() default "";

    /**
     * A domain for the service dashboard that will be whitelisted
     * by the UAA to enable SSO authentication.
     */
    String redirectUri() default "";
}
