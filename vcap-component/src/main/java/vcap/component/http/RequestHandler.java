package vcap.component.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Handles HTTP requests. Register instances of this class with {@link SimpleHttpServer} using
 * {@link SimpleHttpServer#addHandler(String, RequestHandler)} to handle a particular URI.
 *
* @author Mike Heath <elcapo@gmail.com>
*/
public interface RequestHandler {

	/**
	 * Handles an HTTP request.
	 *
	 * @param request the request from the client.
	 * @return the response to be sent to the client.
	 */
	HttpResponse handleRequest(HttpRequest request) throws RequestException;

}
