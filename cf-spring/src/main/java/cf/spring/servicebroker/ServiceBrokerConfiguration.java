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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Configures all the service brokers.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@Configuration
class ServiceBrokerConfiguration implements ImportAware, ApplicationContextAware, BeanFactoryAware, InitializingBean {

	private ApplicationContext context;
	private BeanExpressionResolver expressionResolver;
	private BeanExpressionContext expressionContext;
	private HttpBasicAuthenticator authenticator;

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
		return (String) expressionResolver.evaluate(expression, expressionContext);
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
		return catalogProvider().getCatalogAccessor().createCatalog();
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
		return new ServiceBrokerHandler(serviceBrokerAuthenticator(), catalogProvider());
	}

    @Bean
	CatalogAccessorProvider catalogProvider(){
        return new CompositeCatalogAccessorProvider(context.getBeansOfType(CatalogAccessorProvider.class).values());
    }

    @Bean
	CatalogAccessorProvider annotationCatalogProvider(){
        return new AnnotationCatalogAccessorProvider(expressionResolver, expressionContext);
    }

	@Bean
	CatalogAccessorProvider dynamicCatalogProvider(){
		return new DynamicCatalogAccessorProvider();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		urlMap.put(Constants.CATALOG_URI, catalogHandler());
		urlMap.put("/v2/service_instances/**", serviceBrokerHandler());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			final ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			expressionResolver = new BeanExpressionResolver() {
				@Override
				public Object evaluate(String expression, BeanExpressionContext beanExpressionContext) throws BeansException {
					final Object value = cbf.getBeanExpressionResolver().evaluate(expression, expressionContext);

					return value == null ? null : value;
				}
			};
			expressionContext = new BeanExpressionContext(cbf, cbf.getRegisteredScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE));
		} else {
			throw new BeanCreationException(getClass().getName() + " can only be used with a " + ConfigurableBeanFactory.class.getName());
		}
	}

}
