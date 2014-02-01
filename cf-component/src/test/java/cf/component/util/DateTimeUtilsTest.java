package cf.component.util;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DateTimeUtilsTest {

	@Test
	public void formatUptimeTest() {
		long start = System.currentTimeMillis();
		final String oneSecond = DateTimeUtils.formatUptime(start - TimeUnit.SECONDS.toMillis(1));
		assertEquals(oneSecond, "0d:0h:0m:1s");
		final String oneMinute = DateTimeUtils.formatUptime(start - TimeUnit.MINUTES.toMillis(1));
		assertEquals(oneMinute, "0d:0h:1m:0s");
		final String oneHour = DateTimeUtils.formatUptime(start - TimeUnit.HOURS.toMillis(1));
		assertEquals(oneHour, "0d:1h:0m:0s");
		final String oneDay = DateTimeUtils.formatUptime(start - TimeUnit.DAYS.toMillis(1));
		assertEquals(oneDay, "1d:0h:0m:0s");
		final String twoTwoTwoTwo = DateTimeUtils.formatUptime(
				start - TimeUnit.DAYS.toMillis(2) - TimeUnit.HOURS.toMillis(2) -
						TimeUnit.MINUTES.toMillis(2) - TimeUnit.SECONDS.toMillis(2));
		assertEquals(twoTwoTwoTwo, "2d:2h:2m:2s");
	}

}
