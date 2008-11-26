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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;

/**
 * A simple transitionable state that when entered will execute the first transition whose matching criteria evaluates
 * to <code>true</code> in the {@link RequestContext context} of the current request.
 * <p>
 * A decision state is a convenient, simple way to encapsulate reusable state transition logic in one place.
 * 
 * @author Keith Donald
 */
public class DecisionState extends TransitionableState {

	/**
	 * Creates a new decision state.
	 * @param flow the owning flow
	 * @param stateId the state identifier (must be unique to the flow)
	 * @throws IllegalArgumentException when this state cannot be added to given flow, e.g. because the id is not unique
	 */
	public DecisionState(Flow flow, String stateId) throws IllegalArgumentException {
		super(flow, stateId);
	}

	/**
	 * Specialization of State's <code>doEnter</code> template method that executes behavior specific to this state
	 * type in polymorphic fashion.
	 * <p>
	 * Simply looks up the first transition that matches the state of the context and executes it.
	 * @param context the control context for the currently executing flow, used by this state to manipulate the flow
	 * execution
	 * @throws FlowExecutionException if an exception occurs in this state
	 */
	protected void doEnter(RequestControlContext context) throws FlowExecutionException {
		getRequiredTransition(context).execute(this, context);
	}
}