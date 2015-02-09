/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.nats.message;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

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
		assertNotNull(routerAnnounce);
		assertEquals(ComponentAnnounce.TYPE_ROUTER, routerAnnounce.getType());
		assertEquals(Integer.valueOf(0), routerAnnounce.getIndex());
		assertEquals("0-9adf36d840acd3b1747cdaf4cf4cc949", routerAnnounce.getUuid());
	}

	@Test
	public void routerStart() throws Exception {
		final String routerStartMessage = "{\"id\":\"638024fbd3264b2286a235cf74c46ad1\",\"version\":0.98}";
		final RouterStart routerStart = readRouterStart(routerStartMessage);
		assertEquals("638024fbd3264b2286a235cf74c46ad1", routerStart.getId());
		assertEquals("0.98", routerStart.getVersion());
		assertNull(routerStart.getHosts());
	}

	@Test
	public void routerStartGoRouter() throws Exception {
		final String goRouterStartMessage = "{\"id\":\"5fab1fbf91681d1427bec4ca7ff4698c\",\"hosts\":[\"10.118.216.200\"]}";
		final RouterStart routerStart = readRouterStart(goRouterStartMessage);
		assertEquals("5fab1fbf91681d1427bec4ca7ff4698c", routerStart.getId());
		assertNull(routerStart.getVersion());
		assertNotNull(routerStart.getHosts());
		assertEquals("10.118.216.200", routerStart.getHosts().get(0));
	}

	private RouterStart readRouterStart(String routerStartMessage) throws IOException {
		final ObjectReader reader = mapper.reader(RouterStart.class);
		final RouterStart routerStart = reader.readValue(routerStartMessage);
		assertNotNull(routerStart);
		return routerStart;
	}

	@Test
	public void routerRegisterCloudController() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"host\":\"127.0.0.1\",\"port\":9022,\"uris\":[\"api.mikeheath.cloudfoundry.me\"],\"tags\":{\"component\":\"CloudController\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		assertNotNull(routerRegister);
		assertEquals("127.0.0.1", routerRegister.getHost());
		assertEquals(9022, routerRegister.getPort());
		assertEquals("api.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
	}

	@Test
	public void routerRegisterUaa() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"host\":\"127.0.0.1\",\"port\":8100,\"uris\":[\"uaa.mikeheath.cloudfoundry.me\",\"login.mikeheath.cloudfoundry.me\"],\"tags\":{\"component\":\"uaa\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		assertNotNull(routerRegister);
		assertEquals("127.0.0.1", routerRegister.getHost());
		assertEquals(8100, routerRegister.getPort());
		assertEquals("uaa.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
		assertEquals("login.mikeheath.cloudfoundry.me", routerRegister.getUris().get(1));
	}

	@Test
	public void routerRegisterDeaApp() throws Exception {
		final ObjectReader reader = mapper.reader(RouterRegister.class);
		final String routerRegisterMessage = "{\"dea\":\"0-02ab79376f8a42fe82503ec2a258c345\",\"app\":\"1\",\"host\":\"127.0.0.1\",\"port\":22173,\"uris\":[\"hello.mikeheath.cloudfoundry.me\"],\"tags\":{\"framework\":\"sinatra\",\"runtime\":\"ruby18\"}}";
		final RouterRegister routerRegister = reader.readValue(routerRegisterMessage);
		assertNotNull(routerRegister);
		assertEquals("127.0.0.1", routerRegister.getHost());
		assertEquals(22173, routerRegister.getPort());
		assertEquals("0-02ab79376f8a42fe82503ec2a258c345", routerRegister.getDea());
		assertEquals("hello.mikeheath.cloudfoundry.me", routerRegister.getUris().get(0));
	}
	
	@Test
	public void deaHeartbeat() throws Exception {
		final ObjectReader reader = mapper.reader(DeaHeartbeat.class);
		final String deaHeartbeatMessage = "{\"droplets\":[{\"cc_partition\":\"default\",\"droplet\":\"060e10ec-3c2c-461e-890b-e630e86163ff\",\"version\":\"90dfa9bd-3406-44c0-b1b6-bfdeccac4777\",\"instance\":\"37468705e1794e17be17a31f19c9fe99\",\"index\":0,\"state\":\"RUNNING\",\"state_timestamp\":1401124825.6917121},{\"cc_partition\":\"default\",\"droplet\":\"9887245b-7af0-4b63-b0bb-d0f2baf6c34d\",\"version\":\"18de2fb1-2f47-4910-b391-2c985f0a9bf9\",\"instance\":\"7e823ec1080e4bc284a669398f045dd9\",\"index\":0,\"state\":\"CRASHED\",\"state_timestamp\":1401124844.5487561}],\"dea\":\"0-7b8ba7d0e9f9411188b223dc462bb330\"}";
		final DeaHeartbeat deaHeartbeat = reader.readValue(deaHeartbeatMessage);
		assertNotNull(deaHeartbeat);
		assertEquals("0-7b8ba7d0e9f9411188b223dc462bb330", deaHeartbeat.getDea());
		assertEquals(2, deaHeartbeat.getDroplets().length);
		assertEquals("060e10ec-3c2c-461e-890b-e630e86163ff", deaHeartbeat.getDroplets()[0].getDroplet());
		assertEquals("37468705e1794e17be17a31f19c9fe99", deaHeartbeat.getDroplets()[0].getInstance());
		assertEquals(0, deaHeartbeat.getDroplets()[0].getIndex().intValue());
		assertEquals(DeaHeartbeat.Droplet.RUNNING, deaHeartbeat.getDroplets()[0].getState());
		assertEquals("9887245b-7af0-4b63-b0bb-d0f2baf6c34d", deaHeartbeat.getDroplets()[1].getDroplet());
	}

	@Test
	public void deaStart() throws Exception {
		final ObjectReader reader = mapper.reader(DeaStart.class);
		final String deaStartMessage = "{\"droplet\":\"060e10ec-3c2c-461e-890b-e630e86163ff\",\"name\":\"cf-app\",\"services\":[{\"credentials\":{\"name\":\"Dude\"},\"options\":{},\"label\":\"servicemanager-service-0.1\",\"name\":\"sm\"}],\"vcap_application\":{\"name\":\"cf-app\",\"space_name\":\"test\",\"space_id\":\"b327c22e-879c-4a10-80dc-3b72173bbdfd\"},\"index\":1}";
		final DeaStart deaStart = reader.readValue(deaStartMessage);
		assertNotNull(deaStart);
		assertEquals("060e10ec-3c2c-461e-890b-e630e86163ff", deaStart.getDroplet());
		assertEquals(1, deaStart.getServices().length);
		assertEquals("sm", deaStart.getServices()[0].getName());
		assertNotNull(deaStart.getServices()[0].getCredentials());
		assertEquals("test", deaStart.getVcapApplication().getSpaceName());
		assertEquals(1, deaStart.getIndex().intValue());
	}
}
