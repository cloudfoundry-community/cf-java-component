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

import cf.spring.HttpBasicAuthenticator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures all the service brokers.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@Configuration
class ServiceBrokerConfiguration implements ImportAware, ApplicationContextAware, BeanFactoryAware, InitializingBean {

	private ApplicationContext context;
//	private ConfigurableBeanFactory beanFactory;
	private BeanExpressionResolver expressionResolver;
	private BeanExpressionContext expressionContext;

	private HttpBasicAuthenticator authenticator;
	private CatalogBuilder catalogBuilder;

	private Map<String, HttpRequestHandler> urlMap = new HashMap<>();

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		final MultiValueMap<String,Object> annotationAttributes = importMetadata.getAllAnnotationAttributes(EnableServiceBroker.class.getName());
		final String username = evaluate(annotationAttributes.getFirst("username").toString());
		final String password = evaluate(annotationAttributes.getFirst("password").toString());
		if (username == null) {
			throw new IllegalArgumentException("username cannot be null");
		}
		if (password == null) {
			throw new IllegalArgumentException("password cannot be null");
		}
		authenticator = new HttpBasicAuthenticator("", username, password);
	}

	private String evaluate(String expression) {
		final Object value = expressionResolver.evaluate(expression, expressionContext);
		return value == null ? null : value.toString();
	}

	@Bean HttpBasicAuthenticator serviceBrokerAuthenticator() {
		return authenticator;
	}

	@Bean SimpleUrlHandlerMapping catalogHandlerMapping() {
		final SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(urlMap);
		handlerMapping.setOrder(0);
		return handlerMapping;
	}

	@Bean @Scope(BeanDefinition.SCOPE_PROTOTYPE)
	Catalog serviceBrokerCatalog() {
		return catalogBuilder.build();
	}

	@Bean HttpRequestHandler catalogHandler() {
		return new CatalogHandler(serviceBrokerAuthenticator(), new Provider<Catalog>() {
			@Override
			public Catalog get() {
				return serviceBrokerCatalog();
			}
		});
	}

	@Bean ServiceBrokerHandler serviceBrokerHandler() {
		return new ServiceBrokerHandler(context, serviceBrokerAuthenticator());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		catalogBuilder = new CatalogBuilder();
		final String[] serviceBrokers = context.getBeanNamesForAnnotation(ServiceBroker.class);
		for (String serviceBrokerName : serviceBrokers) {
			Class<?> clazz = context.getType(serviceBrokerName);
			while (Proxy.isProxyClass(clazz) || Enhancer.isEnhanced(clazz)) {
				clazz = clazz.getSuperclass();
			}

			final ServiceBroker serviceBroker = clazz.getAnnotation(ServiceBroker.class);
			catalogBuilder.add(serviceBroker);

			for (Service service : serviceBroker.value()) {
				final String serviceId = evaluate(service.id());
				final boolean bindable = Boolean.valueOf(evaluate(service.bindable()));
				registerServiceBrokerMethods(serviceId, serviceBrokerName, clazz, bindable);
			}
		}

		urlMap.put(Constants.CATALOG_URI, catalogHandler());
		urlMap.put("/v2/service_instances/**", serviceBrokerHandler());
	}

	private void registerServiceBrokerMethods(String serviceId, String serviceBrokerName, Class<?> clazz, boolean bindable) {
		final Method provisionMethod = findMethodWithAnnotation(clazz, Provision.class);
		final Method deprovisionMethod = findMethodWithAnnotation(clazz, Deprovision.class);
		final Method bindMethod = findMethodWithAnnotation(clazz, Bind.class);
		final Method unbindMethod = findMethodWithAnnotation(clazz, Unbind.class);

		serviceBrokerHandler().registerBroker(serviceId, new ServiceBrokerMethods(
				serviceBrokerName,
				bindable,
				provisionMethod,
				deprovisionMethod,
				bindMethod,
				unbindMethod));
	}

	private <T extends Annotation> Method findMethodWithAnnotation(Class<?> clazz, Class<T> annotationType) {
		Method annotatedMethod = null;
		for (Method method : clazz.getDeclaredMethods()) {
			T annotation = AnnotationUtils.findAnnotation(method, annotationType);
			if (annotation != null ) {
				if (annotatedMethod != null) {
					throw new BeanCreationException("Only ONE method with @" + annotationType.getName()
							+ " is allowed on " + clazz.getName() + ".");
				}
				annotatedMethod = method;
			}
		}
		return annotatedMethod;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			final ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			expressionResolver = cbf.getBeanExpressionResolver();
			expressionContext = new BeanExpressionContext(cbf, cbf.getRegisteredScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE));
		} else {
			throw new BeanCreationException(getClass().getName() + " can only be used with a " + ConfigurableBeanFactory.class.getName());
		}
	}

	public class CatalogBuilder {

		private final List<ServiceBroker> serviceBrokers = new ArrayList<>();

		public CatalogBuilder add(ServiceBroker serviceBroker) {
			serviceBrokers.add(serviceBroker);

			return this;
		}

		public Catalog build() {
			List<Catalog.CatalogService> services = new ArrayList<>();
			for (ServiceBroker serviceBroker : serviceBrokers) {
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
						final Map<String, Object> planMetadata = buildMetadata(servicePlan.metadata());
						plans.add(new Catalog.Plan(planId, planName, planDescription, planMetadata));
					}

					services.add(new Catalog.CatalogService(id, name, description, bindable, tags, metadata, requires, plans));
				}
			}
			return new Catalog(services);
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
	}

}
