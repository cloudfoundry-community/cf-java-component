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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cf.common.JsonObject;
import cf.component.http.RequestException;
import cf.component.http.RequestHandler;
import cf.component.http.SimpleHttpServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class GatewayServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayServer.class);

	public static final String SERVICE_INSTANCE_ID = "service_id";
	public static final String SERVICE_BINDING_ID = "binding_id";

	public static final String VCAP_SERVICE_TOKEN_HEADER = "X-VCAP-Service-Token";

	private static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();
	final ObjectMapper mapper = new ObjectMapper();

	private final String authToken;

	public GatewayServer(SimpleHttpServer server, final Provisioner provisioner, String authToken) {
		if (authToken == null) {
			throw new IllegalArgumentException("authToken can not be null");
		}
		this.authToken = authToken;
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		server.addHandler(Pattern.compile("/+gateway/v1/configurations/(.*?)/handles(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				validateAuthToken(request);
				// Bind service
				if (request.getMethod() == HttpMethod.POST) {
					final BindRequest bindRequest = decode(BindRequest.class, body);
					LOGGER.info("Binding service {} for instance {}", bindRequest.getLabel(), bindRequest.getServiceInstanceId());
					final ServiceBinding serviceBinding = provisioner.bind(bindRequest);
					final Map<String,Object> gatewayData = new HashMap<>(serviceBinding.getGatewayData());
					gatewayData.put(SERVICE_INSTANCE_ID, serviceBinding.getInstanceId());
					gatewayData.put(SERVICE_BINDING_ID, serviceBinding.getBindingId());
					final BindResponse bindResponse = new BindResponse(serviceBinding.getBindingId(), gatewayData, serviceBinding.getCredentials());
					return encodeResponse(bindResponse);
				}
				// Unbind service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 3) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(1);
					final String handleId = uriMatcher.group(3);
					LOGGER.info("Unbinding instance {} for binding {}", serviceInstanceId, handleId);
					provisioner.unbind(serviceInstanceId, handleId);
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
					LOGGER.info("Creating instance for service {} in space {} in org {}", new Object[]{
							createRequest.getLabel(),
							createRequest.getSpaceGuid(),
							createRequest.getOrganizationGuid()});
					final ServiceInstance serviceInstance = provisioner.create(createRequest);
					final ObjectNode gatewayData = mapper.createObjectNode();
					putAll(gatewayData, serviceInstance.getGatewayData());
					gatewayData.put(SERVICE_INSTANCE_ID, serviceInstance.getInstanceId());

					final ObjectNode credentials = mapper.createObjectNode();
					putAll(credentials, serviceInstance.getCredentials());

					final CreateResponse createResponse = new CreateResponse(serviceInstance.getInstanceId(), gatewayData, credentials);
					return encodeResponse(createResponse);
				}
				// Delete service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 2) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(2);
					LOGGER.info("Deleting service instance {}", serviceInstanceId);
					provisioner.delete(serviceInstanceId);
					return encodeResponse(EMPTY_JSON_OBJECT);
				}
				throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		});
	}

	private void putAll(ObjectNode object, Map<String,Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			final JsonNode value;
			if (entry.getValue() instanceof JsonNode) {
				value = (JsonNode) entry.getValue();
			} else {
				value = mapper.valueToTree(entry.getValue());
			}
			object.put(entry.getKey(), value);
		}
	}

	private void validateAuthToken(HttpRequest request) throws RequestException {
		final String authToken = request.headers().get(VCAP_SERVICE_TOKEN_HEADER);
		if (!this.authToken.equals(authToken)) {
			throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
		}
	}

	private HttpResponse encodeResponse(JsonObject jsonBody) throws RequestException {
		try {
			final String json = mapper.writeValueAsString(jsonBody);
			LOGGER.debug("JSON response to server {}", json);
			final byte[] bytes = json.getBytes();
			final ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
			final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
			response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
			response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, bytes.length);
			return response;
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	private <T> T decode(Class<T> type, ByteBuf body) throws RequestException {
		try {
			return mapper.readValue(body.array(), type);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

}
