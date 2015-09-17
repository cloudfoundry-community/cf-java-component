package cf.component.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Heath
 */
public class SimpleHttpServerTest {

	private static final int PORT = 3080;

	@Test
	public void simpleTextServer() throws Exception {
		final String responseBody = "This is a simple HTTP test.";
		final String contentType = "text/plain";
		final HttpResponseStatus responseStatus = HttpResponseStatus.OK;
		try (SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(PORT))) {
			server.addHandler(Pattern.compile("/"), new RequestHandler() {
				@Override
				public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
					final ByteBuf buffer = Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8);
					final DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus, buffer);
					response.headers().add(HttpHeaders.Names.CONTENT_TYPE, contentType);
					response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, buffer.readableBytes());
					return response;
				}
			});

			final URL url = new URL("http://localhost:" + PORT);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			Assert.assertEquals(connection.getResponseCode(), responseStatus.code());
			Assert.assertEquals(connection.getContentType(), contentType);
			final InputStream in = (InputStream) connection.getContent();
			final String content = new BufferedReader(new InputStreamReader(in)).readLine();
			Assert.assertEquals(content, responseBody);
		}
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void notFound() throws Exception {
		try (SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(PORT))) {
			final URL url = new URL("http://localhost:" + PORT);
			url.getContent();
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void handlerThrowsException() throws Exception {
		try (SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(PORT))) {
			server.addHandler(Pattern.compile("/"), new RequestHandler() {
				@Override
				public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
					throw new RuntimeException("This should result in a 500 response.");
				}
			});

			final URL url = new URL("http://localhost:" + PORT);
			url.getContent();
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void handlerThrowsRequestException() throws Exception {
		try (SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(PORT))) {
			server.addHandler(Pattern.compile("/"), new RequestHandler() {
				@Override
				public HttpResponse handleRequest(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
					throw new RequestException(HttpResponseStatus.FORBIDDEN);
				}
			});

			final URL url = new URL("http://localhost:" + PORT);
			url.getContent();
		}
	}

	@Test
	public void jsonResponse() throws Exception {
		final String json = "{}";
		try (SimpleHttpServer server = new SimpleHttpServer(new InetSocketAddress(PORT))) {
			server.addHandler(Pattern.compile("/"), new JsonTextResponseRequestHandler() {
				@Override
				public String handle(HttpRequest request, Matcher uriMatcher, ByteBuf body) throws RequestException {
					return json;
				}
			});
			final URL url = new URL("http://localhost:" + PORT);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			Assert.assertTrue(connection.getContentType().startsWith("application/json"));
			final InputStream in = (InputStream) connection.getContent();
			final String content = new BufferedReader(new InputStreamReader(in)).readLine();
			Assert.assertEquals(content, json);
		}
	}
}
