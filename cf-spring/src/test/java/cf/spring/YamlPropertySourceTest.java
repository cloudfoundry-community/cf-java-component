package cf.spring;

import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Mike Heath
 */
public class YamlPropertySourceTest {

	@Test
	public void properties() throws Exception {
		final YamlDocument yamlDocument = YamlDocument.load(new ClassPathResource("testProperties.yml"));
		final YamlPropertySource propertySource = new YamlPropertySource("test", yamlDocument);

		assertEquals(propertySource.getProperty("foo"), "This is foo");
		assertTrue(propertySource.containsProperty("foo"));
		assertFalse(propertySource.containsProperty("bar"));

		assertTrue(propertySource.containsProperty("root"));
		assertTrue(propertySource.getProperty("root") instanceof Map);

		assertTrue(propertySource.containsProperty("root.level1"));
		assertTrue(propertySource.getProperty("root.level1") instanceof Map);
		assertFalse(propertySource.containsProperty("root.bar"));
		assertFalse(propertySource.containsProperty("root.level1.bar"));

		assertTrue(propertySource.containsProperty("root.value"));
		assertEquals(propertySource.getProperty("root.value"), "Some value");

		assertFalse(propertySource.containsProperty("root.value.bar"));
		assertEquals(propertySource.getProperty("root.value.bar"), null);

		assertTrue(propertySource.containsProperty("root.level1.level2"));
		assertEquals(propertySource.getProperty("root.level1.level2"), "Nested value");

		assertTrue(propertySource.containsProperty("number"));
		assertEquals(propertySource.getProperty("number"), 1);
	}

}
