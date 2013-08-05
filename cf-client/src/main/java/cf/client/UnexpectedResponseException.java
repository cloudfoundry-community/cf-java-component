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
package cf.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.ByteArrayOutputStream;
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
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		try (InputStream in = entity.getContent()) {
			while (true) {
				int size = in.read(buffer, 0, buffer.length);
				if (size < 0) {
					break;
				}
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			// If we get an error trying to read the body, just swallow it and return null.
			return null;
		}
		return new String(out.toByteArray());
	}

}
