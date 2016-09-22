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

import java.util.List;
import java.util.Map;

import cf.common.JsonObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps to the JSON produced by "/v2/catalog" - http://docs.cloudfoundry.org/services/api.html.
 *
 * @author Mike Heath
 */
public class Catalog extends JsonObject {

	private final List<CatalogService> services;

	public Catalog(
			@JsonProperty("services") List<CatalogService> services) {
		this.services = services;
	}

	@JsonInclude(JsonInclude.Include.ALWAYS)
	public List<CatalogService> getServices() {
		return services;
	}

	public static class CatalogService extends JsonObject {

		private String id;
		private String name;
		private String description;
		private boolean bindable;
		private List<String> tags;
		private Map<String, Object> metadata;
		private List<String> requires;
		private List<Plan> plans;

		@JsonProperty("dashboard_client")
		private ServiceDashboardClient dashboardClient;

		@JsonProperty("plan_updateable")
		private Boolean planUpdatable;

		public CatalogService() {
		}

		public CatalogService(
				  String id,
				  String name,
				  String description,
				  boolean bindable,
				  List<String> tags,
				  Map<String, Object> metadata,
				  List<String> requires,
				  List<Plan> plans,
				  ServiceDashboardClient dashboardClient) {
			this(id, name, description, bindable, tags, metadata, requires, plans, dashboardClient, false);
		}

		public CatalogService(
			  @JsonProperty("id") String id,
			  @JsonProperty("name") String name,
			  @JsonProperty("description") String description,
			  @JsonProperty("bindable") boolean bindable,
			  @JsonProperty("tags") List<String> tags,
			  @JsonProperty("metadata") Map<String, Object> metadata,
			  @JsonProperty("requires") List<String> requires,
			  @JsonProperty("plans") List<Plan> plans,
			  @JsonProperty("dashboard_client") ServiceDashboardClient dashboardClient,
			  @JsonProperty("plan_updateable") Boolean planUpdatable) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.bindable = bindable;
			this.tags = tags;
			this.metadata = metadata;
			this.requires = requires;
			this.plans = plans;
			this.dashboardClient = dashboardClient;
			this.planUpdatable = planUpdatable;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public boolean isBindable() {
			return bindable;
		}

		public List<String> getTags() {
			return tags;
		}

		public Map<String, Object> getMetadata() {
			return metadata;
		}

		public List<String> getRequires() {
			return requires;
		}

		@JsonInclude(JsonInclude.Include.ALWAYS)
		public List<Plan> getPlans() {
			return plans;
		}

		public ServiceDashboardClient getDashboardClient() {
			return dashboardClient;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setBindable(boolean bindable) {
			this.bindable = bindable;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}

		public void setMetadata(Map<String, Object> metadata) {
			this.metadata = metadata;
		}

		public void setRequires(List<String> requires) {
			this.requires = requires;
		}

		public void setPlans(List<Plan> plans) {
			this.plans = plans;
		}

		public void setDashboardClient(ServiceDashboardClient dashboardClient) {
			this.dashboardClient = dashboardClient;
		}
		
		public Boolean getPlanUpdatable() {
			return planUpdatable;
		}
		
		public void setPlanUpdatable(Boolean planUpdatable) {
			this.planUpdatable = planUpdatable;
		}
	}

	public static class Plan extends JsonObject {
		private String id;

		private String name;

		private String description;
		private boolean free;
		private Map<String, Object> metadata;

		public Plan() {
		}

		public Plan(
				@JsonProperty("id") String id,
				@JsonProperty("name") String name,
				@JsonProperty("description") String description,
				@JsonProperty("free") boolean free,
				@JsonProperty("metadata") Map<String, Object> metadata) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.free = free;
			this.metadata = metadata;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public boolean isFree() {
			return free;
		}

		public Map<String, Object> getMetadata() {
			return metadata;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setFree(boolean free) {
			this.free = free;
		}

		public void setMetadata(Map<String, Object> metadata) {
			this.metadata = metadata;
		}
	}

	public static class ServiceDashboardClient extends JsonObject {
		private String id;

		private String secret;
		@JsonProperty("redirect_uri")
		private String redirectUri;

		public ServiceDashboardClient() {

		}

		public ServiceDashboardClient(
			  @JsonProperty("id") String id,
			  @JsonProperty("secret") String secret,
			  @JsonProperty("redirect_uri") String redirectUri) {
			this.id = id;
			this.secret = secret;
			this.redirectUri = redirectUri;
		}

		public String getId() {
			return id;
		}

		public String getSecret() {
			return secret;
		}

		public String getRedirectUri() {
			return redirectUri;
		}

		public void setRedirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

}
