/*
 *   Copyright (c) 2012 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.component.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Mike Heath
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
