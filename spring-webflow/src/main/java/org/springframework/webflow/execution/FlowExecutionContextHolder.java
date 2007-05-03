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
package org.springframework.webflow.execution;

import org.springframework.util.Assert;

/**
 * Simple holder class that associates a {@link FlowExecutionContext} instance
 * with the current thread. The FlowExecutionContext will not be inherited by
 * any child threads spawned by the current thread.
 * <p>
 * Used as a central holder for the current FlowExecutionContext in Spring Web
 * Flow, wherever necessary. Often used by artifacts needing to access the
 * current active flow execution.
 * 
 * @see FlowExecutionContext
 * 
 * @author Ben Hale
 * @since 1.1
 */
public class FlowExecutionContextHolder {

	private static final ThreadLocal flowExecutionContextHolder = new ThreadLocal();

	/**
	 * Associate the given FlowExecutionContext with the current thread.
	 * @param flowExecutionContext the current FlowExecutionContext, or
	 * <code>null</code> to reset the thread-bound context
	 */
	public static void setFlowExecutionContext(FlowExecutionContext flowExecutionContext) {
		flowExecutionContextHolder.set(flowExecutionContext);
	}

	/**
	 * Return the FlowExecutionContext associated with the current thread, if
	 * any.
	 * @return the current FlowExecutionContext
	 * @throws IllegalStateException if no FlowExecutionContext is bound to this
	 * thread
	 */
	public static FlowExecutionContext getFlowExecutionContext() {
		Assert.state(flowExecutionContextHolder.get() != null,
				"No flow execution context is bound to this thread");
		return (FlowExecutionContext) flowExecutionContextHolder.get();
	}

	// not instantiable
	private FlowExecutionContextHolder() {
	}
}
