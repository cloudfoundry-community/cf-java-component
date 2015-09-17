package cf.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath
 */
public class VarzBuilder {

	public static VarzBuilder create() {
		return new VarzBuilder();
	}

	private final Map<String, Object> varz = new HashMap<>();

	private VarzBuilder() {}

	public VarzBuilder set(String key, Object value) {
		varz.put(key, value);
		return this;
	}

	public Map<String, ?> build() {
		return new HashMap<>(varz);
	}
}
