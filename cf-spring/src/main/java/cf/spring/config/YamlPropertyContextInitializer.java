/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import cf.spring.YamlDocument;
import cf.spring.YamlPropertySource;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class YamlPropertyContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger(YamlPropertyContextInitializer.class);

	private final String name;
	private final String locationProperty;
	private final String locationDefault;
	private final boolean addFirst;

	public YamlPropertyContextInitializer(String name, String locationProperty, String locationDefault) {
		this(name, locationProperty, locationDefault, true);
	}

	public YamlPropertyContextInitializer(String name, String locationProperty, String locationDefault, boolean addFirst) {
		this.name = name;
		this.locationProperty = locationProperty;
		this.locationDefault = locationDefault;
		this.addFirst = addFirst;
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			final ConfigurableEnvironment environment = applicationContext.getEnvironment();
			final Resource resource = applicationContext.getResource(environment.getProperty(locationProperty, locationDefault));
			final YamlDocument yamlDocument;
			LOGGER.info("Loading config from: {}", resource);
			yamlDocument = YamlDocument.load(resource);
			final MutablePropertySources propertySources = environment.getPropertySources();
			final PropertySource propertySource = new YamlPropertySource(name, yamlDocument);
			if (addFirst) {
				propertySources.addFirst(propertySource);
			} else {
				propertySources.addLast(propertySource);
			}

			applicationContext.getBeanFactory().registerSingleton(name, yamlDocument);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
