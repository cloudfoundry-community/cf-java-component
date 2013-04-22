/*
 *   Copyright (c) 2012, 2013 Mike Heath.  All rights reserved.
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
package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import vcap.spring.NatsVcapFactoryBean;
import vcap.spring.RouterRegisterHandlerFactoryBean;
import vcap.spring.VcapSubscriptionConfig;

import java.util.List;

/**
 * @author Mike Heath <heathma@ldschurch.org>
 */
public class NatsBeanDefinitionParser implements BeanDefinitionParser {
	static final String ATTRIBUTE_ID = "id";
	static final String ATTRIBUTE_NATS_REF = "nats-ref";

	static final String ELEMENT_SUBSCRIPTION = "subscription";
	static final String ATTRIBUTE_REF = "ref";
	static final String ATTRIBUTE_METHOD = "method";
	static final String ATTRIBUTE_QUEUE_GROUP = "queue-group";

	static final String ELEMENT_ROUTER_REGISTER = "router-register";

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NatsVcapFactoryBean.class);

		// Set NATS reference
		builder.addConstructorArgReference(element.getAttribute(ATTRIBUTE_NATS_REF));

		final List<BeanDefinition> subscriptions = new ManagedList<>();
		final List<Element> subscriptionElements = DomUtils.getChildElementsByTagName(element, ELEMENT_SUBSCRIPTION);
		for (Element subscriptionElement : subscriptionElements) {
			final BeanDefinitionBuilder subscriptionBuilder = BeanDefinitionBuilder.genericBeanDefinition(VcapSubscriptionConfig.class);
			subscriptionBuilder.addConstructorArgReference(subscriptionElement.getAttribute(ATTRIBUTE_REF));
			subscriptionBuilder.addConstructorArgValue(subscriptionElement.getAttribute(ATTRIBUTE_METHOD));
			subscriptionBuilder.addConstructorArgValue(subscriptionElement.getAttribute(ATTRIBUTE_QUEUE_GROUP));
			subscriptions.add(subscriptionBuilder.getBeanDefinition());
		}
		builder.addConstructorArgValue(subscriptions);

		// Register bean
		final String id = element.getAttribute(ATTRIBUTE_ID);

		final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

		// Router registrations
		final List<Element> routerRegistrationElements = DomUtils.getChildElementsByTagName(element, ELEMENT_ROUTER_REGISTER);
		for (Element routerRegistrationElement : routerRegistrationElements) {
			final String host = routerRegistrationElement.getAttribute("host");
			final String port = routerRegistrationElement.getAttribute("port");
			final List<String> uris = new ManagedList<>();
			for (Element uri : DomUtils.getChildElementsByTagName(routerRegistrationElement, "uri")) {
				uris.add(uri.getTextContent().trim());
			}

			// Register RouterRegistrationHandler
			final BeanDefinitionBuilder routerRegistrationBuilder = BeanDefinitionBuilder.genericBeanDefinition(RouterRegisterHandlerFactoryBean.class);
			routerRegistrationBuilder.addConstructorArgReference(id);
			routerRegistrationBuilder.addConstructorArgValue(host);
			routerRegistrationBuilder.addConstructorArgValue(port);
			routerRegistrationBuilder.addConstructorArgValue(uris);

			final AbstractBeanDefinition routerRegistrationBuilderBeanDefinition = routerRegistrationBuilder.getBeanDefinition();
			routerRegistrationBuilderBeanDefinition.setLazyInit(false);
			final String routerRegistrationName = parserContext.getReaderContext().generateBeanName(routerRegistrationBuilderBeanDefinition);
			parserContext.getRegistry().registerBeanDefinition(routerRegistrationName, routerRegistrationBuilderBeanDefinition);
		}

		return beanDefinition;
	}
}
