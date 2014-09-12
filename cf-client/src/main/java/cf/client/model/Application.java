package cf.client.model;

import java.util.UUID;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application extends JsonObject {
	private final String name;
	private final UUID spaceGuid;
	private final int memory;
	private final int instances;
	private final int diskQuota;
	
	public Application(
			@JsonProperty("name") String name,
			@JsonProperty("space_guid") UUID spaceGuid,
			@JsonProperty("memory") int memory,
			@JsonProperty("instances") int instances,
			@JsonProperty("disk_quota") int diskQuota) {
		super();
		this.name = name;
		this.spaceGuid = spaceGuid;
		this.memory = memory;
		this.instances = instances;
		this.diskQuota = diskQuota;
	}
	
	public String getName() {
		return name;
	}
	
	public int getDiskQuota() {
		return diskQuota;
	}

	public int getInstances() {
		return instances;
	}
	
	public int getMemory() {
		return memory;
	}
	
	public UUID getSpaceGuid() {
		return spaceGuid;
	}
}
