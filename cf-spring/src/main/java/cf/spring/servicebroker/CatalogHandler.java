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

import cf.spring.HttpBasicAuthenticator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.HttpRequestHandler;

import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Publishes the service catalog to "/v2/services". The catalog is built automatically using all the Spring beans
 * annotated with {@code @ServiceBroker}.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
class CatalogHandler implements HttpRequestHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	private final HttpBasicAuthenticator authenticator;
	private final Provider<Catalog> catalog;

	CatalogHandler(HttpBasicAuthenticator authenticator, Provider<Catalog> catalog) {
		this.authenticator = authenticator;
		this.catalog = catalog;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!"get".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		if (!authenticator.authenticate(request, response)) {
			return;
		}
		ApiVersionValidator.validateApiVersion(request);
		response.setContentType(Constants.JSON_CONTENT_TYPE);

		mapper.writeValue(response.getOutputStream(), catalog.get());
	}
}
