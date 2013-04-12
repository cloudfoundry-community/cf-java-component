package vcap.spring.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VcapNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("nats", new NatsBeanDefinitionParser());
	}
}
