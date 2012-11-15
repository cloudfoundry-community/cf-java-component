package vcap.component.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Mike Heath <elcapo@gmail.com>
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
