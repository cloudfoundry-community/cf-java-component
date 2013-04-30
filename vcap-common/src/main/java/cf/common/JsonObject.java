package cf.common;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class JsonObject {

	protected final Map<String, Object> other = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return new HashMap<>(other);
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		other.put(name, value);
	}

}
