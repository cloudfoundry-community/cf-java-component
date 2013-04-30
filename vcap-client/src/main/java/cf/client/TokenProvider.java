package cf.client;

/**
 * Provides an interface for getting a token. This is used for long running processes where the token may expire.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface TokenProvider {

	Token get();

}
