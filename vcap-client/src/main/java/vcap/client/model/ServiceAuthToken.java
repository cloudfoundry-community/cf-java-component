package vcap.client.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceAuthToken {


	private final String label;
	private final String provider;
	private final String token;

	public ServiceAuthToken(
			@JsonProperty("label") String label,
			@JsonProperty("provider") String provider,
			@JsonProperty("token") String token) {
		this.label = label;
		this.provider = provider;
		this.token = token;
	}

	public String getLabel() {
		return label;
	}

	public String getProvider() {
		return provider;
	}

	public String getToken() {
		return token;
	}
}
