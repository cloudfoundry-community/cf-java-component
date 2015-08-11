/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring;

import cf.component.BasicVarzProducer;
import cf.component.VarzAggregator;
import cf.component.VarzProducer;
import cf.component.util.DateTimeUtils;
import cf.nats.CfNats;
import cf.nats.DefaultCfNats;
import cf.nats.Publication;
import cf.nats.PublicationHandler;
import cf.nats.message.ComponentAnnounce;
import cf.nats.message.ComponentDiscover;
import nats.client.Nats;
import nats.client.Subscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mike Heath
 */
@Configuration
public class CfComponentConfiguration implements InitializingBean, DisposableBean, ImportAware, BeanFactoryAware {

	@Autowired
	private Nats nats;
	@Autowired(required = false)
	private CfNats cfNats;

	private BeanExpressionResolver expressionResolver;
	private BeanExpressionContext expressionContext;

	private String type;
	private int index;
	private String uuid;
	private String host;
	private int port;
	private String username;
	private String password;

	private Subscription componentDiscoverSubscription;

	private final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		final MultiValueMap<String,Object> attributes = importMetadata.getAllAnnotationAttributes(CfComponent.class.getName());
		type = evaluate(attributes, "type");
		index = Integer.valueOf(evaluate(attributes, "index"));
		uuid = evaluate(attributes, "uuid");
		if (StringUtils.isEmpty(uuid)) {
			uuid = UUID.randomUUID().toString();
		}
		host = evaluate(attributes, "host");
		port = Integer.valueOf(evaluate(attributes, "port"));
		username = evaluate(attributes, "username");
		password = evaluate(attributes, "password");
		if (StringUtils.isEmpty(password)) {
			password = new BigInteger(256, ThreadLocalRandom.current()).toString(32);
		}
	}

	private String evaluate(MultiValueMap<String,Object> annotationAttributes, String attribute) {
		final String expression = annotationAttributes.getFirst(attribute).toString();
		final Object value = expressionResolver.evaluate(expression, expressionContext);
		return value == null ? null : value.toString();
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if (cfNats == null) {
			cfNats = new DefaultCfNats(nats);
		}
		componentDiscoverSubscription = cfNats.subscribe(ComponentDiscover.class, new PublicationHandler<ComponentDiscover, ComponentAnnounce>() {
			@Override
			public void onMessage(Publication<ComponentDiscover, ComponentAnnounce> publication) {
				publication.reply(buildComponentAnnounceMessage());
			}
		});
		cfNats.publish(buildComponentAnnounceMessage());
	}

	@Override
	public void destroy() throws Exception {
		componentDiscoverSubscription.close();
	}

	@Bean
	HealthzHandlerMapping healthzHandlerMapping() {
		return new HealthzHandlerMapping();
	}

	@Bean
	VarzHandlerMapping varzHandlerMapping(List<VarzProducer> varzProducers) {
			return new VarzHandlerMapping(new VarzAggregator(varzProducers), new HttpBasicAuthenticator("", username, password));
	}

	@Bean
	BasicVarzProducer basicVarz() {
		return new BasicVarzProducer(type, index, uuid);
	}

	public String getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public String getUuid() {
		return uuid;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	private ComponentAnnounce buildComponentAnnounceMessage() {
		return new ComponentAnnounce(
				type,
				index,
				uuid,
				host + ":" + port,
				Arrays.asList(username, password),
				DateTimeUtils.formatDateTime(startTime),
				DateTimeUtils.formatUptime(startTime)
		);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			final ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			expressionResolver = cbf.getBeanExpressionResolver();
			expressionContext = new BeanExpressionContext(cbf, cbf.getRegisteredScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE));
		} else {
			throw new BeanCreationException(getClass().getName() + " can only be used with a " + ConfigurableBeanFactory.class.getName());
		}
	}
}
