/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Mike Heath
 */
public class ProcessUtilsTest {

	@Test
	public void run() throws Exception {
		// This test will probably fail on Windows... maybe Mac too. Who knows?
		final String expectedOutput = "Have a nice day.";
		final String output = ProcessUtils.run("/bin/echo", expectedOutput);
		Assert.assertEquals(output, expectedOutput);
	}

	@Test
	public void processStats() throws Exception {
		System.out.println(ProcessUtils.getProcessStats());
	}
}
