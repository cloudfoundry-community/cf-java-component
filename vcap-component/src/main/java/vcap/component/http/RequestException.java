/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
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
package vcap.component.http;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Thrown when an error occurs processing a request.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class RequestException extends Exception {

	final HttpResponseStatus status;

	public RequestException(HttpResponseStatus status) {
		this.status = status;
	}

	public RequestException(Throwable cause) {
		super(cause);
		status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
}
