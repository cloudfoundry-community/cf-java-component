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

import cf.common.JsonObject;
import cf.spring.HttpBasicAuthenticator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles REST calls from the Cloud Controller and passes them to the appropriate service broker.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ServiceBrokerHandler implements HttpRequestHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBrokerHandler.class);

	private static final Pattern URI_PATTERN = Pattern.compile("^/v2/service_instances/(.+?)(/service_bindings/(.+))?");

	public static final String SERVICE_ID_PARAM = "service_id";
	public static final String PLAN_ID_PARAM = "plan_id";

	private final ObjectMapper mapper = new ObjectMapper();

	private final ApplicationContext context;
	private final HttpBasicAuthenticator authenticator;

	private final Map<String, ServiceBrokerMethods> brokerMethods = new HashMap<>();

	public ServiceBrokerHandler(ApplicationContext context, HttpBasicAuthenticator authenticator) {
		this.context = context;
		this.authenticator = authenticator;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!authenticator.authenticate(request, response)) {
			return;
		}
		ApiVersionValidator.validateApiVersion(request);
		try {
			response.setContentType(Constants.JSON_CONTENT_TYPE);
			final Matcher matcher = URI_PATTERN.matcher(request.getRequestURI());
			if (!matcher.matches()) {
				throw new NotFoundException("Resource not found");
			}
			final String instanceId = matcher.group(1);
			final String bindingId = matcher.group(3);
			if ("put".equalsIgnoreCase(request.getMethod())) {
				if (bindingId == null) {
					final ProvisionBody provisionBody = mapper.readValue(request.getInputStream(), ProvisionBody.class);
					final String serviceId = provisionBody.getServiceId();
					final ServiceBrokerMethods methods = lookupServiceBroker(serviceId);
					final ProvisionRequest provisionRequest = new ProvisionRequest(
							UUID.fromString(instanceId),
							provisionBody.getPlanId(),
							provisionBody.getOrganizationGuid(),
							provisionBody.getSpaceGuid());
					final Object provisionResponse = invokeMethod(serviceId, methods.getProvision(), provisionRequest);
					response.setStatus(HttpServletResponse.SC_CREATED);
					mapper.writeValue(response.getOutputStream(), provisionResponse);
				} else {
					final BindBody bindBody = mapper.readValue(request.getInputStream(), BindBody.class);
					final String serviceId = bindBody.getServiceId();
					final ServiceBrokerMethods methods = lookupServiceBroker(serviceId);
					final Method bind = methods.getBind();
					if (bind == null) {
						throw new NotFoundException("The service broker with id " + serviceId + " is not bindable.");
					}
					final BindRequest bindRequest = new BindRequest(
							UUID.fromString(instanceId),
							UUID.fromString(bindingId),
							bindBody.applicationGuid,
							bindBody.getPlanId());
					final Object bindResponse = invokeMethod(serviceId, methods.getBind(), bindRequest);
					response.setStatus(HttpServletResponse.SC_CREATED);
					mapper.writeValue(response.getOutputStream(), bindResponse);
				}
			} else if ("delete".equalsIgnoreCase(request.getMethod())) {
				final String serviceId = request.getParameter(SERVICE_ID_PARAM);
				final String planId = request.getParameter(PLAN_ID_PARAM);
				final ServiceBrokerMethods methods = lookupServiceBroker(serviceId);
				try {
					if (bindingId == null) {
						// Deprovision
						if (methods.getDeprovision() != null) {
							final DeprovisionRequest deprovisionRequest = new DeprovisionRequest(UUID.fromString(instanceId), planId);
							invokeMethod(serviceId, methods.getDeprovision(), deprovisionRequest);
						}
					} else {
						// Unbind
						if (methods.getUnbind() != null) {
							final UnbindRequest unbindRequest = new UnbindRequest(UUID.fromString(bindingId), UUID.fromString(instanceId), planId);
							invokeMethod(serviceId, methods.getUnbind(), unbindRequest);
						}
					}
				} catch (MissingResourceException e) {
					response.setStatus(HttpServletResponse.SC_GONE);
				}
				response.getWriter().write("{}");
			} else {
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} catch (ConflictException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{}");
		} catch (ServiceBrokerException e) {
			LOGGER.warn("An error occurred processing a service broker request", e);
			response.setStatus(e.getHttpResponseCode());
			mapper.writeValue(response.getOutputStream(), new ErrorBody(e.getMessage()));
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			mapper.writeValue(response.getOutputStream(), new ErrorBody(e.getMessage()));
		}
	}

	private ServiceBrokerMethods lookupServiceBroker(String serviceId) {
		final ServiceBrokerMethods methods = brokerMethods.get(serviceId);
		if (methods == null) {
			throw new NotFoundException("Could not find service broker with service_id " + serviceId);
		}
		return methods;
	}

	private Object invokeMethod(String serviceId, Method method, Object... args) throws Throwable {
		final ServiceBrokerMethods methods = lookupServiceBroker(serviceId);
		final Object serviceBrokerBean = context.getBean(methods.getBeanName());
		try {
			return method.invoke(serviceBrokerBean, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	void registerBroker(String serviceId, ServiceBrokerMethods methods) {
		brokerMethods.put(serviceId, methods);
	}

	static class ProvisionBody extends JsonObject {

		public static final String SERVICE_ID_FIELD = "service_id";
		public static final String PLAN_ID_FIELD = "plan_id";
		public static final String ORGANIZATION_GUID_FIELD = "organization_guid";
		public static final String SPACE_GUID_FIELD = "space_guid";

		private final String serviceId;
		private final String planId;
		private final UUID organizationGuid;
		private final UUID spaceGuid;

		public ProvisionBody(
				@JsonProperty(SERVICE_ID_FIELD) String serviceId,
				@JsonProperty(PLAN_ID_FIELD) String planId,
				@JsonProperty(ORGANIZATION_GUID_FIELD) UUID organizationGuid,
				@JsonProperty(SPACE_GUID_FIELD) UUID spaceGuid
		) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.organizationGuid = organizationGuid;
			this.spaceGuid = spaceGuid;
		}

		@JsonProperty(SERVICE_ID_FIELD)
		public String getServiceId() {
			return serviceId;
		}

		@JsonProperty(PLAN_ID_FIELD)
		public String getPlanId() {
			return planId;
		}

		@JsonProperty(ORGANIZATION_GUID_FIELD)
		public UUID getOrganizationGuid() {
			return organizationGuid;
		}

		@JsonProperty(SPACE_GUID_FIELD)
		public UUID getSpaceGuid() {
			return spaceGuid;
		}
	}

	static class BindBody extends JsonObject {

		public static final String SERVICE_ID_FIELD = "service_id";
		public static final String PLAN_ID = "plan_id";
		public static final String APPLICATION_GUID = "app_guid";

		private final String serviceId;
		private final String planId;
		private final UUID applicationGuid;

		public BindBody(
				@JsonProperty(SERVICE_ID_FIELD) String serviceId,
				@JsonProperty(PLAN_ID) String planId,
				@JsonProperty(APPLICATION_GUID) UUID applicationGuid) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.applicationGuid = applicationGuid;
		}

		public String getServiceId() {
			return serviceId;
		}

		public String getPlanId() {
			return planId;
		}

		public UUID getApplicationGuid() {
			return applicationGuid;
		}
	}

	static class ErrorBody extends JsonObject {
		private final String description;

		ErrorBody(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

}
