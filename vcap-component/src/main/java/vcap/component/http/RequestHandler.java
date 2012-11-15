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

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Handles HTTP requests. Register instances of this class with {@link SimpleHttpServer} using
 * {@link SimpleHttpServer#addHandler(String, RequestHandler)} to handle a particular URI.
 *
* @author Mike Heath <elcapo@gmail.com>
*/
public interface RequestHandler {

	/**
	 * Handles an HTTP request.
	 *
	 * @param request the request from the client.
	 * @return the response to be sent to the client.
	 */
	HttpResponse handleRequest(HttpRequest request) throws RequestException;

}
