/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import cf.service.AuthenticationException;
import cf.service.BadRequestException;
import cf.service.Provisioner;
import cf.service.ResourceNotFoundException;
import cf.service.ServiceBroker;
import cf.service.ServiceBrokerException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mike Heath
 * @deprecated Use the V2 services.
 */
public class ServiceBrokerHandler extends AbstractUrlHandlerMapping {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBrokerHandler.class);

	public static class ServiceBrokerHandlerBuilder {
		private final ServiceBroker serviceBroker;
		private int order = 0;

		public ServiceBrokerHandlerBuilder(ServiceBroker serviceBroker) {
			this.serviceBroker = serviceBroker;
		}

		public ServiceBrokerHandlerBuilder(Provisioner provisioner, String authToken) {
			this.serviceBroker = new ServiceBroker(provisioner, authToken);
		}

		public ServiceBrokerHandlerBuilder order(int order) {
			this.order = order;
			return this;
		}

		public ServiceBrokerHandler build() {
			return new ServiceBrokerHandler(this);
		}

	}

	private final ServiceBroker serviceBroker;
	private final ObjectMapper objectMapper = new ObjectMapper(); 

	private ServiceBrokerHandler(ServiceBrokerHandlerBuilder builder) {
		this.serviceBroker = builder.serviceBroker;
		setOrder(builder.order);
		registerHandlers();
	}

	private void registerHandlers() {
		// Create service
		registerHandler("/gateway/v1/configurations", new RequestHandler("POST") {
			@Override
			protected String handleRequest(HttpServletRequest request, HttpServletResponse response, String authToken, byte[] body) throws ServiceBrokerException {
				return serviceBroker.createService(authToken, body);
			}
		});

		registerHandler("/gateway/v1/configurations/**", new RequestHandler("DELETE", "POST") {
			@Override
			protected String handleRequest(HttpServletRequest request, HttpServletResponse response, String authToken, byte[] body) throws ServiceBrokerException {
				final String requestUri = request.getRequestURI();
				final Matcher bindingMatcher = ServiceBroker.BINDING_PATTERN.matcher(requestUri);
				if (bindingMatcher.matches()) {
					switch (request.getMethod()) {
						case "DELETE":
							return serviceBroker.unbindService(authToken, requestUri);
						case "POST":
							return serviceBroker.bindService(authToken, body);
					}
				}
				final Matcher instanceMatcher = ServiceBroker.INSTANCE_PATTERN.matcher(requestUri);
				if (instanceMatcher.matches() && "DELETE".equals(request.getMethod())) {
					return serviceBroker.deleteService(authToken, requestUri);
				}
				throw new ResourceNotFoundException();
			}
		});

	}

	private abstract class RequestHandler implements HttpRequestHandler {

		private final Set<String> allowedHttpMethods = new HashSet<>();

		public RequestHandler(String... allowedHttpMethods) {
			Collections.addAll(this.allowedHttpMethods, allowedHttpMethods);
		}

		@Override
		public final void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			if (!allowedHttpMethods.contains(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}
			final String authToken = request.getHeader(ServiceBroker.VCAP_SERVICE_TOKEN_HEADER);
			final byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
			try {
				String responseBody = handleRequest(request, response, authToken, body);
				response.setContentType("application/json;charset=utf-8");
				response.getWriter().write(responseBody);
			} catch (AuthenticationException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode().put("description", e.getMessage()));
			} catch (BadRequestException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode().put("description", e.getMessage()));
			} catch (ResourceNotFoundException e) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode().put("description", e.getMessage()));
			} catch (ServiceBrokerException e) {
				LOGGER.error("Unexpected Error", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode().put("description", e.getMessage()));
			} catch (Exception e) {
				LOGGER.error("Unexpected Error", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode().put("description", e.getMessage()));
			}
		}

		protected abstract String handleRequest(HttpServletRequest request, HttpServletResponse response, String authToken, byte[] body) throws ServiceBrokerException;

	}

}
