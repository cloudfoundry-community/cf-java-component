package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import vcap.spring.YamlPropertiesPersister;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
class YamlPropertiesBeanDefinitionParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PropertyPlaceholderConfigurer.class);

		builder.addPropertyValue("locations", element.getAttribute("resource"));
		builder.addPropertyValue("propertiesPersister", BeanDefinitionBuilder.genericBeanDefinition(YamlPropertiesPersister.class).getBeanDefinition());

		final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setLazyInit(false);
		final String beanId = parserContext.getReaderContext().generateBeanName(beanDefinition);

		final BeanComponentDefinition definition = new BeanComponentDefinition(beanDefinition, beanId);
		parserContext.registerBeanComponent(definition);

		return null;
	}
}
