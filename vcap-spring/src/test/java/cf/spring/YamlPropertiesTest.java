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
package cf.spring;

import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class YamlPropertiesTest {

	@Test
	public void properties() {
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:yamlPropertiesContext.xml")) {
			final String value1 = context.getBean("value1", String.class);
			final String value2 = context.getBean("value2", String.class);
			final String value3 = context.getBean("value3", String.class);
			final String value4 = context.getBean("value4", String.class);

			assertEquals(value1, "This is foo");
			assertEquals(value2, "Nested value");
			assertEquals(value3, "Some value");
			assertEquals(value4, "1");
		}
	}

	@Test
	public void id() {
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:yamlPropertiesContext.xml")) {
			final Object yaml = context.getBean("yaml", Object.class);
			
			assertNotNull(yaml);
			assertTrue(yaml instanceof Map);
			assertTrue(((Map<String, Object>)yaml).get("root") instanceof Map);
		}
	}

}
