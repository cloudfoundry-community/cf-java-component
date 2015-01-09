package cf.client.model;

import java.util.UUID;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Application extends JsonObject {
	public static enum State {
		STARTED,
		STOPPED,
		UNKNOWN
	}
	
	private String name;
	private UUID spaceGuid;
	private Integer memory;
	private Integer instances;
	private Integer diskQuota;
	private String stateString;
	
	public Application() {
	}
	public Application(
			@JsonProperty("name") String name,
			@JsonProperty("space_guid") UUID spaceGuid,
			@JsonProperty("memory") Integer memory,
			@JsonProperty("instances") Integer instances,
			@JsonProperty("disk_quota") Integer diskQuota,
			@JsonProperty("state") String stateString) {
		super();
		this.name = name;
		this.spaceGuid = spaceGuid;
		this.memory = memory;
		this.instances = instances;
		this.diskQuota = diskQuota;
		this.stateString = stateString;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getDiskQuota() {
		return diskQuota;
	}
	
	public void setDiskQuota(Integer diskQuota) {
		this.diskQuota = diskQuota;
	}

	public Integer getInstances() {
		return instances;
	}
	
	public void setInstances(Integer instances) {
		this.instances = instances;
	}
	
	public Integer getMemory() {
		return memory;
	}
	
	public void setMemory(Integer memory) {
		this.memory = memory;
	}
	
	public UUID getSpaceGuid() {
		return spaceGuid;
	}
	
	public void setSpaceGuid(UUID spaceGuid) {
		this.spaceGuid = spaceGuid;
	}
	
	@JsonIgnore
	public State getState() {
		try {
			return Enum.valueOf(State.class, stateString);
		} catch(IllegalArgumentException e) {
			return State.UNKNOWN;
		}
	}
	
	@JsonIgnore
	public void setState(State state) {
		if(State.UNKNOWN == state) {
			throw new IllegalArgumentException("Cannot set state to an UNKNOWN value");
		}
		stateString = state.toString();
	}

	@JsonProperty("state")
	public String getStateString() {
		return stateString;
	}

	@JsonProperty("state")
	public void setStateString(String state) {
		this.stateString = state;
	}
}
