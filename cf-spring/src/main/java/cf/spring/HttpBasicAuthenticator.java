/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class HttpBasicAuthenticator {

	private final String realm;
	private final String user;
	private final String password;

	public HttpBasicAuthenticator(String realm, String user, String password) {
		this.realm = realm;
		this.user = user;
		this.password = password;
	}

	public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
		final String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			setUnathorizedResponse(response);
			return false;
		}
		final String[] split = authorization.trim().split("\\s+");

		if (!split[0].equalsIgnoreCase("Basic") || split.length != 2) {
			setUnathorizedResponse(response);
			return false;
		}

		final String[] credentials = parseCredentials(split[1]);

		if (!user.equals(credentials[0]) || !password.equals(credentials[1])) {
			setUnathorizedResponse(response);
			return false;
		}

		return true;
	}

	private void setUnathorizedResponse(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
	}

	private String[] parseCredentials(String base64Credentials) {
		final String credentials = new String(Base64.decodeBase64(base64Credentials));
		final int splitIndex = credentials.indexOf(':');
		if (splitIndex < 0) {
			return new String[] {credentials, ""};
		}
		final String userid = credentials.substring(0, splitIndex);
		final String password = credentials.substring(splitIndex + 1);
		return new String[] {userid, password};
	}

}
