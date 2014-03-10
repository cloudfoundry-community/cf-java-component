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

/**
 * Root exception that services brokers implementations may throw to send specific HTTP error codes back to the Cloud
 * Controller.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBrokerException extends RuntimeException {

	private final int httpResponseCode;

	public ServiceBrokerException(int httpResponseCode, String message) {
		super(message);
		this.httpResponseCode = httpResponseCode;
	}

	public ServiceBrokerException(int httpResponseCode, String message, Throwable cause) {
		super(message, cause);
		this.httpResponseCode = httpResponseCode;
	}

	public ServiceBrokerException(int httpResponseCode, Throwable cause) {
		super(cause);
		this.httpResponseCode = httpResponseCode;
	}

	public int getHttpResponseCode() {
		return httpResponseCode;
	}
}
