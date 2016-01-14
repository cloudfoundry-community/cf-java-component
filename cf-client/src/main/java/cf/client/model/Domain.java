package cf.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Domain {

	private final String name;

	public Domain(
			@JsonProperty("name") String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
