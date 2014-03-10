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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 * @deprecated Use the V2 services.
 */
@Deprecated
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
