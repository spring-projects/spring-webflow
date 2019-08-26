/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.context.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class FilenameFlowUrlHandlerTests {

	private DefaultFlowUrlHandler urlHandler = new FilenameFlowUrlHandler();
	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void testGetFlowId() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	@Test
	public void testGetFlowIdNoPathInfo() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app/foo.htm");
		request.setPathInfo(null);
		request.setRequestURI("/springtravel/app/foo.htm");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	@Test
	public void testGetFlowIdOnlyContextPath() {
		request.setContextPath("/springtravel");
		request.setRequestURI("/springtravel");
		assertEquals("", urlHandler.getFlowId(request));
	}

	@Test
	public void testCreateFlowDefinitionUrlWithPathInfo() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String flowDefUrl = urlHandler.createFlowDefinitionUrl("bar", null, request);
		assertEquals("/springtravel/app/bar", flowDefUrl);
	}

	@Test
	public void testCreateFlowDefinitionUrlWithPathInfoNestedPath() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/nestedPath/foo");
		request.setRequestURI("/springtravel/app/nestedPath/foo");
		String flowDefUrl = urlHandler.createFlowDefinitionUrl("bar", null, request);
		assertEquals("/springtravel/app/nestedPath/bar", flowDefUrl);
	}

	@Test
	public void testCreateFlowDefinitionUrlWithPathInfoNestedPathAndFileExtension() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/nestedPath/foo.flow");
		request.setRequestURI("/springtravel/app/nestedPath/foo.flow");
		String flowDefUrl = urlHandler.createFlowDefinitionUrl("bar", null, request);
		assertEquals("/springtravel/app/nestedPath/bar.flow", flowDefUrl);
	}

	@Test
	public void testCreateFlowDefinitionUrlWithServletPathAndFileExtension() {
		request.setContextPath("/springtravel");
		request.setServletPath("/nestedPath/foo.flow");
		request.setRequestURI("/springtravel/nestedPath/foo.flow");
		String flowDefUrl = urlHandler.createFlowDefinitionUrl("bar", null, request);
		assertEquals("/springtravel/nestedPath/bar.flow", flowDefUrl);
	}
}