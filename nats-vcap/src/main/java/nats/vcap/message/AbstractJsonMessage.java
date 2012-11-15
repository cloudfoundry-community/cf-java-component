/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package nats.vcap.message;

import nats.vcap.VcapJsonMessage;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public abstract class AbstractJsonMessage<R> implements VcapJsonMessage<R> {

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
