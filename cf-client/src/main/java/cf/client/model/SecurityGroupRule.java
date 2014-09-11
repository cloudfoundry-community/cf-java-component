package cf.client.model;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecurityGroupRule extends JsonObject {
	private final String protocol;
	private final String destination;

	public SecurityGroupRule(
			@JsonProperty("protocol") String protocol,
			@JsonProperty("destination") String destination) {
		this.protocol = protocol;
		this.destination = destination;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getDestination() {
		return destination;
	}
}
