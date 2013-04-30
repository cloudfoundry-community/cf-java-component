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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class PidFile implements Closeable {

	private final Path pidFile;
	private final Integer pid;

	public PidFile(String pidFileName) throws IOException {
		pid = processId();
		if (pid == null) {
			pidFile = null;
		} else {
			pidFile = Paths.get(pidFileName);
			Files.write(pidFile, pid.toString().getBytes(), StandardOpenOption.CREATE);
		}
	}

	public Integer getPid() {
		return pid;
	}

	/**
	 * Deletes the pid file.
	 *
	 * @throws IOException if an error occurs deleting the pid file.
	 */
	@Override
	public void close() throws IOException {
		if (pidFile != null) {
			Files.delete(pidFile);
		}
	}

	public static Integer processId() {
		try {
			// Open the symlink /proc/self and follow symlink to determine pid -- This will only work on Unix based systems.
			final Path pidPath = Paths.get("/proc", "self").toRealPath();
			return Integer.valueOf(pidPath.getFileName().toString());
		} catch (Exception e) {
			return null;
		}
	}
}
