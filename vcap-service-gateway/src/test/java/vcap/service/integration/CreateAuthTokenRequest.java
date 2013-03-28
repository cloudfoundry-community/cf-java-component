package vcap.service.integration;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CreateAuthTokenRequest {


	private final String label;
	private final String provider;
	private final String token;

	public CreateAuthTokenRequest(String label, String provider, String token) {
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
