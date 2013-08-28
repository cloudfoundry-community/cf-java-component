package cf.spring;

import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class YamlDocument implements Map<String, Object> {

	@SuppressWarnings("unchecked")
	public static YamlDocument load(Resource resource) throws IOException {
		final Object document = new Yaml().load(resource.getInputStream());
		return new YamlDocument((Map<String, Object>)document);
	}

	private final Map<String, Object> document;

	private YamlDocument(Map<String, Object> document) {
		this.document = document;
	}

	@Override
	public int size() {
		return document.size();
	}

	@Override
	public boolean isEmpty() {
		return document.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return document.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return document.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return document.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return document.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return document.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		document.putAll(m);
	}

	@Override
	public void clear() {
		document.clear();
	}

	@Override
	public Set<String> keySet() {
		return document.keySet();
	}

	@Override
	public Collection<Object> values() {
		return document.values();
	}

	@Override
	public Set<Entry<String,Object>> entrySet() {
		return document.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return document.equals(o);
	}

	@Override
	public int hashCode() {
		return document.hashCode();
	}
}
