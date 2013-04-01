package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;
import vcap.common.JsonObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateRequest extends JsonObject {
	private static final String SPACE_GUID = "space_guid";
	private static final String ORGANIZATION_GUID = "organization_guid";
	private static final String UNIQUE_ID = "unique_id";
	private final String label;
	private final String name;
	private final String email;
	private final String plan;
	private final String version;
	private final String provider;
	private final String spaceGuid;
	private final String organizationGuid;
	private final String uniqueId;

	public CreateRequest(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("email") String email,
			@JsonProperty("plan") String plan,
			@JsonProperty("version") String version,
			@JsonProperty("provider") String provider,
			@JsonProperty(SPACE_GUID) String spaceGuid,
			@JsonProperty(ORGANIZATION_GUID) String organizationGuid,
			@JsonProperty(UNIQUE_ID) String uniqueId) {
		this.label = label;
		this.name = name;
		this.email = email;
		this.plan = plan;
		this.version = version;
		this.provider = provider;
		this.spaceGuid = spaceGuid;
		this.organizationGuid = organizationGuid;
		this.uniqueId = uniqueId;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPlan() {
		return plan;
	}

	public String getVersion() {
		return version;
	}

	public String getProvider() {
		return provider;
	}

	@JsonProperty(SPACE_GUID)
	public String getSpaceGuid() {
		return spaceGuid;
	}

	@JsonProperty(ORGANIZATION_GUID)
	public String getOrganizationGuid() {
		return organizationGuid;
	}

	@JsonProperty(UNIQUE_ID)
	public String getUniqueId() {
		return uniqueId;
	}
}
