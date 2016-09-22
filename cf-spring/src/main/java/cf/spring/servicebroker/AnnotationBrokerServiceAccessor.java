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

import static cf.spring.servicebroker.AccessorUtils.findMethodWithAnnotation;

import java.lang.reflect.Method;

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * {@link BrokerServiceAccessor} accessing to service broker methods based
 * on their annotations.
 *
 * @author Sebastien Gerard
 * @see Provision
 * @see Bind
 * @see Unbind
 * @see Deprovision
 */
public class AnnotationBrokerServiceAccessor implements BrokerServiceAccessor {

    private final CatalogService description;
    private final Object bean;
    private final ServiceBrokerMethods methods;

    public AnnotationBrokerServiceAccessor(CatalogService description, String beanName, Class<?> clazz, Object bean) {
        this.description = description;
        this.bean = bean;

        final Method provisionMethod = findMethodWithAnnotation(clazz, Provision.class);
        final Method updateMethod = findMethodWithAnnotation(clazz, Update.class);
        final Method deprovisionMethod = findMethodWithAnnotation(clazz, Deprovision.class);
        final Method bindMethod = findMethodWithAnnotation(clazz, Bind.class);
        final Method unbindMethod = findMethodWithAnnotation(clazz, Unbind.class);

        methods = new ServiceBrokerMethods(beanName, description.isBindable(), provisionMethod, updateMethod, deprovisionMethod, bindMethod,
              unbindMethod);
    }

    @Override
    public CatalogService getServiceDescription() {
        return description;
    }

    @Override
    public ProvisionResponse provision(ProvisionRequest provisionRequest) throws Throwable {
        return (ProvisionResponse) invokeMethod(methods.getProvision(), provisionRequest);
    }

    @Override
    public void update(UpdateRequest updateRequest) throws Throwable {
        if (methods.getUpdate() == null) {
            throw new NotFoundException("The service broker with id " + getServiceDescription().getId()
                  + " is not updatable.");
        }

        invokeMethod(methods.getUpdate(), updateRequest);
    }

    @Override
    public BindResponse bind(BindRequest request) throws Throwable {
        if (methods.getBind() == null) {
            throw new NotFoundException("The service broker with id " + getServiceDescription().getId()
                  + " is not bindable.");
        }

        return (BindResponse) invokeMethod(methods.getBind(), request);
    }

    @Override
    public void deprovision(DeprovisionRequest request) throws Throwable {
        invokeMethod(methods.getDeprovision(), request);
    }

    @Override
    public void unbind(UnbindRequest request) throws Throwable {
        invokeMethod(methods.getUnbind(), request);
    }

    private Object invokeMethod(Method method, Object... args) throws Throwable {
        return AccessorUtils.invokeMethod(bean, method, args);
    }
}
