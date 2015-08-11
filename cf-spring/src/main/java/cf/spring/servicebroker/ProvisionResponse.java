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

import cf.common.JsonObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response returned to the Cloud Controller after a service has been successfully provisioned.
 *
 * @author Mike Heath
 */
public class ProvisionResponse extends JsonObject {

	public static final String DASHBOARD_URL_PROPERTY = "dashboard_url";

	private final String dashboardUrl;
	private final boolean created;

	/**
	 * Creates a provision response without any parameters to the Cloud Controller.
	 */
	public ProvisionResponse() {
		this(null);
	}

	/**
	 * Creates a provision response and indicates to the Cloud Controller of the service was created or if it already
	 * existed. This has no functional impact on provisioning a service.
	 *
	 * @param created {@code true} if the service was provisioned, {@code false} if the service is previously
	 *                provisioned.
	 */
	public ProvisionResponse(boolean created) {
		this(null, created);
	}

	/**
	 * Creates a provision response with the provided dashboard URL.
	 *
	 * @param dashboardUrl a dashboard URL to be stored by the Cloud Controller.
	 */
	@JsonCreator
	public ProvisionResponse(
			@JsonProperty(DASHBOARD_URL_PROPERTY) String dashboardUrl
	) {
		this(dashboardUrl, true);
	}

	/**
	 * Creates a privision response with the provided dashboard URL and indicates to the Cloud Controller of the
	 * service was created or if it already existed. This has no functional impact on provisioning a service.
	 *
	 * @param dashboardUrl a dashboard URL to be stored by the Cloud Controller.
	 * @param created {@code true} if the service was provisioned, {@code false} if the service is previously
	 *                provisioned.
	 */
	public ProvisionResponse(String dashboardUrl, boolean created) {
		this.dashboardUrl = dashboardUrl;
		this.created = created;
	}

	@JsonProperty(DASHBOARD_URL_PROPERTY)
	public String getDashboardUrl() {
		return dashboardUrl;
	}

	/**
	 * Returns {@code true} if the service was provisioned, or {@code false} if the service already existed. This has
	 * no functional impact on provisioning a service.
	 *
	 * @return {@code true} if the service was provisioned, or {@code false} if the service already existed.
	 */
	@JsonIgnore
	public boolean isCreated() {
		return created;
	}
}
