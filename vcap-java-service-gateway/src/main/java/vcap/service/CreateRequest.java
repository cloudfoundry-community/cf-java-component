package vcap.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateRequest extends JsonObject {
	private final String label;
	private final String name;
	private final String email;
	private final String plan;
	private final String version;
	private final String provider;
	private final String spaceGuid;
	private final String organizationGuid;

	public CreateRequest(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("email") String email,
			@JsonProperty("plan") String plan,
			@JsonProperty("version") String version,
			@JsonProperty("provider") String provider,
			@JsonProperty("space_guid") String spaceGuid,
			@JsonProperty("organization_guid") String organizationGuid) {
		this.label = label;
		this.name = name;
		this.email = email;
		this.plan = plan;
		this.version = version;
		this.provider = provider;
		this.spaceGuid = spaceGuid;
		this.organizationGuid = organizationGuid;
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

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}
}
