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
package cf.spring.servicebroker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;

/**
 * @author Mike Heath
 */
public abstract class AbstractServiceBrokerTest {

	static final String USERNAME = "johndoe";
	static final String PASSWORD = "password";

	protected static final ObjectMapper mapper = new ObjectMapper();

	protected CloseableHttpClient buildAuthenticatingClient() {
		return HttpClients.custom().setDefaultCredentialsProvider(credentials()).build();
	}

	protected BasicCredentialsProvider credentials() {
		final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
		return credentialsProvider;
	}

	protected JsonNode loadCatalog(CloseableHttpClient client) throws IOException {
		final HttpUriRequest catalogRequest = RequestBuilder.get()
				.setUri("http://localhost:8080" + Constants.CATALOG_URI)
				.build();
		final CloseableHttpResponse response = client.execute(catalogRequest);
		assertEquals(response.getStatusLine().getStatusCode(), 200);
		final String body = StreamUtils.copyToString(response.getEntity().getContent(), Charset.defaultCharset());

		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(body);
	}
}
