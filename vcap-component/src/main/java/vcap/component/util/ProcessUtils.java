package vcap.component.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Mike Heath <heathma@ldschurch.org>
 */
public class ProcessUtils {

	public static String run(String... command) throws IOException {
		final Process process = new ProcessBuilder(command).start();
		try
				(InputStream stream = process.getInputStream();
				 InputStreamReader reader = new InputStreamReader(stream);
				 BufferedReader bufferedReader = new BufferedReader(reader)
				) {
			final StringBuilder output = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				output.append(line);
			}
			return output.toString();
		}
	}

	public static ProcessStats getProcessStats() throws IOException {
		final Integer pid = PidFile.processId();
		if (pid == null) {
			return null;
		}
		final String ps = run("ps", "-o", "rss=", "-o", "vsize=", "-o", "pcpu=", "-p", pid.toString());
		final String[] parts = ps.split("\\s+");
		return new ProcessStats(
				Long.valueOf(parts[0]),
				Long.valueOf(parts[1]),
				Float.valueOf(parts[2])
		);

	}

	public static class ProcessStats {
		public final long residentSetSize;
		public final long  vmSize;
		public final float cpuUtilization;

		public ProcessStats(long residentSetSize, long vmSize, float cpuUtilization) {
			this.residentSetSize = residentSetSize;
			this.vmSize = vmSize;
			this.cpuUtilization = cpuUtilization;
		}

		@Override
		public String toString() {
			return "ProcessStats{" +
					"rss=" + residentSetSize +
					", vsize=" + vmSize +
					", pcpu=" + cpuUtilization +
					'}';
		}
	}
}
