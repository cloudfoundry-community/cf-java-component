package vcap.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Uaa {

	private static final String OAUTH_TOKEN_URI = "/oauth/token";

	private static final Header ACCEPT_JSON = new BasicHeader("Accept","application/json;charset=utf-8");

	private final HttpClient httpClient;
	private final URI uaa;

	public Uaa(HttpClient httpClient, String uaaUri) {
		this(httpClient, URI.create(uaaUri));
	}

	public Uaa(HttpClient httpClient, URI uaa) {
		this.httpClient = httpClient;
		this.uaa = uaa;
	}

	public Token getClientToken(String client, String clientSecret) {
		try {
			final HttpPost post = new HttpPost(uaa.resolve(OAUTH_TOKEN_URI));

			post.setHeader(ACCEPT_JSON);

			final String encoding = Base64.encodeBase64String((client + ":" + clientSecret).getBytes());
			post.setHeader("Authorization", "Basic " + encoding);

			// TODO Do we need to make the grant type configurable?
			final BasicNameValuePair nameValuePair = new BasicNameValuePair("grant_type", "client_credentials");
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(nameValuePair)));

			final HttpResponse response = httpClient.execute(post);
			final StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 200) {
				// TODO We need a better exception for 401 responses (bad credentials)
				throw new RuntimeException("Error " + statusLine.getStatusCode() +" " + statusLine.getReasonPhrase());
			}
			try {
				final HttpEntity entity = response.getEntity();
				final InputStream content = entity.getContent();
				return Token.parseJson(content);
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
