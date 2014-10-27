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

import static cf.spring.servicebroker.AccessorUtils.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * Extension of {@link AbstractAnnotationCatalogAccessorProvider} building
 * the catalog from beans annotated with {@link ServiceBroker}. Services
 * provided by this bean are retrieved from the method annotated with {@link DynamicCatalog}.
 *
 * @author Sebastien Gerard
 * @see DynamicCatalog
 */
public class DynamicCatalogAccessorProvider extends AbstractAnnotationCatalogAccessorProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicCatalogAccessorProvider.class);

    @Override
    public CatalogAccessor getCatalogAccessor() {
        final List<BrokerServiceAccessor> serviceAccessors = new ArrayList<>();

        final String[] serviceBrokers = context.getBeanNamesForAnnotation(ServiceBroker.class);
        for (String serviceBrokerName : serviceBrokers) {
            final Object serviceBroker = context.getBean(serviceBrokerName);
            final Class<?> clazz = getBeanClass(serviceBrokerName);

            final Method method = findMethodWithAnnotation(clazz, DynamicCatalog.class);
            if (method != null) {
                validateReturnType(method, DynamicCatalog.class, Catalog.class);

                try {
                    final Catalog catalog = (Catalog) invokeMethod(serviceBroker, method);

                    for (CatalogService catalogService : catalog.getServices()) {
                        serviceAccessors.add(getMethodAccessor(serviceBrokerName, catalogService));
                    }
                } catch (Throwable e) {
                    LOGGER.warn("The broker (" + serviceBrokerName + ") fails to return its catalog", e);
                }
            }
        }

        return new CatalogAccessor(serviceAccessors);
    }
}
