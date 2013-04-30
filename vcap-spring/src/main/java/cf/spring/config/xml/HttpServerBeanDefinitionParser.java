package cf.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import cf.component.http.SimpleHttpServer;
import cf.spring.NettyEventLoopGroupFactoryBean;

import java.net.InetSocketAddress;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class HttpServerBeanDefinitionParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SimpleHttpServer.class);

		final String host = element.getAttribute("host");
		final String port = element.getAttribute("port");
		final BeanDefinitionBuilder localAddressBuilder = BeanDefinitionBuilder.genericBeanDefinition(InetSocketAddress.class);
		localAddressBuilder.addConstructorArgValue(host);
		localAddressBuilder.addConstructorArgValue(port);

		builder.addConstructorArgValue(localAddressBuilder.getBeanDefinition());

		final String parentGroup = element.getAttribute("parent-netty-event-loop-ref");
		if (StringUtils.hasText(parentGroup)) {
			builder.addConstructorArgReference(parentGroup);
		} else {
			final BeanDefinitionBuilder parentGroupBuilder = BeanDefinitionBuilder.genericBeanDefinition(NettyEventLoopGroupFactoryBean.class);
			builder.addConstructorArgValue(parentGroupBuilder.getBeanDefinition());
		}

		final String childGroup = element.getAttribute("child-netty-event-loop-ref");
		if (StringUtils.hasText(childGroup)) {
			builder.addConstructorArgReference(childGroup);
		} else {
			final BeanDefinitionBuilder childGroupBuilder = BeanDefinitionBuilder.genericBeanDefinition(NettyEventLoopGroupFactoryBean.class);
			builder.addConstructorArgValue(childGroupBuilder.getBeanDefinition());
		}

		final String beanId = element.getAttribute("id");
		parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());

		return null;
	}
}
