package vcap.spring;

import org.springframework.beans.factory.FactoryBean;
import vcap.client.CloudController;
import vcap.client.Token;
import vcap.client.TokenProvider;
import vcap.client.Uaa;

/**
 * Does a client authentication with UAA to get a valid token. The token should be used with the
 * {@link javax.inject.Provider} interface so that a fresh token gets fetched after the token expires.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ClientTokenProviderFactoryBean implements FactoryBean<TokenProvider> {

	private final CloudController cloudController;
	private final String client;
	private final String clientSecret;

	private final TokenProvider tokenProvider = new TokenProvider() {
		@Override
		public Token get() {
			return fetchToken();
		}
	};

	private Token token;

	private Token fetchToken() {
		synchronized (this) {
			if (token == null || token.hasExpired()) {
				final Uaa uaa = cloudController.getUaa();
				token = uaa.getClientToken(client, clientSecret);
			}
			return token;
		}
	}

	public ClientTokenProviderFactoryBean(CloudController cloudController, String client, String clientSecret) {
		this.cloudController = cloudController;
		this.client = client;
		this.clientSecret = clientSecret;
	}

	@Override
	public TokenProvider getObject() throws Exception {
		return tokenProvider;
	}

	@Override
	public Class<?> getObjectType() {
		return TokenProvider.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
