/*
 *   Copyright (c) 2012 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.component.http;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Thrown when an error occurs processing a request.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class RequestException extends Exception {

	final HttpResponseStatus status;

	public RequestException(HttpResponseStatus status) {
		this(status, status.reasonPhrase());
	}

	public RequestException(Throwable cause) {
		this(HttpResponseStatus.INTERNAL_SERVER_ERROR, cause);
	}

	public RequestException(HttpResponseStatus status, Throwable cause) {
		super(cause);
		this.status = status;
	}

	public RequestException(HttpResponseStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
}
