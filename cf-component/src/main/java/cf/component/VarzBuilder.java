package cf.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VarzBuilder {

	public static VarzBuilder create() {
		return new VarzBuilder();
	}

	private final Map<String, JsonNode> varz = new HashMap<>();

	private VarzBuilder() {}

	public VarzBuilder set(String key, String text) {
		varz.put(key, JsonNodeFactory.instance.textNode(text));
		return this;
	}

	public VarzBuilder set(String key, int number) {
		varz.put(key, JsonNodeFactory.instance.numberNode(number));
		return this;
	}

	public VarzBuilder set(String key, long number) {
		varz.put(key, JsonNodeFactory.instance.numberNode(number));
		return this;
	}

	public VarzBuilder set(String key, double number) {
		varz.put(key, JsonNodeFactory.instance.numberNode(number));
		return this;
	}

	public Map<String, JsonNode> build() {
		return new HashMap<>(varz);
	}
}
