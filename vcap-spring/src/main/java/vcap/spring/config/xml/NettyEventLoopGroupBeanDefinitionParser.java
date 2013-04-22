package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import vcap.spring.NettyEventLoopGroupFactoryBean;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NettyEventLoopGroupBeanDefinitionParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NettyEventLoopGroupFactoryBean.class);

		final String threadCount = element.getAttribute("thread-count");
		if (StringUtils.hasText(threadCount)) {
			builder.addConstructorArgValue(threadCount);
		}
		final String beanId = element.getAttribute("id");
		parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());

		return null;
	}
}
