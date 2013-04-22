package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import vcap.client.CloudController;
import vcap.spring.HttpClientFactoryBean;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CloudControllerClientBeanDefinitionParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CloudController.class);

		final String httpClientRef = element.getAttribute("http-client-ref");
		if (StringUtils.hasText(httpClientRef)) {
			builder.addConstructorArgReference(httpClientRef);
		} else {
			final BeanDefinitionBuilder httpClientBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpClientFactoryBean.class);
			builder.addConstructorArgValue(httpClientBuilder.getBeanDefinition());
		}

		builder.addConstructorArgValue(element.getAttribute("host"));

		final String beanId = element.getAttribute("id");
		parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());

		return null;
	}
}
