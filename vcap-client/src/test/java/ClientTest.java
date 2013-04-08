import org.apache.http.impl.client.DefaultHttpClient;
import vcap.client.CloudController;
import vcap.client.Resource;
import vcap.client.RestCollection;
import vcap.client.model.Info;
import vcap.client.Token;
import vcap.client.Uaa;
import vcap.client.model.Service;

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
		final Token token = uaa.getClientToken("service-gateway", "gatewaysecret");

		final RestCollection<Service> services = cloudController.getServices(token);
		for (Resource<Service> service : services) {
			System.out.println("Label: " + service.getEntity().getLabel());
			System.out.println("UUID: " + service.getGuid());
			System.out.println();
		}
	}
}
