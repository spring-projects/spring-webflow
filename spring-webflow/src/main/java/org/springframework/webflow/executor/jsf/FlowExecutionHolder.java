/*
 * Copyright 2002-2006 the original author or authors.
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

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.repository.FlowExecutionKey;

/**
 * A holder storing a reference to a flow execution and the key of that flow
 * execution if it has been (or is about to be) managed in a repository.
 * 
 * @author Keith Donald
 */
public class FlowExecutionHolder implements Serializable {

	/**
	 * The flow execution continuation key (may be null if the flow execution
	 * has not yet been generated a repository key). May change as well over the
	 * life of this object, as a flow execution can be given a new key to
	 * capture its state at another point in time.
	 */
	private FlowExecutionKey flowExecutionKey;

	/**
	 * The held flow execution representing the state of an ongoing conversation
	 * at a point in time.
	 */
	private FlowExecution flowExecution;

	private ViewSelection viewSelection;
	
	private boolean needsSave;
	
	/**
	 * Creates a new flow execution holder for a flow execution that has not yet
	 * been placed in a repository.
	 * @param flowExecution the flow execution to hold
	 */
	public FlowExecutionHolder(FlowExecution flowExecution) {
		this.flowExecution = flowExecution;
	}

	/**
	 * Creates a new flow execution holder.
	 * @param flowExecutionKey the continuation key
	 * @param flowExecution the flow execution to hold
	 */
	public FlowExecutionHolder(FlowExecutionKey flowExecutionKey, FlowExecution flowExecution) {
		this.flowExecutionKey = flowExecutionKey;
		this.flowExecution = flowExecution;
	}

	/**
	 * Returns the continuation key.
	 */
	public FlowExecutionKey getFlowExecutionKey() {
		return flowExecutionKey;
	}

	/**
	 * Sets the continuation key.
	 */
	public void setFlowExecutionKey(FlowExecutionKey continuationKey) {
		this.flowExecutionKey = continuationKey;
	}

	/**
	 * Returns the flow execution.
	 */
	public FlowExecution getFlowExecution() {
		return flowExecution;
	}

	public ViewSelection getViewSelection() {
		return viewSelection;
	}

	public void setViewSelection(ViewSelection viewSelection) {
		this.viewSelection = viewSelection;
	}

	public boolean needsSave() {
		return needsSave;
	}
	
	public void markNeedsSave() {
		this.needsSave = true;
	}
	
	public String toString() {
		return new ToStringCreator(this).append("flowExecutionKey", flowExecutionKey).append("flowExecution",
				flowExecution).toString();
	}
}