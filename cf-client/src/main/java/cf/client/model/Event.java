package cf.client.model;



import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Event {

	private final String type;
	private final String actor;
	private final String actor_type;
	private final String actor_name;
	private final String actee;
	private final String actee_type;
	private final String actee_name;
	private final String timestamp;
	private final JsonNode metadata;
	
	private final UUID space_guid;
	private final UUID organization_guid;
	
	public Event(
			
			@JsonProperty("type") String type,
			@JsonProperty("actor") String actor,
			@JsonProperty("actor_type") String actor_type,
			@JsonProperty("actor_name") String actor_name,
			@JsonProperty("actee") String actee,
			@JsonProperty("actee_type") String actee_type,
			@JsonProperty("actee_name") String actee_name,
			@JsonProperty("timestamp") String timestamp,
			@JsonProperty("space_guid") UUID space_guid,
			@JsonProperty("organization_guid") UUID organization_guid,
			@JsonProperty("metadata") JsonNode metadata
			
						
			) {


		this.type=type;
		this.actor=actor;
		this.actor_type=actor_type;
		this.actor_name=actor_name;
		this.actee=actee;
		this.actee_type=actee_type;
		this.actee_name=actee_name;
		
		this.metadata=metadata;
		
		this.timestamp=timestamp;
		
		this.space_guid=space_guid;
		this.organization_guid=organization_guid;
			
	}

	public String getType() {
		return type;
	}

	public String getActor() {
		return actor;
	}

	public String getActor_type() {
		return actor_type;
	}

	public String getActor_name() {
		return actor_name;
	}

	public String getActee() {
		return actee;
	}

	public String getActee_type() {
		return actee_type;
	}

	public String getActee_name() {
		return actee_name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public UUID getSpace_guid() {
		return space_guid;
	}

	public UUID getOrganization_guid() {
		return organization_guid;
	}

	public JsonNode getMetadata() {
		return metadata;
	}

}
