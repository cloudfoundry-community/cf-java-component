package vcap.spring;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class HttpClientFactoryBean implements FactoryBean<HttpClient>, DisposableBean {

	private final HttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

	@Override
	public void destroy() throws Exception {
		httpClient.getConnectionManager().shutdown();
	}

	@Override
	public HttpClient getObject() throws Exception {
		return httpClient;
	}

	@Override
	public Class<?> getObjectType() {
		return httpClient.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
