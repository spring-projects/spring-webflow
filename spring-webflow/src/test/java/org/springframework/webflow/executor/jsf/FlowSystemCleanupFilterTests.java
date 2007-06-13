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
package org.springframework.webflow.executor.jsf;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.execution.FlowExecutionContextHolder;
import org.springframework.webflow.test.MockExternalContext;

public class FlowSystemCleanupFilterTests extends TestCase {

	private FlowSystemCleanupFilter filter;

	private ServletRequest request;

	private ServletResponse response;

	private FilterChain chain;

	protected void setUp() throws Exception {
		filter = new FlowSystemCleanupFilter();
		request = new MockHttpServletRequest();
		request.setAttribute(getFlowExecutionHolderKey(), new FlowExecutionHolder(new FlowExecutionImpl()));
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		FlowExecutionContextHolder.setFlowExecutionContext(new FlowExecutionImpl());
		ExternalContextHolder.setExternalContext(new MockExternalContext());
	}

	public void testCleanup() throws ServletException, IOException {
		filter.doFilter(request, response, chain);

		assertNull("Should have cleaned up the flow execution", request.getAttribute(getFlowExecutionHolderKey()));
		try {
			FlowExecutionContextHolder.getFlowExecutionContext();
			fail("Should have an empty holder");
		} catch (IllegalStateException e) {
		}
		try {
			ExternalContextHolder.getExternalContext();
			fail("Should have an empty holder");
		} catch (IllegalStateException e) {
		}
	}

	public void testExceptionThrown() throws ServletException, IOException {
		try {
			filter.doFilter(request, response, new ExceptionThrowingMockFilterChain());
		} catch (RuntimeException e) {
			assertNull("Should have cleaned up the flow execution", request.getAttribute(getFlowExecutionHolderKey()));
			try {
				FlowExecutionContextHolder.getFlowExecutionContext();
				fail("Should have an empty holder");
			} catch (IllegalStateException e1) {
			}
			try {
				ExternalContextHolder.getExternalContext();
				fail("Should have an empty holder");
			} catch (IllegalStateException e1) {
			}
		}
	}

	private static String getFlowExecutionHolderKey() {
		return FlowExecutionHolder.class.getName();
	}

	private class ExceptionThrowingMockFilterChain extends MockFilterChain {

		public void doFilter(ServletRequest request, ServletResponse response) {
			throw new RuntimeException();
		}

	}
}
