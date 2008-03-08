/*
 * Copyright 2004-2007 the original author or authors.
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * Unit tests for {@link ServletExternalContext}.
 */
public class ServletExternalContextTests extends TestCase {

	private MockServletContext servletContext;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private ServletExternalContext context;

	protected void setUp() {
		servletContext = new MockServletContext();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		context = new ServletExternalContext(servletContext, request, response);
	}

	public void testGetContextPath() {
		request.setContextPath("/foo");
		assertEquals("/foo", request.getContextPath());
	}

	public void testRequestParameters() {
		assertTrue(context.getRequestParameterMap().isEmpty());
	}

	public void testGetNativeObjects() {
		assertEquals(servletContext, context.getNativeContext());
		assertEquals(request, context.getNativeRequest());
		assertEquals(response, context.getNativeResponse());
	}

	public void testNotAnAjaxRequest() {
		assertFalse(context.isAjaxRequest());
	}

	public void testAjaxRequestAcceptHeader() {
		context.setAjaxRequest(true);
		assertTrue(context.isAjaxRequest());
	}

	public void testNotResponseCommitted() {
		assertFalse(context.isResponseCommitted());
	}

	public void testCommitExecutionRedirect() {
		context.requestFlowExecutionRedirect();
		assertTrue(context.isResponseCommitted());
		assertTrue(context.flowExecutionRedirectRequested());
	}

	public void testCommitFlowRedirect() {
		context.requestFlowDefinitionRedirect("foo", null);
		assertTrue(context.isResponseCommitted());
		assertTrue(context.flowDefinitionRedirectRequested());
		assertEquals("foo", context.getFlowRedirectFlowId());
	}

	public void testCommitExternalRedirect() {
		context.requestExternalRedirect("foo");
		assertTrue(context.isResponseCommitted());
		assertTrue(context.externalRedirectRequested());
		assertEquals("foo", context.getExternalRedirectUrl());
	}

	public void testCommitExecutionRedirectPopup() {
		context.requestFlowExecutionRedirect();
		context.requestRedirectInPopup();
		assertTrue(context.isResponseCommitted());
		assertTrue(context.flowExecutionRedirectRequested());
		assertTrue(context.redirectInPopup());
	}

	public void testCommitFlowRedirectPopup() {
		context.requestFlowDefinitionRedirect("foo", null);
		context.requestRedirectInPopup();
		assertTrue(context.isResponseCommitted());
		assertTrue(context.flowDefinitionRedirectRequested());
		assertEquals("foo", context.getFlowRedirectFlowId());
		assertTrue(context.redirectInPopup());
	}

	public void testCommitExternalRedirectPopup() {
		context.requestExternalRedirect("foo");
		context.requestRedirectInPopup();
		assertTrue(context.isResponseCommitted());
		assertTrue(context.externalRedirectRequested());
		assertEquals("foo", context.getExternalRedirectUrl());
		assertTrue(context.redirectInPopup());
	}

}
