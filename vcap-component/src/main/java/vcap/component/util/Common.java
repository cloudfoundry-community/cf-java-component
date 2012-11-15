package vcap.component.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class Common {

	public static String generateUniqueId() {
		return randomHexString(16);
	}

	public static String generateCredential() {
		return randomHexString(8);
	}

	private static String randomHexString(int size) {
		final SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		return new BigInteger(bytes).abs().toString(16);
	}

}
