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

import cf.common.JsonObject;
import cf.component.http.RequestException;
import cf.component.http.RequestHandler;
import cf.component.http.SimpleHttpServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NettyBrokerServer extends AbstractBrokerServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyBrokerServer.class);

	private static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();

	public NettyBrokerServer(SimpleHttpServer server, Provisioner provisioner, String authToken) {
		super(provisioner, authToken);
		server.addHandler(Pattern.compile("/+gateway/v1/configurations/(.*?)/handles(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				validateAuthToken(request);
				// Bind service
				if (request.getMethod() == HttpMethod.POST) {
					final BindRequest bindRequest = decode(BindRequest.class, body);
					final BindResponse bindResponse = bindService(bindRequest);
					return encodeResponse(bindResponse);
				}
				// Unbind service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 3) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(1);
					final String handleId = uriMatcher.group(3);
					unbindService(serviceInstanceId, handleId);
					return encodeResponse(EMPTY_JSON_OBJECT);
				}
				throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		});
		server.addHandler(Pattern.compile("/+gateway/v1/configurations(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				validateAuthToken(request);
				// Create service
				if (request.getMethod() == HttpMethod.POST) {
					final CreateRequest createRequest = decode(CreateRequest.class, body);
					final CreateResponse createResponse = createService(createRequest);
					return encodeResponse(createResponse);
				}
				// Delete service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 2) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(2);
					deleteService(serviceInstanceId);
					return encodeResponse(EMPTY_JSON_OBJECT);
				}
				throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		});
	}

	private void validateAuthToken(HttpRequest request) throws RequestException {
		final String authToken = request.headers().get(VCAP_SERVICE_TOKEN_HEADER);
		if (!isValidAuthToken(authToken)) {
			throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
		}
	}

	private HttpResponse encodeResponse(JsonObject jsonBody) throws RequestException {
		final String json = toString(jsonBody);
		LOGGER.debug("JSON response to server {}", json);
		final byte[] bytes = json.getBytes();
		final ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
		final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
		response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, bytes.length);
		return response;
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
}
