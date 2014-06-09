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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Maps to the JSON produced by "/v2/catalog" - http://docs.cloudfoundry.org/services/api.html.
 *
 * @author Mike Heath <elcapo@gmail.com>
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
		private final String id;
		private final String name;
		private final String description;
		private final boolean bindable;
		private final List<String> tags;
		private final Map<String, Object> metadata;
		private final List<String> requires;
		private final List<Plan> plans;

		public CatalogService(
				@JsonProperty("id") String id,
				@JsonProperty("name") String name,
				@JsonProperty("description") String description,
				@JsonProperty("bindable") boolean bindable,
				@JsonProperty("tags") List<String> tags,
				@JsonProperty("metadata") Map<String, Object> metadata,
				@JsonProperty("requires") List<String> requires,
				@JsonProperty("plans") List<Plan> plans) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.bindable = bindable;
			this.tags = tags;
			this.metadata = metadata;
			this.requires = requires;
			this.plans = plans;
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
	}

	public static class Plan extends JsonObject {
		private final String id;
		private final String name;
		private final String description;
		private final boolean free;
		private final Map<String, Object> metadata;

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
	}

}
