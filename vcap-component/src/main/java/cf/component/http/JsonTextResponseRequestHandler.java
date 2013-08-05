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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.util.regex.Matcher;

/**
* @author Mike Heath <heathma@ldschurch.org>
*/
public abstract class JsonTextResponseRequestHandler implements RequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
		final String responseBody = handle(request, uriMatcher, body);
		final ByteBuf buffer = Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8);
		final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
		final HttpHeaders headers = response.headers();
		headers.add(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
		headers.add(HttpHeaders.Names.CONTENT_LENGTH, buffer.readableBytes());
		return response;
	}

	public abstract String handle(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException;
}
