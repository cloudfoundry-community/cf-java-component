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
 * Contains the parameters received from the Cloud Controller during a service bind.
 *
 * @author Mike Heath
 */
public class BindRequest {

	private final UUID serviceInstanceGuid;
	private final UUID bindingGuid;
	private final UUID applicationGuid;
	private final Binding binding;
	private final String planId;


	public BindRequest(UUID serviceInstanceGuid, UUID bindingGuid, BindingType type, String boundResource, String planId) {
		this.serviceInstanceGuid = serviceInstanceGuid;
		this.bindingGuid = bindingGuid;
		if (type == BindingType.APPLICATION) {
			this.applicationGuid = UUID.fromString(boundResource);
		} else {
			this.applicationGuid = null;
		}
		this.binding = new Binding(type, boundResource);
		this.planId = planId;
	}

	/**
	 * The GUID of the servic instance being bound.
	 */
	public UUID getServiceInstanceGuid() {
		return serviceInstanceGuid;
	}

	public Binding getBoundResource() {
		return binding;
	}

	/**
	 * The GUID from the Cloud Controller that identifies the bind
	 */
	public UUID getBindingGuid() {
		return bindingGuid;
	}

	/**
	 * The GUID of the application being bound.
	 *
	 * @deprecated Use #getBinding() instead.
	 */
	@Deprecated
	public UUID getApplicationGuid() {
		return applicationGuid;
	}

	/**
	 * The plan id. Matches the value set in {@link cf.spring.servicebroker.ServicePlan#id()}.
	 */
	public String getPlanId() {
		return planId;
	}

	public enum BindingType {
		APPLICATION,
		ROUTE
	}

	public class Binding {
		private final BindingType type;
		private final String resource;

		private Binding(BindingType type, String resource) {
			this.type = type;
			this.resource = resource;
		}

		public BindingType getType() {
			return type;
		}

		public String getResource() {
			return resource;
		}
	}
}
