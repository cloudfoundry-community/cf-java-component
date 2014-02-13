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
		final String pid = PidFile.processId();
		if (pid == null) {
			return null;
		}
		final String ps = run("ps", "-o", "rss=", "-o", "pcpu=", "-p", pid);
		final String[] parts = ps.split("\\s+");
		return new ProcessStats(
				Long.valueOf(parts[0]),
				Float.valueOf(parts[1])
		);

	}

	public static class ProcessStats {
		public final long residentSetSize;
		public final float cpuUtilization;

		public ProcessStats(long residentSetSize, float cpuUtilization) {
			this.residentSetSize = residentSetSize;
			this.cpuUtilization = cpuUtilization;
		}

		@Override
		public String toString() {
			return "ProcessStats{" +
					"rss=" + residentSetSize +
					", pcpu=" + cpuUtilization +
					'}';
		}
	}
}
