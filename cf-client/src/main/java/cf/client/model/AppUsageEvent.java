package cf.client.model;

import java.util.UUID;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppUsageEvent extends JsonObject {

	
	private final String state;
	private final int memory_in_mb_per_instance;
	private final int instance_count;
	private final UUID app_guid;
	private final String app_name;
	private final UUID space_guid;
	private final String space_name;
	private final UUID org_guid;
	private final UUID buildpack_guid;
	private final String buildpack_name;
	
	private final String package_state;
	private final UUID parent_app_guid;
	private final String parent_app_name;
	private final String process_type;
	

	public AppUsageEvent(
			
			@JsonProperty("state") String state,
			@JsonProperty("memory_in_mb_per_instance") int memory_in_mb_per_instance,
			@JsonProperty("instance_count") int instance_count,
			@JsonProperty("app_guid") UUID app_guid,
			@JsonProperty("app_name") String app_name,
			@JsonProperty("space_guid") UUID space_guid,
			
			@JsonProperty("space_name") String space_name,
			@JsonProperty("org_guid") UUID org_guid,
			@JsonProperty("buildpack_guid") UUID buildpack_guid,
			@JsonProperty("buildpack_name") String buildpack_name,

			
			@JsonProperty("package_state") String package_state,
			@JsonProperty("parent_app_guid") UUID parent_app_guid,
			@JsonProperty("parent_app_name") String parent_app_name,
			@JsonProperty("process_type") String process_type
					
			) {


		
		this.state=state;
		this.memory_in_mb_per_instance=memory_in_mb_per_instance;
		this.instance_count=instance_count;
		this.app_guid=app_guid;
		this.app_name=app_name;
		this.space_guid=space_guid;
		this.space_name=space_name;
		this.org_guid=org_guid;
		this.buildpack_guid=buildpack_guid;
		this.buildpack_name=buildpack_name;
		
		this.package_state=package_state;
		this.parent_app_guid=parent_app_guid;
		this.parent_app_name=parent_app_name;
		this.process_type=process_type;		

	
			
	}






	public String getState() {
		return state;
	}



	public int getMemory_in_mb_per_instance() {
		return memory_in_mb_per_instance;
	}

	public int getInstance_count() {
		return instance_count;
	}



	public UUID getApp_guid() {
		return app_guid;
	}



	public String getApp_name() {
		return app_name;
	}



	public UUID getSpace_guid() {
		return space_guid;
	}



	public String getSpace_name() {
		return space_name;
	}



	public UUID getOrg_guid() {
		return org_guid;
	}



	public UUID getBuildpack_guid() {
		return buildpack_guid;
	}



	public String getBuildpack_name() {
		return buildpack_name;
	}






	public String getPackage_state() {
		return package_state;
	}






	public UUID getParent_app_guid() {
		return parent_app_guid;
	}






	public String getParent_app_name() {
		return parent_app_name;
	}






	public String getProcess_type() {
		return process_type;
	}




}
