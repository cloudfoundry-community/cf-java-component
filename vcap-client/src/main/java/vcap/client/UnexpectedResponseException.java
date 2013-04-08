package vcap.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class UnexpectedResponseException extends RuntimeException {
	public UnexpectedResponseException(HttpResponse response) {
		super(buildMessage(response));
	}

	private static String buildMessage(HttpResponse response) {
		final StatusLine statusLine = response.getStatusLine();
		final String reasonPhrase = statusLine.getReasonPhrase();
		final String body = readResponseBody(response);


		final StringBuilder builder = new StringBuilder();
		builder.append("Unexpected response code ").append(statusLine.getStatusCode());
		if (reasonPhrase != null && reasonPhrase.trim().length() > 0) {
			builder.append(' ').append(reasonPhrase);
		}

		if (body != null && body.trim().length() > 0) {
			builder.append(" response body:").append(body);
		}
		return builder.toString();
	}

	private static String readResponseBody(HttpResponse response) {
		final HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		byte[] body = new byte[(int) entity.getContentLength()];
		int i = 0;
		while (i < body.length) {
			try (InputStream in = entity.getContent()) {
				i += in.read(body, i, body.length - i);
			} catch (IOException e) {
				// If we get an error trying to read the body, just swallow it and return null.
				return null;
			}
		}
		return new String(body);
	}

}
