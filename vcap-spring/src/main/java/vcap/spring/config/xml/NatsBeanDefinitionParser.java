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

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NatsVcapFactoryBean.class);

		// Set NATS reference
		builder.addConstructorArgReference(element.getAttribute(ATTRIBUTE_NATS_REF));

		final List<BeanDefinition> subscriptions = new ManagedList<BeanDefinition>();
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

		return beanDefinition;
	}
}
