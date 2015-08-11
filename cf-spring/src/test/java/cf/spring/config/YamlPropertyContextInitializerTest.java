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

import cf.spring.YamlDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Mike Heath
 */
public class YamlPropertyContextInitializerTest {

	@Configuration
	@EnableAutoConfiguration
	public static class Config {}

	@Test
	public void  contextWithYamlProperties() {
		final String name = "config";

		final SpringApplication springApplication = new SpringApplication(Config.class);
		springApplication.addInitializers(new YamlPropertyContextInitializer(name, "config", "testProperties.yml"));
		try (ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) springApplication.run()) {
			final YamlDocument config = applicationContext.getBean(name, YamlDocument.class);
			assertNotNull(config);
			assertEquals(config.get("foo"), "This is foo");

			final Environment environment = applicationContext.getEnvironment();
			assertEquals(environment.getProperty("foo"), "This is foo");
		}
	}

	@Test(dependsOnMethods = "contextWithYamlProperties")
	public void  contextWithAlternateYamlProperties() {
		final String property = "config";
		final String name = "alternate";

		System.setProperty(property, "alternateConfig.yml");

		final SpringApplication springApplication = new SpringApplication(Config.class);
		springApplication.addInitializers(new YamlPropertyContextInitializer(name, property, "testProperties.yml"));
		try (ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) springApplication.run()) {
			final YamlDocument config = applicationContext.getBean(name, YamlDocument.class);
			assertNotNull(config);
			assertEquals(config.get("foo"), "bar");
		}
	}
}
