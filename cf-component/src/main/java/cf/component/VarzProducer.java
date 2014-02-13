package cf.component;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface VarzProducer {

	Map<String, JsonNode> produceVarz();

}
