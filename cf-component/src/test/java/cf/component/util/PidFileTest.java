package cf.component.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Mike Heath
 */
public class PidFileTest {

	@Test
	public void singleInstanceTest() throws Exception {
		final Path pidFile = Files.createTempFile("pidfile", "test");
		try (final PidFile pid = new PidFile(pidFile.toString())) {
			try {
				new PidFile(pidFile.toString());
				Assert.fail("An exception should have been thrown indicating the process is already running.");
			} catch (Error e) {
				// Pass
			}
		}
	}

}
