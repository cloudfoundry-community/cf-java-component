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
