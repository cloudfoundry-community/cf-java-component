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
import cf.nats.Publication;
import cf.nats.PublicationHandler;
import cf.nats.message.ComponentAnnounce;
import cf.nats.message.ComponentDiscover;
import nats.client.Subscription;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
@Configuration
public class CfComponentConfiguration implements InitializingBean, DisposableBean, ImportAware {

	@Autowired
	private CfNats nats;
	@Autowired(required = false)
	private CfComponentSettings settings;

	private String type;
	private Subscription componentDiscoverSubscription;

	private final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		final MultiValueMap<String,Object> annotationAttributes = importMetadata.getAllAnnotationAttributes(CfComponent.class.getName());
		type = annotationAttributes.getFirst("value").toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (settings == null) {
			settings = new CfComponentSettings.Builder().build();
		}
		componentDiscoverSubscription = nats.subscribe(ComponentDiscover.class, new PublicationHandler<ComponentDiscover, ComponentAnnounce>() {
			@Override
			public void onMessage(Publication<ComponentDiscover, ComponentAnnounce> publication) {
				publication.reply(buildComponentAnnounceMessage());
			}
		});
		nats.publish(buildComponentAnnounceMessage());
	}

	@Override
	public void destroy() throws Exception {
		componentDiscoverSubscription.close();
	}

	public CfComponentSettings getSettings() {
		return settings;
	}

	@Bean
	HealthzHandlerMapping healthzHandlerMapping() {
		return new HealthzHandlerMapping();
	}

	@Bean
	VarzHandlerMapping varzHandlerMapping(List<VarzProducer> varzProducers) {
			return new VarzHandlerMapping(new VarzAggregator(varzProducers), new HttpBasicAuthenticator("", settings.getUsername(), settings.getPassword()));
	}

	@Bean
	BasicVarzProducer basicVarz() {
		return new BasicVarzProducer(type, settings.getIndex(), settings.getUuid());
	}

	private ComponentAnnounce buildComponentAnnounceMessage() {
		return new ComponentAnnounce(
				type,
				settings.getIndex(),
				settings.getUuid(),
				settings.getHost() + ":" + settings.getPort(),
				Arrays.asList(settings.getUsername(), settings.getPassword()),
				DateTimeUtils.formatDateTime(startTime),
				DateTimeUtils.formatUptime(startTime)
		);
	}

}
