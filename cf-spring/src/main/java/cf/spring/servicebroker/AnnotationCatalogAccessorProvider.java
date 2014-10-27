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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;

import cf.spring.servicebroker.Catalog.CatalogService;

/**
 * Extension of {@link AbstractAnnotationCatalogAccessorProvider} building
 * the catalog from beans annotated with {@link ServiceBroker}. Services
 * are completely defined by this annotation (static catalog).
 *
 * @author Sebastien Gerard
 * @see cf.spring.servicebroker.ServiceBroker
 * @see Service
 * @see ServicePlan
 */
public class AnnotationCatalogAccessorProvider extends AbstractAnnotationCatalogAccessorProvider {

    private final BeanExpressionResolver expressionResolver;
    private final BeanExpressionContext expressionContext;

    public AnnotationCatalogAccessorProvider(BeanExpressionResolver expressionResolver,
                                             BeanExpressionContext expressionContext) {
        this.expressionResolver = expressionResolver;
        this.expressionContext = expressionContext;
    }

    @Override
    public CatalogAccessor getCatalogAccessor() {
        final List<BrokerServiceAccessor> serviceAccessors = new ArrayList<>();

        final String[] serviceBrokers = context.getBeanNamesForAnnotation(ServiceBroker.class);
        for (String serviceBrokerName : serviceBrokers) {
            final Class<?> clazz = getBeanClass(serviceBrokerName);

            final ServiceBroker serviceBroker = clazz.getAnnotation(ServiceBroker.class);

            for (Service service : serviceBroker.value()) {
                final String id = evaluate(service.id());
                final String name = evaluate(service.name());
                final String description = evaluate(service.description());
                final boolean bindable = Boolean.valueOf(evaluate(service.bindable()));

                final List<String> tags = new ArrayList<>();
                for (String tag : service.tags()) {
                    tags.add(evaluate(tag));
                }

                final Map<String, Object> metadata = buildMetadata(service.metadata());

                final List<String> requires = new ArrayList<>();
                for (Permission permission : service.requires()) {
                    requires.add(permission.toString());
                }

                final List<Catalog.Plan> plans = new ArrayList<>();
                for (ServicePlan servicePlan : service.plans()) {
                    final String planId = evaluate(servicePlan.id());
                    final String planName = evaluate(servicePlan.name());
                    final String planDescription = evaluate(servicePlan.description());
                    final boolean free = Boolean.valueOf(evaluate(servicePlan.free()));
                    final Map<String, Object> planMetadata = buildMetadata(servicePlan.metadata());
                    plans.add(new Catalog.Plan(planId, planName, planDescription, free, planMetadata));
                }

                final CatalogService catalogService
                      = new CatalogService(id, name, description, bindable, tags, metadata, requires, plans);

                serviceAccessors.add(getMethodAccessor(serviceBrokerName, catalogService));
            }
        }

        return new CatalogAccessor(serviceAccessors);
    }

    private Map<String, Object> buildMetadata(Metadata[] metadata) {
        final Map<String, Object> metadataObject = new HashMap<>();
        for (Metadata metadatum : metadata) {
            final List<Object> values = new ArrayList<>();
            for (String value : metadatum.value()) {
                values.add(expressionResolver.evaluate(value, expressionContext));
            }
            final String key = evaluate(metadatum.field());
            if (values.size() == 1) {
                metadataObject.put(key, values.get(0));
            } else {
                metadataObject.put(key, values);
            }
        }
        return metadataObject;
    }

    private String evaluate(String expression) {
        return (String) expressionResolver.evaluate(expression, expressionContext);
    }
}
