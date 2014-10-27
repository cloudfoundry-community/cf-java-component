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

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * Abstract {@link CatalogAccessorProvider} that guests that a certain bean offers
 * a certain service which has annotated methods. So, the {@link BrokerServiceAccessor}
 * will be based on annotations.
 *
 * @author Sebastien Gerard
 */
public abstract class AbstractAnnotationCatalogAccessorProvider implements CatalogAccessorProvider,
      ApplicationContextAware {

    protected ApplicationContext context;

    protected AbstractAnnotationCatalogAccessorProvider() {
    }

    /**
     * Returns the accessor used to access the specified service of the
     * specified broker.
     *
     * @param serviceBrokerName the name of the broker offering the specified service
     * @param description the service description
     */
    protected BrokerServiceAccessor getMethodAccessor(String serviceBrokerName, CatalogService description) {
        return new AnnotationBrokerServiceAccessor(description, serviceBrokerName, getBeanClass(serviceBrokerName),
              context.getBean(serviceBrokerName));
    }

    /**
     * Returns the class of the specified bean name.
     */
    protected Class<?> getBeanClass(String beanName) {
        Class<?> clazz = context.getType(beanName);
        while (Proxy.isProxyClass(clazz) || Enhancer.isEnhanced(clazz)) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
