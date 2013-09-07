package cf.service;

import cf.common.JsonObject;
import cf.component.http.RequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public abstract class AbstractBrokerServer {

	public static final String SERVICE_INSTANCE_ID = "service_id";
	public static final String SERVICE_BINDING_ID = "binding_id";

	public static final String VCAP_SERVICE_TOKEN_HEADER = "X-VCAP-Service-Token";

	final private ObjectMapper mapper = new ObjectMapper();

	private final String authToken;
	private final Provisioner provisioner;

	public AbstractBrokerServer(String authToken, Provisioner provisioner) {
		if (authToken == null) {
			throw new IllegalArgumentException("authToken can not be null");
		}
		this.authToken = authToken;
		this.provisioner = provisioner;

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected CreateResponse createService(CreateRequest createRequest) {
		logger().info("Creating instance for service {} in space {} in org {}",
				createRequest.getLabel(),
				createRequest.getSpaceGuid(),
				createRequest.getOrganizationGuid());
		final ServiceInstance serviceInstance = provisioner.create(createRequest);
		final ObjectNode gatewayData = mapper.createObjectNode();
		putAll(gatewayData, serviceInstance.getGatewayData());
		gatewayData.put(SERVICE_INSTANCE_ID, serviceInstance.getInstanceId());

		final ObjectNode credentials = mapper.createObjectNode();
		putAll(credentials, serviceInstance.getCredentials());

		return new CreateResponse(serviceInstance.getInstanceId(), gatewayData, credentials);
	}

	protected void deleteService(String serviceInstanceId) {
		logger().info("Deleting service instance {}", serviceInstanceId);
		provisioner.delete(serviceInstanceId);
	}

	protected BindResponse bindService(BindRequest bindRequest) {
		logger().info("Binding service {} for instance {}", bindRequest.getLabel(), bindRequest.getServiceInstanceId());
		final ServiceBinding serviceBinding = provisioner.bind(bindRequest);
		final Map<String,Object> gatewayData = new HashMap<>(serviceBinding.getGatewayData());
		gatewayData.put(SERVICE_INSTANCE_ID, serviceBinding.getInstanceId());
		gatewayData.put(SERVICE_BINDING_ID, serviceBinding.getBindingId());
		return new BindResponse(serviceBinding.getBindingId(), gatewayData, serviceBinding.getCredentials());

	}

	protected void unbindService(String serviceInstanceId, String handleId) {
		logger().info("Unbinding instance {} for binding {}", serviceInstanceId, handleId);
		provisioner.unbind(serviceInstanceId, handleId);
	}

	protected abstract Logger logger();

	protected boolean isValidAuthToken(String authToken) {
		return this.authToken.equals(authToken);
	}

	protected String toString(JsonObject object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> T decode(Class<T> type, ByteBuf body) throws RequestException {
		try {
			return mapper.readValue(body.array(), type);
		} catch (IOException e) {
			throw new RequestException(e);
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
