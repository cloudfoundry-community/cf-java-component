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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a Cloud Foundry service broker. This broker does not implement the actual REST endpoint. This must be
 * done using Spring MVC or some other HTTP server technology. This class is intended to act as an intermediary between
 * a set of HTTP endpoints and a user provide implementation of {@link cf.service.Provisioner}.
 *
 * @author Mike Heath
 * @deprecated Use the V2 services.
 */
@Deprecated
public class ServiceBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBroker.class);

	/**
	 * The name of the HTTP header holding the service auth token.
	 */
	public static final String VCAP_SERVICE_TOKEN_HEADER = "X-VCAP-Service-Token";

	public static final String SERVICE_INSTANCE_ID = "service_id";
	public static final String SERVICE_BINDING_ID = "binding_id";

	public static final Pattern BINDING_PATTERN = Pattern.compile("/+gateway/v1/configurations/(.*?)/handles(/(.*))?");
	public static final Pattern INSTANCE_PATTERN = Pattern.compile("/+gateway/v1/configurations(/(.*))?");

	final private ObjectMapper mapper = new ObjectMapper();

	private final String authToken;
	private final Provisioner provisioner;

	public ServiceBroker(Provisioner provisioner, String authToken) {
		if (authToken == null) {
			throw new IllegalArgumentException("authToken can not be null");
		}
		this.provisioner = provisioner;
		this.authToken = authToken;

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public String createService(String authToken, byte[] body)
			throws ServiceBrokerException {
		LOGGER.debug("Creating service");

		validateAuthToken(authToken);
		final CreateRequest createRequest = decode(CreateRequest.class, body);
		final ServiceInstance serviceInstance = provisioner.create(createRequest);
		final ObjectNode gatewayData = mapper.createObjectNode();
		putAll(gatewayData, serviceInstance.getGatewayData());
		gatewayData.put(SERVICE_INSTANCE_ID, serviceInstance.getInstanceId());

		final ObjectNode credentials = mapper.createObjectNode();
		putAll(credentials, serviceInstance.getCredentials());

		final CreateResponse response = new CreateResponse(serviceInstance.getInstanceId(), gatewayData, credentials);

		return encode(response);
	}

	public String deleteService(String authToken, String uri)
			throws ServiceBrokerException {
		LOGGER.debug("Deleting service");
		validateAuthToken(authToken);
		final Matcher matcher = INSTANCE_PATTERN.matcher(uri);
		if (!matcher.matches() && matcher.groupCount() != 2) {
			throw new ResourceNotFoundException();
		}
		final String serviceInstanceId = matcher.group(2);
		provisioner.delete(serviceInstanceId);
		return "{}";
	}

	public String bindService(String authToken, byte[] body)
			throws ServiceBrokerException {
		LOGGER.debug("Binding service");
		validateAuthToken(authToken);
		final BindRequest bindRequest = decode(BindRequest.class, body);
		final ServiceBinding serviceBinding = provisioner.bind(bindRequest);

		final Map<String,Object> gatewayData = new HashMap<>(serviceBinding.getGatewayData());
		gatewayData.put(SERVICE_INSTANCE_ID, serviceBinding.getInstanceId());
		gatewayData.put(SERVICE_BINDING_ID, serviceBinding.getBindingId());

		final BindResponse bindResponse = new BindResponse(serviceBinding.getBindingId(), gatewayData, serviceBinding.getCredentials());

		return encode(bindResponse);
	}

	public String unbindService(String authToken, String uri) throws ServiceBrokerException {
		LOGGER.debug("Unbinding service");
		validateAuthToken(authToken);
		final Matcher matcher = BINDING_PATTERN.matcher(uri);
		if (!matcher.matches() && matcher.groupCount() != 3) {
			throw new ResourceNotFoundException();
		}
		final String instanceId = matcher.group(1);
		final String bindingId = matcher.group(2);
		provisioner.unbind(instanceId, bindingId);
		return "{}";
	}

	private void validateAuthToken(String authToken) throws AuthenticationException {
		if (!this.authToken.equals(authToken)) {
			LOGGER.warn("Received invalid service-auth-token from Cloud Controller.");
			throw new AuthenticationException();
		}
	}

	private <T> T decode(Class<T> type, byte[] body) throws BadRequestException {
		try {
			return mapper.readValue(body, type);
		} catch (IOException e) {
			throw new BadRequestException();
		}
	}

	private String encode(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
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

}
