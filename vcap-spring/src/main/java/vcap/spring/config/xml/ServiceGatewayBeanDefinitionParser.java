package vcap.spring.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import vcap.service.GatewayServer;
import vcap.service.ServiceGarbageCollector;
import vcap.spring.BootstrappingServiceGuidProvider;
import vcap.spring.DefaultServiceGuidFactoryBean;

import java.util.List;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceGatewayBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		final String provisioner = element.getAttribute("provisioner-ref");
		final String cloudControllerClient = element.getAttribute("cloud-controller-client-ref");
		final String clientToken = element.getAttribute("client-token-ref");
		final String httpServer = element.getAttribute("http-server-ref");
		final String authToken = element.getAttribute("auth-token");

		createGatewayServer(parserContext, provisioner, httpServer, authToken);

		createGarbageCollector(element, parserContext, provisioner, cloudControllerClient, clientToken, authToken);

		return null;
	}

	private void createGatewayServer(ParserContext parserContext, String provisioner, String httpServer, String authToken) {
		final BeanDefinitionBuilder gatewayServerBuilder = BeanDefinitionBuilder.genericBeanDefinition(GatewayServer.class);
		gatewayServerBuilder.addConstructorArgReference(httpServer);
		gatewayServerBuilder.addConstructorArgReference(provisioner);
		gatewayServerBuilder.addConstructorArgValue(authToken);

		final AbstractBeanDefinition gatewayServerBean = gatewayServerBuilder.getBeanDefinition();
		gatewayServerBean.setLazyInit(false);
		final String gatewayServerBeanName = parserContext.getReaderContext().generateBeanName(gatewayServerBean);

		parserContext.getRegistry().registerBeanDefinition(gatewayServerBeanName, gatewayServerBean);
	}

	private void createGarbageCollector(Element element, ParserContext parserContext, String provisioner, String cloudControllerClient, String clientToken, String authToken) {
		final BeanDefinition serviceGuidBean = createServiceGuidBean(element, cloudControllerClient, clientToken, authToken);

		final BeanDefinitionBuilder schedulerBuilder = BeanDefinitionBuilder.genericBeanDefinition(ScheduledExecutorFactoryBean.class);

		final BeanDefinitionBuilder garbageCollectorBuilder = BeanDefinitionBuilder.genericBeanDefinition(ServiceGarbageCollector.class);
		garbageCollectorBuilder.addConstructorArgValue(schedulerBuilder.getBeanDefinition());
		garbageCollectorBuilder.addConstructorArgValue(serviceGuidBean);
		garbageCollectorBuilder.addConstructorArgReference(cloudControllerClient);
		garbageCollectorBuilder.addConstructorArgReference(clientToken);
		garbageCollectorBuilder.addConstructorArgReference(provisioner);

		final AbstractBeanDefinition garbageCollectorBean = garbageCollectorBuilder.getBeanDefinition();
		garbageCollectorBean.setLazyInit(false);
		final String beanName = parserContext.getReaderContext().generateBeanName(garbageCollectorBean);
		parserContext.getRegistry().registerBeanDefinition(beanName, garbageCollectorBean);
	}

	private BeanDefinition createServiceGuidBean(Element element, String cloudControllerClient, String clientToken, String authToken) {
		final Element configuration = DomUtils.getChildElementByTagName(element, "configuration");
		if (configuration != null) {
			final BeanDefinitionBuilder serviceGuidBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultServiceGuidFactoryBean.class);
			serviceGuidBuilder.addConstructorArgValue(configuration.getAttribute("service-guid"));
			return serviceGuidBuilder.getBeanDefinition();
		} else {
			final Element bootstrap = DomUtils.getChildElementByTagName(element, "bootstrap");
			final BeanDefinitionBuilder bootstrapBuilder = BeanDefinitionBuilder.genericBeanDefinition(BootstrappingServiceGuidProvider.class);
			bootstrapBuilder.addConstructorArgReference(cloudControllerClient);
			bootstrapBuilder.addConstructorArgReference(clientToken);
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("label"));
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("provider"));
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("version"));
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("url"));
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("description"));
			bootstrapBuilder.addConstructorArgValue(bootstrap.getAttribute("info-url"));
			bootstrapBuilder.addConstructorArgValue(authToken);

			final ManagedList<BeanDefinition> plans = new ManagedList<>();
			final List<Element> planElements = DomUtils.getChildElementsByTagName(bootstrap, "plan");
			for (Element planElement : planElements) {
				final BeanDefinitionBuilder planBuilder = BeanDefinitionBuilder.genericBeanDefinition(BootstrappingServiceGuidProvider.ServicePlan.class);
				planBuilder.addConstructorArgValue(planElement.getAttribute("name"));
				planBuilder.addConstructorArgValue(planElement.getAttribute("description"));
				plans.add(planBuilder.getBeanDefinition());
			}
			bootstrapBuilder.addConstructorArgValue(plans);
			return bootstrapBuilder.getBeanDefinition();
		}
	}

}
