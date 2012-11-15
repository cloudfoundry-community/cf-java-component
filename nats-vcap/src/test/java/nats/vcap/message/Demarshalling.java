package nats.vcap.message;

import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.testng.annotations.Test;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Demarshalling {

	final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void componentAnnounceRouter() throws Exception {
		final ObjectReader reader = mapper.reader(ComponentAnnounce.class);
		final String router = "{\"type\":\"Router\",\"index\":0,\"uuid\":\"0-9adf36d840acd3b1747cdaf4cf4cc949\",\"host\":\"127.0.0.1:8080\",\"credentials\":[\"f0e24f6bb38ec0cb\",\"72153443a92c05be\"],\"start\":\"2012-11-14 05:35:16 +0000\"}";
		final ComponentAnnounce routerAnnounce = reader.readValue(router);
		Assert.assertNotNull(routerAnnounce);
		Assert.assertEquals(ComponentAnnounce.TYPE_ROUTER, routerAnnounce.getType());
		Assert.assertEquals(Integer.valueOf(0), routerAnnounce.getIndex());
		Assert.assertEquals("0-9adf36d840acd3b1747cdaf4cf4cc949", routerAnnounce.getUuid());
	}

	@Test
	public void routerStart() throws Exception {
		final ObjectReader reader = mapper.reader(RouterStart.class);
		final String routerStartMessage = "{\"id\":\"638024fbd3264b2286a235cf74c46ad1\",\"version\":0.98}";
		final RouterStart routerStart = reader.readValue(routerStartMessage);
		Assert.assertNotNull(routerStart);
		Assert.assertEquals("638024fbd3264b2286a235cf74c46ad1", routerStart.getId());
		Assert.assertEquals("0.98", routerStart.getVersion());
	}

	@Test
	public void routerRegisterCloudController() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"host\":\"127.0.0.1\",\"port\":9022,\"uris\":[\"api.mikeheath.cloudfoundry.me\"],\"tags\":{\"component\":\"CloudController\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		Assert.assertNotNull(routerRegister);
		Assert.assertEquals("127.0.0.1", routerRegister.getHost());
		Assert.assertEquals(Integer.valueOf(9022), routerRegister.getPort());
		Assert.assertEquals("api.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
	}

	@Test
	public void routerRegisterUaa() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"host\":\"127.0.0.1\",\"port\":8100,\"uris\":[\"uaa.mikeheath.cloudfoundry.me\",\"login.mikeheath.cloudfoundry.me\"],\"tags\":{\"component\":\"uaa\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		Assert.assertNotNull(routerRegister);
		Assert.assertEquals("127.0.0.1", routerRegister.getHost());
		Assert.assertEquals(Integer.valueOf(8100), routerRegister.getPort());
		Assert.assertEquals("uaa.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
		Assert.assertEquals("login.mikeheath.cloudfoundry.me", routerRegister.getUris().get(1));
	}

	@Test
	public void routerRegisterDeaApp() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"dea\":\"0-02ab79376f8a42fe82503ec2a258c345\",\"app\":\"1\",\"host\":\"127.0.0.1\",\"port\":22173,\"uris\":[\"hello.mikeheath.cloudfoundry.me\"],\"tags\":{\"framework\":\"sinatra\",\"runtime\":\"ruby18\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		Assert.assertNotNull(routerRegister);
		Assert.assertEquals("127.0.0.1", routerRegister.getHost());
		Assert.assertEquals(Integer.valueOf(22173), routerRegister.getPort());
		Assert.assertEquals("0-02ab79376f8a42fe82503ec2a258c345", routerRegister.getDea());
		Assert.assertEquals("hello.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
	}
}
