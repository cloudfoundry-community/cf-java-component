package cf.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import cf.spring.YamlFactoryBean;
import cf.spring.YamlPropertiesPersister;

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

		if(StringUtils.hasText(element.getAttribute("id"))) {
			BeanDefinitionBuilder yamlBuilder = BeanDefinitionBuilder.genericBeanDefinition(YamlFactoryBean.class);
			yamlBuilder.addPropertyValue("yamlFile", element.getAttribute("resource"));
			
			final BeanComponentDefinition yamlDefinition = new BeanComponentDefinition(yamlBuilder.getBeanDefinition(), element.getAttribute("id"));
			parserContext.registerBeanComponent(yamlDefinition);
		}
		
		return null;
	}
}
