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

import org.springframework.webflow.definition.StateDefinition;

/**
 * Exception thrown to veto the entering of a state of a flow. Typically thrown by {@link FlowExecutionListener} objects
 * that apply security or other runtime constraint checks to flow executions.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class EnterStateVetoException extends FlowExecutionException {

	/**
	 * The state whose entering was vetoed.
	 */
	private String vetoedStateId;

	/**
	 * Create a new enter state veto exception.
	 * @param flowId the active flow
	 * @param sourceStateId the current state when the veto operation occured
	 * @param vetoedStateId the state for which entering is vetoed
	 * @param message a descriptive message
	 */
	public EnterStateVetoException(String flowId, String sourceStateId, String vetoedStateId, String message) {
		super(flowId, sourceStateId, message);
		this.vetoedStateId = vetoedStateId;
	}

	/**
	 * Create a new enter state veto exception.
	 * @param flowId the active flow
	 * @param sourceStateId the current state when the veto operation occured
	 * @param vetoedStateId the state for which entering is vetoed
	 * @param message a descriptive message
	 * @param cause the underlying cause
	 */
	public EnterStateVetoException(String flowId, String sourceStateId, String vetoedStateId, String message,
			Throwable cause) {
		super(flowId, sourceStateId, message, cause);
		this.vetoedStateId = vetoedStateId;
	}

	/**
	 * Create a new enter state veto exception.
	 * @param context the flow execution request context
	 * @param vetoedState the state for which entering is vetoed
	 * @param message a descriptive message
	 */
	public EnterStateVetoException(RequestContext context, StateDefinition vetoedState, String message) {
		this(context.getActiveFlow().getId(), context.getCurrentState().getId(), vetoedState.getId(), message);
	}

	/**
	 * Create a new enter state veto exception.
	 * @param context the flow execution request context
	 * @param vetoedState the state for which entering is vetoed
	 * @param message a descriptive message
	 * @param cause the underlying cause
	 */
	public EnterStateVetoException(RequestContext context, StateDefinition vetoedState, String message, Throwable cause) {
		this(context.getActiveFlow().getId(), context.getCurrentState().getId(), vetoedState.getId(), message, cause);
	}

	/**
	 * Returns the state for which entering was vetoed.
	 */
	public String getVetoedStateId() {
		return vetoedStateId;
	}
}