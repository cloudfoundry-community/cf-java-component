/*
 *   Copyright (c) 2013 Mike Heath.  All rights reserved.
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
package cf.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import cf.component.util.PidFile;

import java.io.IOException;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class PidFileFactory implements FactoryBean<PidFile>, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(PidFileFactory.class);

	private final PidFile pidFile;

	public PidFileFactory(String pidFileName) throws IOException {
		pidFile = new PidFile(pidFileName);
	}

	@Override
	public void destroy() throws Exception {
		try {
			LOGGER.debug("Deleting pid file");
			pidFile.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PidFile getObject() throws Exception {
		return pidFile;
	}

	@Override
	public Class<?> getObjectType() {
		return pidFile.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
