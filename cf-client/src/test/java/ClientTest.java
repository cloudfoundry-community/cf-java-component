import cf.client.model.ApplicationInstance;
import org.apache.http.impl.client.DefaultHttpClient;
import cf.client.CfTokens;
import cf.client.CloudController;
import cf.client.DefaultCloudController;
import cf.client.TokenContents;
import cf.client.Uaa;
import cf.client.model.Info;

import java.util.Map;
import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ClientTest {
	public static void main(String[] args) {
		final CloudController cloudController = new DefaultCloudController(new DefaultHttpClient(), "http://api.app2-dev.lds.org");
		final Info info = cloudController.getInfo();
		System.out.println(info.getName());

		final Uaa uaa = cloudController.getUaa();
//		final Uaa uaa = new Uaa(new DefaultHttpClient(), "https://uaa.app2-dev.lds.org");

		final CfTokens tokens = new CfTokens();
		final TokenContents tokenContents = uaa.checkToken("servicegateway", "gatewaysecret", tokens.getCurrentTargetToken().getToken());
		System.out.println(tokenContents.getEmail());
		System.out.println(tokenContents.getExpires());

		final Map<String,ApplicationInstance> instances = cloudController.getApplicationInstances(tokens.getCurrentTargetToken().getToken(), UUID.fromString("be25b808-c7ab-44df-83f6-f46f38f9d649"));
		System.out.println(instances);
	}
}
