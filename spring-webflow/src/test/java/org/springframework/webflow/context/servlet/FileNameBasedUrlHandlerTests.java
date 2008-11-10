/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.context.servlet;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;

public class FileNameBasedUrlHandlerTests extends TestCase {

	private DefaultFlowUrlHandler urlHandler = new FileNameBasedFlowUrlHandler();
	private MockHttpServletRequest request = new MockHttpServletRequest();

	public void testGetFlowId() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	public void testGetFlowIdNoPathInfo() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app/foo.htm");
		request.setPathInfo(null);
		request.setRequestURI("/springtravel/app/foo.htm");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	public void testGetFlowIdOnlyContextPath() {
		request.setContextPath("/springtravel");
		request.setRequestURI("/springtravel");
		assertEquals("", urlHandler.getFlowId(request));
	}
}