/*
 *   Copyright (c) 2013 Mike Heath.  All rights reserved.
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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VcapNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("client-token", new ClientTokenBeanDefinitionParser());
		registerBeanDefinitionParser("pid-file", new PidFileBeanDefinitionParser());
		registerBeanDefinitionParser("nats", new NatsBeanDefinitionParser());
		registerBeanDefinitionParser("service-gateway", new ServiceGatewayBeanDefinitionParser());
		registerBeanDefinitionParser("yaml-properties", new YamlPropertiesBeanDefinitionParser());
	}
}
