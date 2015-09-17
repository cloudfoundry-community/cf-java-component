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
package cf.service;

/**
 * Root exception for all service broker exceptions.
 *
 * @author Mike Heath
 * @deprecated Use the V2 services.
 */
@Deprecated
public class ServiceBrokerException extends Exception {

	public ServiceBrokerException() {
	}

	public ServiceBrokerException(String message) {
		super(message);
	}

	public ServiceBrokerException(String message, Throwable cause) {
		super(message, cause);
	}
}
