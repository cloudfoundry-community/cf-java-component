/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

import static org.testng.Assert.*;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class JsonMarshallingTest {

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void provisionResponse() throws Exception {
		final ProvisionResponse emptyResponse = new ProvisionResponse();
		final String emptyResponseJson = mapper.writeValueAsString(emptyResponse);
		assertEquals(emptyResponseJson, "{}");
		assertNull(mapper.readValue("{}", ProvisionResponse.class).getDashboardUrl());

		final String dashboardUrl = "https://someurl.goes.here/dude";
		final ProvisionResponse response = new ProvisionResponse(dashboardUrl);
		final String responseJson = mapper.writeValueAsString(response);
		assertTrue(responseJson.contains(dashboardUrl));
		final ProvisionResponse unmarshalledResponse = mapper.readValue(responseJson, ProvisionResponse.class);
		assertEquals(unmarshalledResponse.getDashboardUrl(), dashboardUrl);
	}

	@Test
	public void provisionBody() throws Exception {
		final String serviceId = "some service id";
		final String planId = "some plan id";
		final UUID organizationGuid = UUID.randomUUID();
		final UUID spaceGuid = UUID.randomUUID();
		final ServiceBrokerHandler.ProvisionBody request = new ServiceBrokerHandler.ProvisionBody(serviceId, planId, organizationGuid, spaceGuid, Collections.emptyMap());
		final String json = mapper.writeValueAsString(request);
		assertTrue(json.contains(serviceId));
		assertTrue(json.contains(planId));
		assertTrue(json.contains(organizationGuid.toString()));
		assertTrue(json.contains(spaceGuid.toString()));
		final ServiceBrokerHandler.ProvisionBody unmarshalledRequest = mapper.readValue(json, ServiceBrokerHandler.ProvisionBody.class);
		assertEquals(unmarshalledRequest.getServiceId(), serviceId);
		assertEquals(unmarshalledRequest.getPlanId(), planId);
		assertEquals(unmarshalledRequest.getOrganizationGuid(), organizationGuid);
		assertEquals(unmarshalledRequest.getSpaceGuid(), spaceGuid);
	}
}
