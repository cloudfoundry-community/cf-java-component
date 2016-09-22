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
					final BindRequest.BindingType type;
					final String boundResource;
					if (bindBody.getBindResource().getAppGuid() != null) {
						type = BindRequest.BindingType.APPLICATION;
						boundResource = bindBody.getBindResource().getAppGuid();
					} else if (bindBody.getBindResource().getRoute() != null) {
						type = BindRequest.BindingType.ROUTE;
						boundResource = bindBody.getBindResource().getRoute();
					} else {
						type = BindRequest.BindingType.APPLICATION;
						boundResource = bindBody.getApplicationGuid().toString();
					}

					final BindRequest bindRequest = new BindRequest(
							UUID.fromString(instanceId),
							UUID.fromString(bindingId),
							type,
							boundResource,
							bindBody.getPlanId());
					final BindResponse bindResponse = accessor.bind(bindRequest);
					if (bindResponse.isCreated()) {
						response.setStatus(HttpServletResponse.SC_CREATED);
					}
					mapper.writeValue(response.getOutputStream(), bindResponse);
				}
			} else if ("patch".equalsIgnoreCase(request.getMethod())) {
				final UpdateBody updateBody = mapper.readValue(request.getInputStream(), UpdateBody.class);
				final String serviceId = updateBody.getServiceId();
				final BrokerServiceAccessor accessor = getServiceAccessor(serviceId);
				try {
					final UpdateRequest updateRequest
						  = new UpdateRequest(UUID.fromString(instanceId), updateBody.getPlanId(), updateBody.getParameters(),
								  new UpdateRequest.PreviousValues(updateBody.getPreviousValues().getServiceId(), updateBody.getPreviousValues().getPlanId(), updateBody.getPreviousValues().getOrganizationId(), updateBody.getPreviousValues().getSpaceId()));
					accessor.update(updateRequest);
				} catch (MissingResourceException e) {
					response.setStatus(HttpServletResponse.SC_GONE);
				}
				response.getWriter().write("{}");
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

	static class UpdateBody extends JsonObject {

		public static final String SERVICE_ID_FIELD = "service_id";
		public static final String PLAN_ID_FIELD = "plan_id";
		public static final String PARAMETERS = "parameters";
		public static final String PREVIOUS_VALUES = "previous_values";

		private final String serviceId;
		private final String planId;
		private final Map<String, Object> parameters;
		private final PreviousValues previousValues;

		public UpdateBody(
				@JsonProperty(SERVICE_ID_FIELD) String serviceId,
				@JsonProperty(PLAN_ID_FIELD) String planId,
				@JsonProperty(PARAMETERS) Map<String, Object> parameters,
				@JsonProperty(PREVIOUS_VALUES) PreviousValues previousValues) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.parameters = parameters == null ? Collections.emptyMap() : parameters;
			this.previousValues = previousValues;
		}

		@JsonProperty(SERVICE_ID_FIELD)
		public String getServiceId() {
			return serviceId;
		}

		@JsonProperty(PLAN_ID_FIELD)
		public String getPlanId() {
			return planId;
		}

		@JsonProperty(PARAMETERS)
		public Map<String, Object> getParameters() {
			return parameters;
		}

		@JsonProperty(PREVIOUS_VALUES)
		public PreviousValues getPreviousValues() {
			return previousValues;
		}

		static class PreviousValues extends JsonObject {
			public static final String SERVICE_ID_FIELD = "service_id";
			public static final String PLAN_ID_FIELD = "plan_id";
			public static final String ORGANIZATION_ID_FIELD = "organization_id";
			public static final String SPACE_ID_FIELD = "space_id";

			private final String serviceId;
			private final String planId;
			private final UUID organizationId;
			private final UUID spaceId;


			public PreviousValues(
					@JsonProperty(SERVICE_ID_FIELD) String serviceId,
					@JsonProperty(PLAN_ID_FIELD) String planId,
					@JsonProperty(ORGANIZATION_ID_FIELD) UUID organizationId,
					@JsonProperty(SPACE_ID_FIELD) UUID spaceId) {
				this.serviceId = serviceId;
				this.planId = planId;
				this.organizationId = organizationId;
				this.spaceId = spaceId;
			}

			@JsonProperty(SERVICE_ID_FIELD)
			public String getServiceId() {
				return serviceId;
			}

			@JsonProperty(PLAN_ID_FIELD)
			public String getPlanId() {
				return planId;
			}
			
			@JsonProperty(ORGANIZATION_ID_FIELD)
			public UUID getOrganizationId() {
				return organizationId;
			}

			@JsonProperty(SPACE_ID_FIELD)
			public UUID getSpaceId() {
				return spaceId;
			}
		}
	}

	static class BindBody extends JsonObject {

		public static final String SERVICE_ID_FIELD = "service_id";
		public static final String PLAN_ID = "plan_id";
		public static final String APPLICATION_GUID = "app_guid";
		public static final String BIND_RESOURCE = "bind_resource";
		public static final String PARAMETERS = "parameters";

		private final String serviceId;
		private final String planId;
		private final UUID applicationGuid;
		private final BindResource bindResource;
		private final Map<String, Object> parameters;

		public BindBody(
				@JsonProperty(SERVICE_ID_FIELD) String serviceId,
				@JsonProperty(PLAN_ID) String planId,
				@JsonProperty(APPLICATION_GUID) UUID applicationGuid,
				@JsonProperty(BIND_RESOURCE) BindResource bindResource,
				@JsonProperty(PARAMETERS) Map<String, Object> parameters) {
			this.serviceId = serviceId;
			this.planId = planId;
			this.applicationGuid = applicationGuid;
			this.bindResource = bindResource;
			this.parameters = parameters == null ? Collections.emptyMap() : parameters;
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

		public BindResource getBindResource() {
			return bindResource;
		}

		public Map<String, Object> getParameters() {
			return parameters;
		}
	}

	static class BindResource extends JsonObject {
		public static final String APP_GUID = "app_guid";
		public static final String ROUTE = "route";

		private final String appGuid;
		private final String route;

		public BindResource(
				@JsonProperty(APP_GUID) String appGuid,
				@JsonProperty(ROUTE) String route) {
			this.appGuid = appGuid;
			this.route = route;
		}

		public String getAppGuid() {
			return appGuid;
		}

		public String getRoute() {
			return route;
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
