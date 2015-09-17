/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates that we know how to speak the version of the protocol being used by the Cloud Controller. Logs a warning
 * if an unknown version is being used.
 *
 * @author Mike Heath
 */
public class ApiVersionValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiVersionValidator.class);

	public static final String API_VERSION_HEADER = "X-Broker-Api-Version";

	private static final Set<String> VERSIONS = new HashSet<>();

	static {
		Collections.addAll(VERSIONS, "2.1", "2.2", "2.5");
	}

	public static void validateApiVersion(HttpServletRequest request) {
		final String version = request.getHeader(API_VERSION_HEADER);
		if (version == null) {
			return;
		}
		if (!VERSIONS.contains(version)) {
			LOGGER.warn("Received service broker request from Cloud Controller using unknown API version(" + version + ")");
		}
	}
}
