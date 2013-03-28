package vcap.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vcap.component.http.RequestException;
import vcap.component.http.RequestHandler;
import vcap.component.http.SimpleHttpServer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Gateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

	public static final String VCAP_SERVICE_TOKEN_HEADER = "X-VCAP-Service-Token";

	private static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();

	final ObjectMapper mapper = new ObjectMapper();

	private final String authToken;

	public Gateway(SimpleHttpServer server, final Provisioner provisioner, String authToken) {
		if (authToken == null) {
			throw new IllegalArgumentException("authToken can not be null");
		}
		this.authToken = authToken;
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		server.addHandler(Pattern.compile("/+gateway/v1/configurations/(.*?)/handles(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				validateAuthToken(request);
				// Bind service
				if (request.getMethod() == HttpMethod.POST) {
					final BindRequest bindRequest = decode(BindRequest.class, body);
					LOGGER.info("Binding service {} for instance {}", bindRequest.getLabel(), bindRequest.getServiceInstanceId());
					final BindResponse bindResponse = provisioner.bind(bindRequest);
					return encodeResponse(bindResponse);
				}
				// Unbind service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 3) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(1);
					final String handleId = uriMatcher.group(3);
					LOGGER.info("Unbinding instance {} for binding {}", serviceInstanceId, handleId);
					provisioner.unbind(serviceInstanceId, handleId);
					return encodeResponse(EMPTY_JSON_OBJECT);
				}
				throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		});
		server.addHandler(Pattern.compile("/+gateway/v1/configurations(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				validateAuthToken(request);
				// Create service
				if (request.getMethod() == HttpMethod.POST) {
					final CreateRequest createRequest = decode(CreateRequest.class, body);
					LOGGER.info("Creating instance for service {} in space {} in org {}", new Object[]{
							createRequest.getLabel(),
							createRequest.getSpaceGuid(),
							createRequest.getOrganizationGuid()});
					final CreateResponse createResponse = provisioner.create(createRequest);
					return encodeResponse(createResponse);
				}
				// Delete service
				if (request.getMethod() == HttpMethod.DELETE) {
					if (uriMatcher.groupCount() != 2) {
						throw new RequestException(HttpResponseStatus.NOT_FOUND);
					}
					final String serviceInstanceId = uriMatcher.group(2);
					LOGGER.info("Deleting service instance {}", serviceInstanceId);
					provisioner.delete(serviceInstanceId);
					return encodeResponse(EMPTY_JSON_OBJECT);
				}
				throw new RequestException(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		});
	}

	private void validateAuthToken(HttpRequest request) throws RequestException {
		final String authToken = request.headers().get(VCAP_SERVICE_TOKEN_HEADER);
		if (!this.authToken.equals(authToken)) {
			throw new RequestException(HttpResponseStatus.UNAUTHORIZED);
		}
	}

	private HttpResponse encodeResponse(JsonObject jsonBody) throws RequestException {
		try {
			final String json = mapper.writeValueAsString(jsonBody);
			LOGGER.debug("JSON response to server {}", json);
			final byte[] bytes = json.getBytes();
			final ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
			final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
			response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
			response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, bytes.length);
			return response;
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	private <T> T decode(Class<T> type, ByteBuf body) throws RequestException {
		try {
			return mapper.readValue(body.array(), type);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

}
