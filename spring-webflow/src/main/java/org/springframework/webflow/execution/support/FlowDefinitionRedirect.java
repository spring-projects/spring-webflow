/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.execution.support;

import java.util.Collections;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.ViewSelection;

/**
 * Concrete response type that requests that a <i>new</i> execution of a flow
 * definition (representing the start of a new conversation) be launched.
 * <p>
 * This allows "redirect to new flow" semantics; useful for restarting a flow
 * after completion, or starting an entirely new flow from within the end state
 * of another flow definition.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public final class FlowDefinitionRedirect extends ViewSelection {

	/**
	 * The id of the flow definition to launch.
	 */
	private final String flowDefinitionId;

	/**
	 * A map of input attributes to pass to the flow.
	 */
	private final Map executionInput;

	/**
	 * Creates a new flow definition redirect.
	 * @param flowDefinitionId the id of the flow definition to launch
	 * @param executionInput the input data to pass to the new flow execution on launch
	 */
	public FlowDefinitionRedirect(String flowDefinitionId, Map executionInput) {
		Assert.hasText(flowDefinitionId, "The flow definition id is required");
		this.flowDefinitionId = flowDefinitionId;
		if (executionInput == null) {
			executionInput = Collections.EMPTY_MAP;
		}
		this.executionInput = executionInput;
	}

	/**
	 * Return the id of the flow definition to launch a new execution of.
	 */
	public String getFlowDefinitionId() {
		return flowDefinitionId;
	}

	/**
	 * Return the flow execution input map as an unmodifiable map. Never returns
	 * null.
	 */
	public Map getExecutionInput() {
		return Collections.unmodifiableMap(executionInput);
	}

	public boolean equals(Object o) {
		if (!(o instanceof FlowDefinitionRedirect)) {
			return false;
		}
		FlowDefinitionRedirect other = (FlowDefinitionRedirect)o;
		return flowDefinitionId.equals(other.flowDefinitionId) && executionInput.equals(other.executionInput);
	}

	public int hashCode() {
		return flowDefinitionId.hashCode() + executionInput.hashCode();
	}

	public String toString() {
		return "flowRedirect:'" + flowDefinitionId + "'";
	}
}