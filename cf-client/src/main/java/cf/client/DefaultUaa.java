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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mike Heath
 */
public class DefaultUaa implements Uaa {

	private static final String CHECK_TOKEN = "/check_token";
	private static final String OAUTH_TOKEN_URI = "/oauth/token";

	private static final Header ACCEPT_JSON = new BasicHeader("Accept","application/json;charset=utf-8");

	private final HttpClient httpClient;
	private final URI uaa;

	private final ObjectMapper mapper;

	public DefaultUaa(HttpClient httpClient, String uaaUri) {
		this(httpClient, URI.create(uaaUri));
	}

	public DefaultUaa(HttpClient httpClient, URI uaa) {
		this.httpClient = httpClient;
		this.uaa = uaa;

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public Token getClientToken(String client, String clientSecret) {
		try {
			final HttpPost post = new HttpPost(uaa.resolve(OAUTH_TOKEN_URI));

			post.setHeader(ACCEPT_JSON);

			post.setHeader(createClientCredentialsHeader(client, clientSecret));

			// TODO Do we need to make the grant type configurable?
			final BasicNameValuePair nameValuePair = new BasicNameValuePair("grant_type", "client_credentials");
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(nameValuePair)));

			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response);
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

	@Override
	public Token getUserToken(String client, String username, String password) {
		try {
			final HttpPost post = new HttpPost(uaa.resolve(OAUTH_TOKEN_URI));

			post.setHeader(ACCEPT_JSON);

			post.setHeader(createClientCredentialsHeader(client, ""));

			// TODO Do we need to make the grant type configurable?
			final BasicNameValuePair grantTypePair = new BasicNameValuePair("grant_type", "password");
			final BasicNameValuePair usernamePair = new BasicNameValuePair("username", username);
			final BasicNameValuePair passwordPair = new BasicNameValuePair("password", password);
			final BasicNameValuePair scopePair = new BasicNameValuePair("scope", "");
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(grantTypePair, usernamePair, passwordPair, scopePair)));

			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response);
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


	@Override
	public TokenContents checkToken(String client, String clientSecret, Token token) {
		try {
			final URI checkTokenUri = uaa.resolve(CHECK_TOKEN);
			final HttpPost post = new HttpPost(checkTokenUri);
			post.setHeader(createClientCredentialsHeader(client,clientSecret));
			final NameValuePair tokenType = new BasicNameValuePair("token_type", token.getType().getValue());
			final NameValuePair tokenValue = new BasicNameValuePair("token", token.getAccessToken());
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(tokenType, tokenValue)));

			final HttpResponse response = httpClient.execute(post);
			try {
				validateResponse(response);

				final HttpEntity entity = response.getEntity();
				final InputStream content = entity.getContent();

				return mapper.readValue(content, TokenContents.class);
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private Header createClientCredentialsHeader(String client, String clientSecret) {
		final String encoding = Base64.encodeBase64String((client + ":" + clientSecret).getBytes());
		return new BasicHeader("Authorization", "Basic " + encoding);
	}

	private Header createClientHeader(String client) {
		final String encoding = Base64.encodeBase64String((client).getBytes());
		return new BasicHeader("Authorization", "Basic " + encoding);
	}

	private void validateResponse(HttpResponse response) {
		final StatusLine statusLine = response.getStatusLine();
		final int statusCode = statusLine.getStatusCode();
		if (statusCode != 200) {
			throw new UnexpectedResponseException(response);
		}
	}

}
