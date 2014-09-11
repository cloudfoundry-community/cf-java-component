package cf.client.model;

import cf.common.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecurityGroupRule extends JsonObject {
	private final String protocol;
	private final String destination;
	private final String ports;

	public SecurityGroupRule(
			@JsonProperty("protocol") String protocol,
			@JsonProperty("destination") String destination,
			@JsonProperty("ports") String ports) {
		this.protocol = protocol;
		this.destination = destination;
		this.ports = ports;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getPorts() {
		return ports;
	}
}
