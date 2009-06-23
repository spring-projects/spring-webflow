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
package org.springframework.webflow.execution;

import org.springframework.core.NamedThreadLocal;

/**
 * Simple holder class that associates a {@link RequestContext} instance with the current thread. The RequestContext
 * will not be inherited by any child threads spawned by the current thread.
 * <p>
 * Used as a central holder for the current RequestContext in Spring Web Flow, wherever necessary. Often used by
 * integration artifacts needing access to the current flow execution.
 * 
 * @see RequestContext
 * 
 * @author Jeremy Grelle
 */
public class RequestContextHolder {

	private static final ThreadLocal requestContextHolder = new NamedThreadLocal("Flow RequestContext");

	/**
	 * Associate the given RequestContext with the current thread.
	 * @param requestContext the current RequestContext, or <code>null</code> to reset the thread-bound context
	 */
	public static void setRequestContext(RequestContext requestContext) {
		requestContextHolder.set(requestContext);
	}

	/**
	 * Return the RequestContext associated with the current thread, if any.
	 * @return the current RequestContext
	 * @throws IllegalStateException if no RequestContext is bound to this thread
	 */
	public static RequestContext getRequestContext() {
		return (RequestContext) requestContextHolder.get();
	}

	// not instantiable
	private RequestContextHolder() {
	}
}
