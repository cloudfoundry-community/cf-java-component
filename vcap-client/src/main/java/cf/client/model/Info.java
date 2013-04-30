package cf.client.model;

import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.net.URI;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Info extends JsonObject {

	private final String name;
	private final Integer version;
	private final URI authorizationEndpoint;
	private final URI tokenEndpoint;

	public Info(
			@JsonProperty("name") String name,
			@JsonProperty("version") Integer version,
			@JsonProperty("authorization_endpoint") String authorizationEndpoint,
			@JsonProperty("token_endpoint") String tokenEndpoint) {
		this.name = name;
		this.version = version;
		this.authorizationEndpoint = URI.create(authorizationEndpoint);
		this.tokenEndpoint = URI.create(tokenEndpoint);
	}

	public String getName() {
		return name;
	}

	public Integer getVersion() {
		return version;
	}

	public URI getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	public URI getTokenEndpoint() {
		return tokenEndpoint;
	}
}
