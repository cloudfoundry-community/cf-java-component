package cf.client.model;

import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
// TODO Can Jackson serialize/deserialize UUID objects?
public class ServicePlan extends JsonObject {

	private static final String SERVICE_GUID = "service_guid";
	private static final String UNIQUE_ID = "unique_id";
	private final String name;
	private final String description;
	private final UUID serviceGuid;
	private final boolean free;
	private final String uniqueId;


	public ServicePlan(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty(SERVICE_GUID) UUID serviceGuid,
			@JsonProperty("free") boolean free,
			@JsonProperty(UNIQUE_ID) String uniqueId) {
		this.name = name;
		this.description = description;
		this.serviceGuid = serviceGuid;
		this.free = free;
		this.uniqueId = uniqueId;
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

	@JsonProperty(SERVICE_GUID)
	public UUID getServiceGuid() {
		return serviceGuid;
	}

	@JsonProperty(UNIQUE_ID)
	public String getUniqueId() {
		return uniqueId;
	}
}