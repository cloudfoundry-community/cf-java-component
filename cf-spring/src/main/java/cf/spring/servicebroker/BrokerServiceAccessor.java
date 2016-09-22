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

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * Service accessing the service broker methods.
 *
 * @author Sebastien Gerard
 */
public interface BrokerServiceAccessor {

    /**
     * Returns the description of the service accessed by this instance.
     */
    CatalogService getServiceDescription();

    /**
     * Provisions a service according to the specified request.
     */
    ProvisionResponse provision(ProvisionRequest provisionRequest) throws Throwable;

    /**
     * Binds a service according the specify request.
     */
    BindResponse bind(BindRequest request) throws Throwable;

    /**
     * Update a service according the specified request.
     */
    void update(UpdateRequest request) throws Throwable;

    /**
     * Unbinds an existing service from an existing app according to the specified request.
     */
    void unbind(UnbindRequest request) throws Throwable;

    /**
     * Deprovisions an existing service according to the specified request.
     */
    void deprovision(DeprovisionRequest request) throws Throwable;
}
