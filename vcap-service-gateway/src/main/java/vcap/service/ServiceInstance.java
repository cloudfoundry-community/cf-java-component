package vcap.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceInstance {

	private final String instanceId;
	private final Map<String, Object> gatewayData = new HashMap<>();
	private final Map<String, Object> credentials = new HashMap<>();

	public ServiceInstance(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public Map<String, Object> getGatewayData() {
		return gatewayData;
	}

	public Map<String, Object> getCredentials() {
		return credentials;
	}

	public void addGatewayDataField(String key, Object value) {
		gatewayData.put(key, value);
	}

	public void addCredential(String key, Object value) {
		credentials.put(key, value);
	}
}
