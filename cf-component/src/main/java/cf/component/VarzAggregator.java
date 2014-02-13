package cf.component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VarzAggregator {

	public VarzAggregator(Iterable<VarzProducer> producers) {
		this.producers = producers;
	}

	private final Iterable<VarzProducer> producers;

	public ObjectNode aggregateVarz() {
		final ObjectNode varz = JsonNodeFactory.instance.objectNode();
		for (VarzProducer producer : producers) {
			varz.putAll(producer.produceVarz());
		}
		return varz;
	}

}
