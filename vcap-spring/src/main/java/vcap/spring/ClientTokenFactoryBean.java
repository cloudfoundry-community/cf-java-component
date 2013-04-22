package vcap.spring;

import org.springframework.beans.factory.FactoryBean;
import vcap.client.CloudController;
import vcap.client.Token;
import vcap.client.Uaa;

/**
 * Does a client authentication with UAA to get a valid token. The token should be used with the
 * {@link javax.inject.Provider} interface so that a fresh token gets fetched after the token expires.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ClientTokenFactoryBean implements FactoryBean<Token> {

	private final CloudController cloudController;
	private final String client;
	private final String clientSecret;

	private Token token;

	public ClientTokenFactoryBean(CloudController cloudController, String client, String clientSecret) {
		this.cloudController = cloudController;
		this.client = client;
		this.clientSecret = clientSecret;
	}

	@Override
	public Token getObject() throws Exception {
		synchronized (this) {
			if (token == null || token.hasExpired()) {
				final Uaa uaa = cloudController.getUaa();
				token = uaa.getClientToken(client, clientSecret);
			}
			return token;
		}
	}

	@Override
	public Class<?> getObjectType() {
		synchronized (this) {
			return token == null ? Token.class : token.getClass();
		}
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
