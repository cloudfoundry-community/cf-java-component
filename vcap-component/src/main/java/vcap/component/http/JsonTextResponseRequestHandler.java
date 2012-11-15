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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
* @author Mike Heath <heathma@ldschurch.org>
*/
public abstract class JsonTextResponseRequestHandler implements RequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request) throws RequestException {
		final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
		final String body = handle(request);
		final ByteBuf buffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, Integer.valueOf(buffer.readableBytes()));
		response.setContent(buffer);
		return response;
	}

	public abstract String handle(HttpRequest request) throws RequestException;
}
