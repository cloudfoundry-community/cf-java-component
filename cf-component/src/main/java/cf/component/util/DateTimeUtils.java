package cf.component.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DateTimeUtils {

	public static String formatDateTime(long time) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date(time));
	}

	public static String formatUptime(long startTime) {
		long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
		final long days = seconds / TimeUnit.DAYS.toSeconds(1);
		seconds -= TimeUnit.DAYS.toSeconds(days);
		final long hours = seconds / TimeUnit.HOURS.toSeconds(1);
		seconds -= TimeUnit.HOURS.toSeconds(hours);
		final long minutes = seconds / TimeUnit.MINUTES.toSeconds(1);
		seconds -= TimeUnit.MINUTES.toSeconds(minutes);
		return String.format("%dd:%dh:%dm:%ds", days, hours, minutes, seconds);
	}

}
