package cf.client.model;

import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.net.URI;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Service extends JsonObject {

	private static final String INFO_URL = "info_url";
	private static final String UNIQUE_ID = "unique_id";
	private final String label;
	private final String provider;
	private final URI url;
	private final String description;
	private final String version;
	private final URI infoUrl;
	private final boolean active;
	private final String uniqueId;


	public Service(
			@JsonProperty("label") String label,
			@JsonProperty("provider") String provider,
			@JsonProperty("url") URI url,
			@JsonProperty("description") String description,
			@JsonProperty("version") String version,
			@JsonProperty(INFO_URL) URI infoUrl,
			@JsonProperty("active") boolean active,
			@JsonProperty(UNIQUE_ID) String uniqueId) {
		this.label = label;
		this.provider = provider;
		this.url = url;
		this.description = description;
		this.version = version;
		this.infoUrl = infoUrl;
		this.active = active;
		this.uniqueId = uniqueId;
	}

	public String getLabel() {
		return label;
	}

	public String getProvider() {
		return provider;
	}

	public URI getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

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
}
