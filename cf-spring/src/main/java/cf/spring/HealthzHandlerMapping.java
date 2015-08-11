/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mike Heath
 */
public class HealthzHandlerMapping extends AbstractUrlHandlerMapping {

	public HealthzHandlerMapping() {
		this(Integer.MIN_VALUE);
	}

	public HealthzHandlerMapping(int order) {
		setOrder(order);

		registerHandler("/healthz", new HttpRequestHandler() {
			@Override
			public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
				httpServletResponse.setContentType("text/plain;charset=utf-8");
				httpServletResponse.getWriter().write("ok");
			}
		});
	}
}
