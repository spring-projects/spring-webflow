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
package org.springframework.faces.webflow;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.execution.FlowExecutionContextHolder;

/**
 * A servlet filter used to guarantee that web flow context information is cleaned up in a JSF environment.
 * 
 * @author Ben Hale
 * @since 1.0.4
 */
public class FlowSystemCleanupFilter extends OncePerRequestFilter {

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			chain.doFilter(request, response);
		} finally {
			cleanupCurrentFlowExecution(request);
			ExternalContextHolder.setExternalContext(null);
		}
	}

	/**
	 * Cleans up the current flow execution in the request context if necessary. Specifically, handles unlocking the
	 * execution if necessary, setting the holder to null, and cleaning up the flow execution context thread local. Can
	 * be safely called even if no execution is bound or one is bound but not locked.
	 * @param request the servlet request
	 */
	private void cleanupCurrentFlowExecution(ServletRequest request) {
		if (isFlowExecutionRestored(request)) {
			FlowExecutionContextHolder.setFlowExecutionContext(null);
			getFlowExecutionHolder(request).unlockFlowExecutionIfNecessary();
			request.removeAttribute(FlowExecutionHolderUtils.getFlowExecutionHolderKey());
		}
	}

	/**
	 * Returns true if the flow execution has been restored in the current thread.
	 * @param request the servlet request
	 * @return true if restored, false otherwise
	 */
	private boolean isFlowExecutionRestored(ServletRequest request) {
		return getFlowExecutionHolder(request) != null;
	}

	/**
	 * Returns the current flow execution holder for the given servlet request.
	 * @param request the servlet request
	 * @return the flow execution holder, or <code>null</code> if none set.
	 */
	private FlowExecutionHolder getFlowExecutionHolder(ServletRequest request) {
		return (FlowExecutionHolder) request.getAttribute(FlowExecutionHolderUtils.getFlowExecutionHolderKey());
	}
}
