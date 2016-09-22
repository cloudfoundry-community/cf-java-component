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

import java.util.Map;
import java.util.UUID;

/**
 * Contains the parameters received from the Cloud Controller during a service provision.

 * @author Mike Youngstrom
 */
public class UpdateRequest {

	private final UUID serviceInstanceGuid;
	private final String planId;
	private final Map<String, Object> parameters;
	private final PreviousValues previousValues;

	public UpdateRequest(UUID serviceInstanceGuid, String planId, Map<String, Object> parameters, PreviousValues previousValues) {
		this.serviceInstanceGuid = serviceInstanceGuid;
		this.planId = planId;
		this.parameters = parameters;
		this.previousValues = previousValues;
	}

	/**
	 * The service instance being updated. 
	 */
	public UUID getServiceInstanceGuid() {
		return serviceInstanceGuid;
	}

	/**
	 * ID of the new plan from the catalog.
	 */
	public String getPlanId() {
		return planId;
	}

	/**
	 * Cloud Foundry API clients can provide a JSON object of configuration parameters with their request and this value will be passed through to the service broker. Brokers are responsible for validation.
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Information about the instance prior to the update.
	 */
	public PreviousValues getPreviousValues() {
		return previousValues;
	}

	public static class PreviousValues {

		private final String serviceId;
		private final String planId;
		private final UUID organizationId;
		private final UUID spaceId;

		public PreviousValues(String serviceId, String planId, UUID organizationId, UUID spaceId) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.organizationId = organizationId;
			this.spaceId = spaceId;
		}

		/**
		 * ID of the service for the instance.
		 */
		public String getServiceId() {
			return serviceId;
		}

		/**
		 * ID of the plan prior to the update.
		 */
		public String getPlanId() {
			return planId;
		}

		/**
		 * ID of the organization containing the instance.
		 */
		public UUID getOrganizationId() {
			return organizationId;
		}

		/**
		 * ID of the space containing the instance.
		 */
		public UUID getSpaceId() {
			return spaceId;
		}
	}
}
