package cf.client.model;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecurityGroup extends JsonObject {

	private final String name;
	private final SecurityGroupRule[] rules;
	private final boolean runningDefault;
	private final boolean stagingDefault;

	public SecurityGroup(
			@JsonProperty("name") String name,
			@JsonProperty("running_default") boolean runningDefault,
			@JsonProperty("staging_default") boolean stagingDefault,
			@JsonProperty("rules") SecurityGroupRule[] rules) {
		this.name = name;
		this.rules = rules;
		this.runningDefault = runningDefault;
		this.stagingDefault = stagingDefault;
		
	}

	public String getName() {
		return name;
	}

	public SecurityGroupRule[] getRules() {
		return rules;
	}
	
	public boolean isRunningDefault() {
		return runningDefault;
	}
	
	public boolean isStagingDefault() {
		return stagingDefault;
	}
}
