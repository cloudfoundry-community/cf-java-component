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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Holds an Oauth2 token from UAA. This should not be used as a general purpose Oauth2 token. It only implements a few
 * specific use cases for interacting with the 'cf' client and simple interactions with UAA.
 *
 * @author Mike Heath
 */
public class Token {

	public enum Type {
		BEARER("bearer");

		private final String value;

		private Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static Type getType(String value) {
			for (Type type : values()) {
				if (type.value.equalsIgnoreCase(value)) {
					return type;
				}
			}
			return null;
		}
	}

	public static Token parseJson(InputStream json) {
		try {
			final JsonNode node = new ObjectMapper().readTree(json);
			final String accessToken = node.get("access_token").asText();
			final Type type = Type.getType(node.get("token_type").asText());
			final Date expiration = new Date(System.currentTimeMillis() + node.get("expires_in").asLong() * 1000);
			final String rawScopes = node.get("scope").asText();
			final String[] splitScopes = rawScopes.split("\\s+");
			final List<String> scopes = Collections.unmodifiableList(Arrays.asList(splitScopes));
			final String jti = node.get("jti").asText();
			return new Token(accessToken, type, expiration, scopes, jti);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Token parseAuthorization(String authorization) {
		final String[] parts = authorization.split("\\s+");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid authorization token.");
		}
		return new Token(parts[1], Type.getType(parts[0]), null, null, null);
	}

	private final String accessToken;
	private final Type type;
	private final Date expiration;
	private final List<String> scopes;
	private final String jti;

	private Token(String accessToken, Type type, Date expiration, List<String> scopes, String jti) {
		this.accessToken = accessToken;
		this.type = type;
		this.expiration = expiration;
		this.scopes = scopes;
		this.jti = jti;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Type getType() {
		return type;
	}

	public Date getExpiration() {
		return new Date(expiration.getTime());
	}

	public List<String> getScopes() {
		return scopes;
	}

	public String getJti() {
		return jti;
	}

	public String toAuthorizationString() {
		return type.getValue() + " " + accessToken;
	}

	public Header toAuthorizationHeader() {
		return new BasicHeader("Authorization", toAuthorizationString());
	}

	public boolean hasExpired() {
		return expiration != null && expiration.before(new Date(System.currentTimeMillis()));
	}


}
