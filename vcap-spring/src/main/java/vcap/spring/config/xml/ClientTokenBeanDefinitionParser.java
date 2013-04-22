package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import vcap.spring.ClientTokenFactoryBean;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ClientTokenBeanDefinitionParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ClientTokenFactoryBean.class);
		builder.addConstructorArgReference(element.getAttribute("cloud-controller-client-ref"));
		builder.addConstructorArgValue(element.getAttribute("client-name"));
		builder.addConstructorArgValue(element.getAttribute("client-secret"));

		final String beanId = element.getAttribute("id");
		parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());

		return null;
	}
}
