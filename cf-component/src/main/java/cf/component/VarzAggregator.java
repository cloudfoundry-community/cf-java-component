package cf.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VarzAggregator {

	public VarzAggregator(Iterable<VarzProducer> producers) {
		this.producers = producers;
	}

	private final ObjectMapper mapper = new ObjectMapper()
			;
	private final Iterable<VarzProducer> producers;

	public ObjectNode aggregateVarz() {
		final Map<String, Object> varz = new HashMap<>();
		for (VarzProducer producer : producers) {
			varz.putAll(producer.produceVarz());
		}
		final JsonNode jsonNode = mapper.valueToTree(varz);
		return (ObjectNode) jsonNode;
	}

}
