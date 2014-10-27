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

import java.util.ArrayList;
import java.util.List;

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * Accessor of the {@link Catalog} and all its services.
 *
 * @author Sebastien Gerard
 */
public class CatalogAccessor {

    private final List<BrokerServiceAccessor> serviceAccessors = new ArrayList<>();

    /**
     * Creates the accessor based on the service accessors.
     */
    public CatalogAccessor(List<BrokerServiceAccessor> accessors) {
        this.serviceAccessors.addAll(accessors);
    }

    /**
     * Creates the accessor based on other accessors.
     */
    public CatalogAccessor(CatalogAccessor... catalogAccessors) {
        for (CatalogAccessor catalogAccessor : catalogAccessors) {
            this.serviceAccessors.addAll(catalogAccessor.serviceAccessors);
        }
    }

    /**
     * Creates the catalog.
     */
    public Catalog createCatalog() {
        final List<CatalogService> services = new ArrayList<>();

        for (BrokerServiceAccessor serviceAccessor : serviceAccessors) {
            services.add(serviceAccessor.getServiceDescription());
        }

        return new Catalog(services);
    }

    /**
     * Returns the accessor associated to the specified service id.
     */
    public BrokerServiceAccessor getServiceAccessor(String serviceId) {
        for (BrokerServiceAccessor serviceAccessor : serviceAccessors) {
            if (serviceId.equals(serviceAccessor.getServiceDescription().getId())) {
                return serviceAccessor;
            }
        }

        throw new NotFoundException("Could not find service broker with service_id " + serviceId);
    }
}
