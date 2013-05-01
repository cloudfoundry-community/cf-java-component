package cf.client;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface Uaa {
	Token getClientToken(String client, String clientSecret);

	TokenContents checkToken(String client, String clientSecret, Token token);
}
