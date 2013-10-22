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
	private final String pid = processId();

	public PidFile(String pidFileName) throws IOException {
		if (pid == null) {
			pidFile = null;
		} else {
			pidFile = Paths.get(pidFileName);
			Files.write(pidFile, pid.getBytes(), StandardOpenOption.CREATE);
		}
	}

	public String getPid() {
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

	public static String processId() {
		return System.getProperty("PID");
	}
}
