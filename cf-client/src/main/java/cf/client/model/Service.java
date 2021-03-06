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
package cf.client.model;

import java.io.IOException;
import java.net.URI;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * @author Mike Heath
 */
public class Service extends JsonObject {

	private static final String INFO_URL = "info_url";
	private static final String UNIQUE_ID = "unique_id";
	private static final ObjectMapper mapper = new ObjectMapper();

	private final String label;
	private final String provider;
	private final URI url;
	private final String description;
	private final String version;
	private final URI infoUrl;
	private final boolean active;
	private final String uniqueId;
	private JsonNode extra;


	public Service(
			@JsonProperty("label") String label,
			@JsonProperty("provider") String provider,
			@JsonProperty("url") URI url,
			@JsonProperty("description") String description,
			@JsonProperty("version") String version,
			@JsonProperty(INFO_URL) URI infoUrl,
			@JsonProperty("active") boolean active,
			@JsonProperty(UNIQUE_ID) String uniqueId,
			@JsonProperty("extra") String extra) {
		this.label = label;
		this.provider = provider;
		this.url = url;
		this.description = description;
		this.version = version;
		this.infoUrl = infoUrl;
		this.active = active;
		this.uniqueId = uniqueId;
		if(extra != null) {
			try {
				this.extra = mapper.readValue(extra, JsonNode.class);
			} catch(Exception e) {}
		} else {
			this.extra = null;
		}
	}

	public String getLabel() {
		return label;
	}

	@Deprecated
	public String getProvider() {
		return provider;
	}

	@Deprecated
	public URI getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	@Deprecated
	public String getVersion() {
		return version;
	}

	@Deprecated
	@JsonProperty(INFO_URL)
	public URI getInfoUrl() {
		return infoUrl;
	}

	public boolean isActive() {
		return active;
	}

	@JsonProperty(UNIQUE_ID)
	public String getUniqueId() {
		return uniqueId;
	}
	
	public JsonNode getExtra() {
		return extra;
	}
}
