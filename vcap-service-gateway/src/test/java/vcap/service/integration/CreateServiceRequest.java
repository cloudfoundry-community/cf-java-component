package vcap.service.integration;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
// TODO Eventually move this to a light weight CC client library
public class CreateServiceRequest {

	private final String label;
	private final String provider;
	private final String url;
	private final String description;
	private final String version;

	public CreateServiceRequest(String label, String provider, String url, String description, String version) {
		this.label = label;
		this.provider = provider;
		this.url = url;
		this.description = description;
		this.version = version;
	}

	public String getLabel() {
		return label;
	}

	public String getProvider() {
		return provider;
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}
}
