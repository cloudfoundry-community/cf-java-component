package cf.spring;

import org.springframework.util.PropertiesPersister;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * @author Mike Heath
 * @deprecated Use @{link YamlPropertySource} instead.
 */

public class YamlPropertiesPersister implements PropertiesPersister {
	@Override
	public void load(Properties props, InputStream is) throws IOException {
		try (Reader reader = new InputStreamReader(is)) {
			load(props, reader);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void load(Properties props, Reader reader) throws IOException {
		Yaml yaml = new Yaml();
		Map<String, Object> map = (Map<String, Object>) yaml.load(reader);
		mapProperties(props, map, "");
	}

	@SuppressWarnings("unchecked")
	protected void mapProperties(Properties props, Map<String, Object> map, String prefix) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			final String key = prefix + entry.getKey();
			final Object val = entry.getValue();
			if (val instanceof Map) {
				mapProperties(props, (Map<String, Object>) val, key + ".");
			} else {
				props.put(key, val.toString());
			}
		}
	}

	@Override
	public void store(Properties props, OutputStream os, String header) throws IOException {
		throw new UnsupportedOperationException("Storing properties is not supported.");
	}

	@Override
	public void store(Properties props, Writer writer, String header) throws IOException {
		throw new UnsupportedOperationException("Storing properties is not supported.");
	}

	@Override
	public void loadFromXml(Properties props, InputStream is) throws IOException {
		throw new UnsupportedOperationException("Was XML properties ever even a good idea?");
	}

	@Override
	public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
		throw new UnsupportedOperationException("Was XML properties ever even a good idea?");
	}

	@Override
	public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
		throw new UnsupportedOperationException("Was XML properties ever even a good idea?");
	}
}
