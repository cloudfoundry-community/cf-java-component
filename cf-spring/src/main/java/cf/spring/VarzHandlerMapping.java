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
package cf.spring;

import cf.component.VarzAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VarzHandlerMapping extends AbstractUrlHandlerMapping {

	private final ObjectMapper mapper = new ObjectMapper();

	public VarzHandlerMapping(VarzAggregator aggregator, HttpBasicAuthenticator authenticator) {
		this(aggregator, authenticator, Integer.MIN_VALUE);
	}

	public VarzHandlerMapping(final VarzAggregator aggregator, final HttpBasicAuthenticator authenticator, int order) {
		setOrder(order);
		registerHandler("/varz", new HttpRequestHandler() {
			@Override
			public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
				System.out.println("Handling request");
				if (httpServletRequest.getMethod().equalsIgnoreCase("GET")) {
					if (authenticator.authenticate(httpServletRequest, httpServletResponse)) {
						httpServletResponse.setContentType("application/json;charset=utf-8");
						final ObjectNode varz = aggregator.aggregateVarz();
						mapper.writeValue(httpServletResponse.getOutputStream(), varz);
					}
				} else {
					httpServletResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				}
			}
		});
	}
}
