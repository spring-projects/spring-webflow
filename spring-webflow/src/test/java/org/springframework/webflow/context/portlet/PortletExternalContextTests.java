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
package org.springframework.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockPortletResponse;
import org.springframework.webflow.context.servlet.ServletExternalContext;

/**
 * Unit tests for {@link ServletExternalContext}.
 */
public class PortletExternalContextTests extends TestCase {

	private MockPortletContext portletContext;

	private MockPortletRequest request;

	private MockPortletResponse response;

	private PortletExternalContext context;

	protected void setUp() {
		portletContext = new MockPortletContext();
		request = new MockActionRequest();
		response = new MockActionResponse();
		context = new PortletExternalContext(portletContext, request, response);
	}

	public void testGetContextPath() {
		request.setContextPath("/foo");
		assertEquals("/foo", request.getContextPath());
	}

	public void testRequestParameters() {
		assertTrue(context.getRequestParameterMap().isEmpty());
	}

	public void testGetNativeObjects() {
		assertEquals(portletContext, context.getNativeContext());
		assertEquals(request, context.getNativeRequest());
		assertEquals(response, context.getNativeResponse());
	}

	public void testNotAnAjaxRequest() {
		assertFalse(context.isAjaxRequest());
	}

	public void testAjaxRequestAcceptHeader() {
		assertFalse(context.isAjaxRequest());
	}

	public void testNotResponseCommitted() {
		assertFalse(context.isResponseComplete());
	}

	public void testCommitExecutionRedirect() {
		context.requestFlowExecutionRedirect();
		assertTrue(context.getFlowExecutionRedirectRequested());
	}

	public void testCommitFlowRedirect() {
		context.requestFlowDefinitionRedirect("foo", null);
		assertTrue(context.getFlowDefinitionRedirectRequested());
		assertEquals("foo", context.getFlowRedirectFlowId());
	}

	public void testCommitExternalRedirect() {
		context.requestExternalRedirect("foo");
		assertTrue(context.getExternalRedirectRequested());
		assertEquals("foo", context.getExternalRedirectUrl());
	}

	public void testCommitExecutionRedirectPopup() {
		context.requestFlowExecutionRedirect();
		context.requestRedirectInPopup();
		assertTrue(context.getFlowExecutionRedirectRequested());
		assertTrue(context.getRedirectInPopup());
	}

	public void testCommitFlowRedirectPopup() {
		context.requestFlowDefinitionRedirect("foo", null);
		context.requestRedirectInPopup();
		assertTrue(context.getFlowDefinitionRedirectRequested());
		assertEquals("foo", context.getFlowRedirectFlowId());
		assertTrue(context.getRedirectInPopup());
	}

	public void testCommitExternalRedirectPopup() {
		context.requestExternalRedirect("foo");
		context.requestRedirectInPopup();
		assertTrue(context.getExternalRedirectRequested());
		assertEquals("foo", context.getExternalRedirectUrl());
		assertTrue(context.getRedirectInPopup());
	}

	public void testResponseAllowed() {
		assertFalse(context.isResponseAllowed());
	}

	public void testIsActionPhase() {
		assertTrue(context.isActionPhase());
	}

	public void testIsRenderPhase() {
		assertFalse(context.isRenderPhase());
	}

}
