package cf.component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import cf.component.http.RequestException;
import cf.component.http.RequestHandler;
import cf.component.http.SimpleHttpServer;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class TestServer {
	public static void main(String[] args) {
		final SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(3080));
		server.addHandler(Pattern.compile("/"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				final ByteBuf buffer = Unpooled.copiedBuffer("Hello world, dude", CharsetUtil.UTF_8);
				final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
				response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
				return response;
			}
		});

		// Bind
		server.addHandler(Pattern.compile("/+gateway/v1/configurations/(.*?)/handles(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				if (request.getMethod() == HttpMethod.POST) {
					System.out.println("Binding service instance " + uriMatcher.group(1));
					final ByteBuf buffer = Unpooled.copiedBuffer("{\"service_id\":\"bind_1\",\"configuration\":{\"yourmom\":\"Went to college.\"}, \"credentials\":{\"host\":\"test\",\"port\":1}}", CharsetUtil.UTF_8);
					final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
					response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
					return response;

				}
				throw new RequestException(HttpResponseStatus.NOT_FOUND);
			}
		});

		// Create and delete
		server.addHandler(Pattern.compile("/+gateway/v1/configurations(/(.*))?"), new RequestHandler() {
			@Override
			public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
				// TODO Validate auth token
				if (request.getMethod() == HttpMethod.POST) {
					System.out.println("Creating service instance");
					final ByteBuf buffer = Unpooled.copiedBuffer("{\"service_id\":\"id_1\",\"configuration\":{\"yourmom\":\"Goes to college.\"}, \"credentials\":{}}", CharsetUtil.UTF_8);
					final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
					response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
					return response;

				}
				if (request.getMethod() == HttpMethod.DELETE) {
					System.out.println("Deleting service " + uriMatcher.group(2));
					final ByteBuf buffer = Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8);
					final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
					response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
					return response;
				}
				throw new RequestException(HttpResponseStatus.NOT_FOUND);
			}
		});

		System.out.println("Listening on port 3080");
	}
}
