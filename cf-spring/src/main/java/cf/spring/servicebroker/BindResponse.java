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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * The response returned to the Cloud Controller after a successful bind. Methods annotated with {@code @Bind} must
 * return an instance of this type.
 *
 * @author Mike Heath
 */
public class BindResponse extends JsonObject {

	private final Object credentials;
	private final String syslogDrainUrl;
	private final String routeServiceUrl;
	private final List<VolumeMount> volumeMounts;

	private final boolean created;

	/**
	 * Creates a bind response.
	 *
	 * @param credentials credentials sent to the Cloud Controller. This must be a Jackson serializable JSON object.
	 */
	public BindResponse(Object credentials) {
		this(credentials, null);
	}

	/**
	 * Creates a bind response.
	 *
	 * @param credentials credentials sent to the Cloud Controller. This must be a Jackson serializable JSON object.
	 * @param syslogDrainUrl this should be a URL to a syslog endpoint.  Null if not applicable.
	 */
	public BindResponse(Object credentials, String syslogDrainUrl) {
		this(credentials, syslogDrainUrl, true);
	}

	/**
	 * Creates a bind response.
	 *
	 * @param credentials credentials sent to the Cloud Controller. This must be a Jackson serializable JSON object.
	 * @param syslogDrainUrl this should be a URL to a syslog endpoint.  Null if not applicable.
	 * @param created indicates to the Cloud Controller if the binding was created or {@code false} if the binding
	 *                already existed. This has no functional impact on binding a service.
	 */
	public BindResponse(Object credentials, String syslogDrainUrl, boolean created) {
		this(credentials, syslogDrainUrl, null, created);
	}

	/**
	 * Creates a bind response.
	 *
	 * @param volumeMounts this should be a list of volumes to mount in containers with this service.  Null if not applicable.
	 */
	public BindResponse(List<VolumeMount> volumeMounts) {
		this(JsonNodeFactory.instance.objectNode().put("empty", true), null, null, volumeMounts, true);
	}

	/**
	 * Creates a bind response.
	 *
	 * @param credentials credentials sent to the Cloud Controller. This must be a Jackson serializable JSON object.
	 * @param syslogDrainUrl this should be a URL to a syslog endpoint.  Null if not applicable.
	 * @param routeServiceUrl this should be an https URL to a route service proxy.  Null if not applicable.
	 * @param created indicates to the Cloud Controller if the binding was created or {@code false} if the binding
	 *                already existed. This has no functional impact on binding a service.
	 */
	public BindResponse(Object credentials, String syslogDrainUrl, String routeServiceUrl, boolean created) {
		this(credentials, syslogDrainUrl, routeServiceUrl, null, created);
	}

	/**
	 * Creates a bind response.
	 *
	 * @param credentials credentials sent to the Cloud Controller. This must be a Jackson serializable JSON object.
	 * @param syslogDrainUrl this should be a URL to a syslog endpoint.  Null if not applicable.
	 * @param routeServiceUrl this should be an https URL to a route service proxy.  Null if not applicable.
	 * @param volumeMounts this should be a list of volumes to mount in containers with this service.  Null if not applicable.
	 * @param created indicates to the Cloud Controller if the binding was created or {@code false} if the binding
	 *                already existed. This has no functional impact on binding a service.
	 */
	public BindResponse(Object credentials, String syslogDrainUrl, String routeServiceUrl, List<VolumeMount> volumeMounts, boolean created) {
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
		this.created = created;
		this.routeServiceUrl = routeServiceUrl;
		this.volumeMounts = volumeMounts;
	}

	/**
	 * The credentials for this service binding.
	 */
	public Object getCredentials() {
		return credentials;
	}

	/**
	 * The syslog drain URL that receives all of the application's loggregator logs.
	 */
	@JsonProperty("syslog_drain_url")
	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	/**
	 * The route service URL that requests should proxy through for a route service.
	 */
	@JsonProperty("route_service_url")
	public String getRouteServiceUrl() {
		return routeServiceUrl;
	}
	
	/**
	 * The volume mounts this service wishes to inject into the container.
	 */
	@JsonProperty("volume_mounts")
	public List<VolumeMount> getVolumeMounts() {
		return volumeMounts;
	}

	/**
	 * Returns {@code true} if the binding was created, {@code false} if the binding already exists. This has no
	 * functional impact on binding a service.
	 *
	 * @return {@code true} if the binding was created, {@code false} if the binding already exists.
	 */
	@JsonIgnore
	public boolean isCreated() {
		return created;
	}
}
