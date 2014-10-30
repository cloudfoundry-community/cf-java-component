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

import static org.testng.Assert.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cf.spring.servicebroker.Catalog.CatalogService;
import cf.spring.servicebroker.Catalog.Plan;
import cf.spring.servicebroker.Catalog.ServiceDashboardClient;

/**
 * @author Sebastien Gerard
 */
public class AnnotationCatalogAccessorProviderTest extends AbstractServiceBrokerTest {

    private static final String SERVICE_ID = "some-broker-id-1";
    private static final String SERVICE_NAME = "test-broker";
    private static final String SERVICE_DESCRIPTION = "This is for testing";

    private static final String PLAN_ID = "plan-id-2";
    private static final String PLAN_NAME = "test-plan";
    private static final String PLAN_DESCRIPTION = "Some test plan for testing.";

    private static final String SERVICE_ID_2 = "some-broker-id-2";
    private static final String SERVICE_NAME_2 = "test-broker-2";

    private static final String DASHBOARD_CLIENT_ID = "dashboard-client-id";
    private static final String DASHBOARD_SECRET = "dashboard-secret";
    private static final String DASHBOARD_URI = "http://localhost/dashboard";

    @Configuration
    @EnableAutoConfiguration
    @EnableServiceBroker(username = USERNAME, password = PASSWORD)
    @ServiceBroker({
          @Service(id = SERVICE_ID, name = SERVICE_NAME, description = SERVICE_DESCRIPTION,
                plans = {
                      @ServicePlan(id = PLAN_ID, name = PLAN_NAME, description = PLAN_DESCRIPTION)
                }
          ),
          @Service(id = SERVICE_ID_2, name = SERVICE_NAME_2, description = SERVICE_DESCRIPTION,
                plans = {},
                dashboardClient = @DashboardClient(id = DASHBOARD_CLIENT_ID, secret = DASHBOARD_SECRET,
                      redirectUri = DASHBOARD_URI))
    })
    static class ServiceBrokerConfiguration {

        @Provision
        public ProvisionResponse provision(ProvisionRequest request) {
            return new ProvisionResponse("url");
        }

        @Bind
        public BindResponse bind(BindRequest request) {
            return new BindResponse("cred");
        }

        @Unbind
        public void unbind(UnbindRequest request) {

        }

        @Deprovision
        public void deprovision(DeprovisionRequest request) {
        }
    }

    private ConfigurableApplicationContext context;

    @BeforeClass
    public void init() {
        final SpringApplication application = new SpringApplication(ServiceBrokerConfiguration.class);
        context = application.run();
    }

    @AfterClass
    public void cleanup() throws Exception {
        context.close();
    }

    @Test
    public void catalog() {
        final CatalogAccessor catalogAccessor = getProvider().getCatalogAccessor();
        final BrokerServiceAccessor serviceAccessor = catalogAccessor.getServiceAccessor(SERVICE_ID);
        final CatalogService serviceDescription = serviceAccessor.getServiceDescription();

        assertEquals(SERVICE_ID, serviceDescription.getId());
        assertEquals(SERVICE_NAME, serviceDescription.getName());
        assertEquals(SERVICE_DESCRIPTION, serviceDescription.getDescription());
        assertEquals(1, serviceDescription.getPlans().size());
        assertNull(serviceDescription.getDashboardClient());

        final Plan plan = serviceDescription.getPlans().get(0);
        assertEquals(PLAN_ID, plan.getId());
        assertEquals(PLAN_NAME, plan.getName());
        assertEquals(PLAN_DESCRIPTION, plan.getDescription());

        final ServiceDashboardClient dashboardClient
              = catalogAccessor.getServiceAccessor(SERVICE_ID_2).getServiceDescription().getDashboardClient();
        assertNotNull(dashboardClient);
        assertEquals(DASHBOARD_CLIENT_ID, dashboardClient.getId());
        assertEquals(DASHBOARD_SECRET, dashboardClient.getSecret());
        assertEquals(DASHBOARD_URI, dashboardClient.getRedirectUri());
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void serviceNotExist() {
        getProvider().getCatalogAccessor().getServiceAccessor("1234");
    }

    private AnnotationCatalogAccessorProvider getProvider() {
        return context.getBean(AnnotationCatalogAccessorProvider.class);
    }
}
