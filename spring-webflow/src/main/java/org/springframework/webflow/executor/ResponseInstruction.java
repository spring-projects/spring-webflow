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
package org.springframework.webflow.executor;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.execution.support.FlowExecutionRedirect;

/**
 * Immutable value object that provides clients with information about a
 * response to issue.
 * <p>
 * There are five different <i>types</i> of response instruction:
 * <ul>
 * <li>An {@link #isApplicationView() application view}.</li>
 * <li>A {@link #isFlowExecutionRedirect() flow execution redirect}, showing
 * an application view via a redirect that refreshes an ongoing flow
 * execution.</li>
 * <li>A {@link #isFlowDefinitionRedirect() flow definition redirect},
 * launching an entirely new flow execution.</li>
 * <li>An {@link #isExternalRedirect() external redirect}, redirecting
 * to an external URL.</li>
 * <li>A {@link #isNull() null view}, not showing a response at all.</li>
 * </ul>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ResponseInstruction implements Serializable {

	/**
	 * The persistent identifier of the flow execution that
	 * resulted in this response instruction.
	 */
	private String flowExecutionKey;

	/**
	 * Basic state info on the flow execution.
	 */
	private transient FlowExecutionContext flowExecutionContext;

	/**
	 * The view selection that was made.
	 */
	private ViewSelection viewSelection;

	/**
	 * Create a new response instruction for a paused flow execution.
	 * @param flowExecutionKey the persistent identifier of the flow execution
	 * @param flowExecutionContext the current flow execution context
	 * @param viewSelection the selected view
	 */
	public ResponseInstruction(String flowExecutionKey, FlowExecutionContext flowExecutionContext,
			ViewSelection viewSelection) {
		Assert.notNull(flowExecutionKey, "The flow execution key is required");
		this.flowExecutionKey = flowExecutionKey;
		init(flowExecutionContext, viewSelection);
	}

	/**
	 * Create a new response instruction for an ended flow execution. No
	 * flow execution key needs to be provided since the flow execution no longer
	 * exists and cannot be referenced any longer.
	 * @param flowExecutionContext the current flow execution context (inactive)
	 * @param viewSelection the selected view
	 */
	public ResponseInstruction(FlowExecutionContext flowExecutionContext, ViewSelection viewSelection) {
		init(flowExecutionContext, viewSelection);
	}

	/**
	 * Helper to initialize the flow execution context and view selection.
	 */
	private void init(FlowExecutionContext flowExecutionContext, ViewSelection viewSelection) {
		Assert.notNull(flowExecutionContext, "The flow execution context is required");
		Assert.notNull(viewSelection, "The view selection is required");
		this.flowExecutionContext = flowExecutionContext;
		this.viewSelection = viewSelection;
	}

	/**
	 * Returns the persistent identifier of the flow execution.
	 */
	public String getFlowExecutionKey() {
		return flowExecutionKey;
	}

	/**
	 * Returns the flow execution context representing the current state of the
	 * execution. It could be that the returned flow execution is
	 * {@link FlowExecutionContext#isActive() inactive}.
	 */
	public FlowExecutionContext getFlowExecutionContext() {
		return flowExecutionContext;
	}

	/**
	 * Returns the view selection selected by the flow execution.
	 */
	public ViewSelection getViewSelection() {
		return viewSelection;
	}

	/**
	 * Returns true if this is an instruction to render an application view for
	 * an "active" (in progress) flow execution.
	 */
	public boolean isActiveView() {
		return isApplicationView() && flowExecutionContext.isActive();
	}

	/**
	 * Returns true if this is an instruction to render an application view for
	 * an "ended" (inactive) flow execution from an end state.
	 */
	public boolean isEndingView() {
		return isApplicationView() && !flowExecutionContext.isActive();
	}
	
	// response types

	/**
	 * Returns true if this is an "application view" (forward) response
	 * instruction.
	 */
	public boolean isApplicationView() {
		return viewSelection instanceof ApplicationView;
	}

	/**
	 * Returns true if this is an instruction to perform a redirect to the
	 * current flow execution to render an application view.
	 */
	public boolean isFlowExecutionRedirect() {
		return viewSelection instanceof FlowExecutionRedirect;
	}

	/**
	 * Returns true if this is an instruction to launch an entirely new
	 * (independent) flow execution.
	 */
	public boolean isFlowDefinitionRedirect() {
		return viewSelection instanceof FlowDefinitionRedirect;
	}

	/**
	 * Returns true if this an instruction to perform a redirect to an external
	 * URL.
	 */
	public boolean isExternalRedirect() {
		return viewSelection instanceof ExternalRedirect;
	}

	/**
	 * Returns true if this is a "null" response instruction, e.g.
	 * no response needs to be rendered.
	 */
	public boolean isNull() {
		return viewSelection == ViewSelection.NULL_VIEW;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ResponseInstruction)) {
			return false;
		}
		ResponseInstruction other = (ResponseInstruction)o;
		if (getFlowExecutionKey() != null) {
			return getFlowExecutionKey().equals(other.getFlowExecutionKey())
					&& viewSelection.equals(other.viewSelection);
		}
		else {
			return other.getFlowExecutionKey() == null && viewSelection.equals(other.viewSelection);
		}
	}

	public int hashCode() {
		int hashCode = viewSelection.hashCode();
		if (getFlowExecutionKey() != null) {
			hashCode += getFlowExecutionKey().hashCode();
		}
		return hashCode;
	}

	public String toString() {
		return new ToStringCreator(this).append("flowExecutionKey", flowExecutionKey)
			.append("viewSelection", viewSelection).append("flowExecutionContext", flowExecutionContext).toString();
	}
}