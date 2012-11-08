package vcap.component.http;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Thrown when an error occurs processing a request.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class RequestException extends Exception {

	final HttpResponseStatus status;

	public RequestException(HttpResponseStatus status) {
		this.status = status;
	}

	public RequestException(HttpResponseStatus status, Throwable cause) {
		super(cause);
		this.status = status;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
}
