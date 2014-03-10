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

import javax.servlet.http.HttpServletResponse;

/**
 * Thrown when a requested resource cannot be found. This triggers an HTTP 404 response.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NotFoundException extends ServiceBrokerException {

	public NotFoundException(String message) {
		super(HttpServletResponse.SC_NOT_FOUND, message);
	}

	public NotFoundException(String message, Throwable t) {
		super(HttpServletResponse.SC_NOT_FOUND, message, t);
	}
}
