package cf.spring;

import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath
 */
public class YamlPropertySource extends PropertySource<YamlDocument> {

	public YamlPropertySource(String name, YamlDocument source) {
		super(name, source);
	}

	@Override
	public Object getProperty(String name) {
		try {
			return findPropertyValue(name);
		} catch (MissingValueException e) {
			return null;
		}
	}

	@Override
	public boolean containsProperty(String name) {
		try {
			findPropertyValue(name);
			return true;
		} catch (MissingValueException e) {
			return false;
		}

	}

	private static MissingValueException MISSING_VALUE_EXCEPTION = new MissingValueException();

	@SuppressWarnings("unchecked")
	public Object findPropertyValue(String name) throws MissingValueException {
		final List<String> names = Arrays.asList(name.split("\\."));
		final Iterator<String> iterator = names.iterator();
		Map<String, Object> values = getSource();
		while (iterator.hasNext()) {
			final String key = iterator.next();
			if (!values.containsKey(key)) {
				throw MISSING_VALUE_EXCEPTION;
			}
			if (iterator.hasNext()) {
				final Object nestedValues = values.get(key);
				if ((nestedValues instanceof Map)) {
					values = (Map<String, Object>) nestedValues;
				} else {
					throw MISSING_VALUE_EXCEPTION;
				}
			} else {
				return values.get(key);
			}
		}
		throw MISSING_VALUE_EXCEPTION;
	}

	private static class MissingValueException extends Exception {}
}
