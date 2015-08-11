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
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles REST calls from the Cloud Controller and passes them to the appropriate service broker.
 *
 * @author Mike Heath
 */
public class ServiceBrokerHandler implements HttpRequestHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBrokerHandler.class);

	private static final Pattern URI_PATTERN = Pattern.compile("^/v2/service_instances/(.+?)(/service_bindings/(.+))?");

	public static final String SERVICE_ID_PARAM = "service_id";
	public static final String PLAN_ID_PARAM = "plan_id";

	private final ObjectMapper mapper = new ObjectMapper();

	private final HttpBasicAuthenticator authenticator;
	private final CatalogAccessorProvider catalogAccessorProvider;

	public ServiceBrokerHandler(HttpBasicAuthenticator authenticator, CatalogAccessorProvider catalogAccessorProvider) {
		this.authenticator = authenticator;
		this.catalogAccessorProvider = catalogAccessorProvider;
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
					final BrokerServiceAccessor accessor = getServiceAccessor(serviceId);
					final ProvisionRequest provisionRequest = new ProvisionRequest(
							UUID.fromString(instanceId),
							provisionBody.getPlanId(),
							provisionBody.getOrganizationGuid(),
							provisionBody.getSpaceGuid(),
							provisionBody.getParameters());
					final ProvisionResponse provisionResponse = accessor.provision(provisionRequest);
					if (provisionResponse.isCreated()) {
						response.setStatus(HttpServletResponse.SC_CREATED);
					}
					mapper.writeValue(response.getOutputStream(), provisionResponse);
				} else {
					final BindBody bindBody = mapper.readValue(request.getInputStream(), BindBody.class);
					final String serviceId = bindBody.getServiceId();
					final BrokerServiceAccessor accessor = getServiceAccessor(serviceId);

					final BindRequest bindRequest = new BindRequest(
							UUID.fromString(instanceId),
							UUID.fromString(bindingId),
							bindBody.applicationGuid,
							bindBody.getPlanId());
					final BindResponse bindResponse = accessor.bind(bindRequest);
					if (bindResponse.isCreated()) {
						response.setStatus(HttpServletResponse.SC_CREATED);
					}
					mapper.writeValue(response.getOutputStream(), bindResponse);
				}
			} else if ("delete".equalsIgnoreCase(request.getMethod())) {
				final String serviceId = request.getParameter(SERVICE_ID_PARAM);
				final String planId = request.getParameter(PLAN_ID_PARAM);
				final BrokerServiceAccessor accessor = getServiceAccessor(serviceId);
				try {
					if (bindingId == null) {
						// Deprovision
						final DeprovisionRequest deprovisionRequest
							  = new DeprovisionRequest(UUID.fromString(instanceId), planId);
						accessor.deprovision(deprovisionRequest);
					} else {
						// Unbind
						final UnbindRequest unbindRequest
							  = new UnbindRequest(UUID.fromString(bindingId), UUID.fromString(instanceId), planId);
						accessor.unbind(unbindRequest);
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

	private BrokerServiceAccessor getServiceAccessor(String serviceId) {
		return catalogAccessorProvider.getCatalogAccessor().getServiceAccessor(serviceId);
	}

	static class ProvisionBody extends JsonObject {

		public static final String SERVICE_ID_FIELD = "service_id";
		public static final String PLAN_ID_FIELD = "plan_id";
		public static final String ORGANIZATION_GUID_FIELD = "organization_guid";
		public static final String SPACE_GUID_FIELD = "space_guid";
		public static final String PARAMETERS = "parameters";

		private final String serviceId;
		private final String planId;
		private final UUID organizationGuid;
		private final UUID spaceGuid;
		private final Map<String, Object> parameters;

		public ProvisionBody(
				@JsonProperty(SERVICE_ID_FIELD) String serviceId,
				@JsonProperty(PLAN_ID_FIELD) String planId,
				@JsonProperty(ORGANIZATION_GUID_FIELD) UUID organizationGuid,
				@JsonProperty(SPACE_GUID_FIELD) UUID spaceGuid,
				@JsonProperty(PARAMETERS) Map<String, Object> parameters) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.organizationGuid = organizationGuid;
			this.spaceGuid = spaceGuid;
			this.parameters = parameters == null ? Collections.emptyMap() : parameters;
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

		@JsonProperty(PARAMETERS)
		public Map<String, Object> getParameters() {
			return parameters;
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
