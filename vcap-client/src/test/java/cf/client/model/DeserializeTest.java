package cf.client.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;

/**
 * This test takes actual JSON from CF and ensure that it can be deserialized into the appropriate model object.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DeserializeTest {

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void info() throws Exception {
		final String infoString = "{\"name\":\"vcap\",\"build\":\"2222\",\"support\":\"http://support.cloudfoundry.com\",\"version\":2,\"description\":\"Cloud Foundry sponsored by Pivotal\",\"authorization_endpoint\":\"https://uaa.app2-dev.lds.org\",\"token_endpoint\":\"https://uaa.app2-dev.lds.org\",\"allow_debug\":true}";
		final Info info = mapper.readValue(infoString, Info.class);
		assertEquals(info.getAuthorizationEndpoint().toString(), "https://uaa.app2-dev.lds.org");
		assertEquals(info.getName(), "vcap");
		assertEquals(info.getTokenEndpoint().toString(), "https://uaa.app2-dev.lds.org");
		assertEquals(info.getVersion(), Integer.valueOf(2));
	}

	@Test
	public void service() throws Exception {
		final String serviceString = "{\n" +
				"  \"label\": \"custom-service\",\n" +
				"  \"provider\": \"ICS\",\n" +
				"  \"url\": \"http://service-gateway.app2-dev.lds.org\",\n" +
				"  \"description\": \"A generic service for connecting to existing services in ICS.\",\n" +
				"  \"version\": \"0.1\",\n" +
				"  \"info_url\": \"http://service-gateway.app2-dev.lds.org/info\",\n" +
				"  \"active\": true,\n" +
				"  \"unique_id\": \"ICS_custom-service\",\n" +
				"  \"extra\": null,\n" +
				"  \"service_plans_url\": \"/v2/services/93d6d0d6-e655-482a-8f2a-68c40bdd42eb/service_plans\"\n" +
				"}";
		final Service service = mapper.readValue(serviceString, Service.class);
		assertEquals(service.getDescription(), "A generic service for connecting to existing services in ICS.");
		assertEquals(service.getInfoUrl().toString(), "http://service-gateway.app2-dev.lds.org/info");
		assertEquals(service.getLabel(), "custom-service");
		assertEquals(service.getProvider(), "ICS");
		assertEquals(service.getUniqueId(), "ICS_custom-service");
		assertEquals(service.getVersion(), "0.1");
		assertEquals(service.getUrl().toString(), "http://service-gateway.app2-dev.lds.org");
		assertTrue(service.isActive());
	}

	@Test
	public void serviceAuthToken() throws Exception {
		final String serviceAuthTokenString = "{\n" +
				"  \"label\": \"custom-service\",\n" +
				"  \"provider\": \"ICS\",\n" +
				"  \"token\": \"secret\"\n" +
				"}";
		final ServiceAuthToken serviceAuthToken = mapper.readValue(serviceAuthTokenString, ServiceAuthToken.class);
		assertEquals(serviceAuthToken.getLabel(), "custom-service");
		assertEquals(serviceAuthToken.getProvider(), "ICS");
		assertEquals(serviceAuthToken.getToken(), "secret");
	}

	@Test
	public void serviceBinding() throws Exception {
		final String serviceBindingString = "{\n" +
				"   \"app_guid\": \"43bfa9c1-ccc8-4929-ab12-a9a6cdcb201b\",\n" +
				"   \"service_instance_guid\": \"3d30cbca-63d4-4377-aa94-f8859be59963\",\n" +
				"   \"credentials\": {\n" +
				"     \"custom\": \"field\"\n" +
				"   },\n" +
				"  \"binding_options\": null,\n" +
				"  \"gateway_data\": {\n" +
				"    \"service_id\": \"8505c7e0-b6d8-4261-8ce2-060cbca23fb9\",\n" +
				"    \"binding_id\": \"9471e8f1-095a-4f3d-b419-9b35311dce3f\"\n" +
				"  },\n" +
				"  \"gateway_name\": \"9471e8f1-095a-4f3d-b419-9b35311dce3f\",\n" +
				"  \"app_url\": \"/v2/apps/43bfa9c1-ccc8-4929-ab12-a9a6cdcb201b\",\n" +
				"  \"service_instance_url\": \"/v2/service_instances/3d30cbca-63d4-4377-aa94-f8859be59963\"\n" +
				"}";
		final ServiceBinding serviceBinding = mapper.readValue(serviceBindingString, ServiceBinding.class);
		assertEquals(serviceBinding.getAppGuid(), UUID.fromString("43bfa9c1-ccc8-4929-ab12-a9a6cdcb201b"));
		assertEquals(serviceBinding.getGatewayName(), "9471e8f1-095a-4f3d-b419-9b35311dce3f");
		assertEquals(serviceBinding.getServiceInstanceGuid(), UUID.fromString("3d30cbca-63d4-4377-aa94-f8859be59963"));
		assertTrue(serviceBinding.getCredentials().has("custom"));
		assertTrue(serviceBinding.getGatewayData().has("service_id"));
	}

	@Test
	public void serviceInstance() throws Exception {
		final String serviceInstanceString = "{\n" +
				"  \"name\": \"test-service\",\n" +
				"  \"credentials\": {\n" +
				"    \"custom\": \"field\"\n" +
				"  },\n" +
				"  \"service_plan_guid\": \"88f7a6fd-1562-467d-adae-d907f1c89299\",\n" +
				"  \"space_guid\": \"841b0f63-ab62-4ef2-a16b-aa330b8ef69b\",\n" +
				"  \"gateway_data\": {\n" +
				"    \"service_id\": \"8505c7e0-b6d8-4261-8ce2-060cbca23fb9\"\n" +
				"  },\n" +
				"  \"service_bindings_url\": \"/v2/service_instances/3d30cbca-63d4-4377-aa94-f8859be59963/service_bindings\",\n" +
				"  \"space_url\": \"/v2/spaces/841b0f63-ab62-4ef2-a16b-aa330b8ef69b\",\n" +
				"  \"service_plan_url\": \"/v2/service_plans/88f7a6fd-1562-467d-adae-d907f1c89299\"\n" +
				"}";
		final ServiceInstance serviceInstance = mapper.readValue(serviceInstanceString, ServiceInstance.class);
		assertEquals(serviceInstance.getName(), "test-service");
		assertEquals(serviceInstance.getServicePlanGuid(), UUID.fromString("88f7a6fd-1562-467d-adae-d907f1c89299"));
		assertEquals(serviceInstance.getSpaceGuid(), UUID.fromString("841b0f63-ab62-4ef2-a16b-aa330b8ef69b"));
		assertNotNull(serviceInstance.getCredentials());
		assertTrue(serviceInstance.getCredentials().has("custom"));
		assertNotNull(serviceInstance.getGatewayData());
		assertTrue(serviceInstance.getGatewayData().has("service_id"));
	}

	@Test
	public void servicePlan() throws Exception {
		final String servicePlanString = "{\n" +
				"  \"name\": \"Default\",\n" +
				"  \"free\": true,\n" +
				"  \"description\": \"The default service plan.\",\n" +
				"  \"service_guid\": \"93d6d0d6-e655-482a-8f2a-68c40bdd42eb\",\n" +
				"  \"extra\": null,\n" +
				"  \"unique_id\": \"ICS_custom-service_Default\",\n" +
				"  \"service_instances_url\": \"/v2/service_plans/88f7a6fd-1562-467d-adae-d907f1c89299/service_instances\",\n" +
				"  \"service_url\": \"/v2/services/93d6d0d6-e655-482a-8f2a-68c40bdd42eb\"\n" +
				"}";
		final ServicePlan servicePlan = mapper.readValue(servicePlanString, ServicePlan.class);
		assertEquals(servicePlan.getDescription(), "The default service plan.");
		assertEquals(servicePlan.getName(), "Default");
		assertEquals(servicePlan.getUniqueId(), "ICS_custom-service_Default");
		assertEquals(servicePlan.getServiceGuid(), UUID.fromString("93d6d0d6-e655-482a-8f2a-68c40bdd42eb"));
		assertTrue(servicePlan.isFree());
	}
}
