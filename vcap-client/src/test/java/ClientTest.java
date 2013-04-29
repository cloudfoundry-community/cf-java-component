import org.apache.http.impl.client.DefaultHttpClient;
import vcap.client.CfTokens;
import vcap.client.CloudController;
import vcap.client.TokenContents;
import vcap.client.Uaa;
import vcap.client.model.Info;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ClientTest {
	public static void main(String[] args) {
		final CloudController cloudController = new CloudController(new DefaultHttpClient(), "http://api.app2-dev.lds.org");
		final Info info = cloudController.getInfo();
		System.out.println(info.getName());

		final Uaa uaa = cloudController.getUaa();
//		final Uaa uaa = new Uaa(new DefaultHttpClient(), "https://uaa.app2-dev.lds.org");

		final CfTokens tokens = new CfTokens();
		final TokenContents tokenContents = uaa.checkToken("servicegateway", "gatewaysecret", tokens.getTargetToken().getToken());
		System.out.println(tokenContents.getEmail());
	}
}
