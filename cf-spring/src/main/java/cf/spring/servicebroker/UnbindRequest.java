/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

import java.util.UUID;

/**
 * Contains the parameters received from the Cloud Controller during a service unbind.
 *
 * @author Mike Heath
 */
public class UnbindRequest {
	private final UUID bindingGuid;
	private final UUID serviceInstanceGuid;
	private final String planId;

	public UnbindRequest(UUID bindingGuid, UUID serviceInstanceGuid, String planId) {
		this.bindingGuid = bindingGuid;
		this.serviceInstanceGuid = serviceInstanceGuid;
		this.planId = planId;
	}

	/**
	 * The GUID of the service binding.
	 */
	public UUID getBindingGuid() {
		return bindingGuid;
	}

	/**
	 * The GUID of the service instance.
	 */
	public UUID getServiceInstanceGuid() {
		return serviceInstanceGuid;
	}

	/**
	 * The plan id. Matches the value set in {@link cf.spring.servicebroker.ServicePlan#id()}.
	 */
	public String getPlanId() {
		return planId;
	}
}
