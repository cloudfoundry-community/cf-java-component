package vcap.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NatsVcapNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("nats-vcap", new NatsVcapBeanDefinitionParser());
	}
}
