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
package org.springframework.webflow.test;

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionableState;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.View;

/**
 * Mock implementation of the {@link RequestControlContext} interface to facilitate standalone Flow and State unit
 * tests.
 * 
 * @see org.springframework.webflow.execution.RequestContext
 * @see org.springframework.webflow.execution.FlowSession
 * @see org.springframework.webflow.engine.State
 * 
 * @author Keith Donald
 */
public class MockRequestControlContext extends MockRequestContext implements RequestControlContext {

	private boolean alwaysRedirectOnPause;

	/**
	 * Creates a new mock request control context.
	 */
	public MockRequestControlContext() {
	}

	/**
	 * Creates a new mock request control context for controlling a mock execution of the provided flow definition.
	 * @param flow the flow definition
	 */
	public MockRequestControlContext(Flow flow) {
		super(flow);
	}

	/**
	 * Creates a new mock request control context for controlling a flow execution.
	 * @param flowExecutionContext the flow execution context
	 */
	public MockRequestControlContext(FlowExecutionContext flowExecutionContext) {
		super(flowExecutionContext);
	}

	// implementing RequestControlContext

	public void setCurrentState(State state) {
		getMockFlowExecutionContext().getMockActiveSession().setState(state);
	}

	public FlowExecutionKey assignFlowExecutionKey() {
		GeneratedFlowExecutionKey key = new GeneratedFlowExecutionKey();
		getMockFlowExecutionContext().setKey(key);
		return key;
	}

	public void viewRendering(View view) {
	}

	public void viewRendered(View view) {
	}

	public boolean handleEvent(Event event) {
		setCurrentEvent(event);
		return ((Flow) getActiveFlow()).handleEvent(this);
	}

	public boolean execute(Transition transition) {
		return transition.execute((TransitionableState) getCurrentState(), this);
	}

	public void removeAllFlowExecutionSnapshots() {

	}

	public void removeCurrentFlowExecutionSnapshot() {

	}

	public void updateCurrentFlowExecutionSnapshot() {

	}

	public void start(Flow flow, MutableAttributeMap input) throws IllegalStateException {
		MockFlowSession session = new MockFlowSession(flow, input);
		if (getFlowExecutionContext().isActive()) {
			session.setParent(getFlowExecutionContext().getActiveSession());
		}
		getMockFlowExecutionContext().setActiveSession(session);
		flow.start(this, input);
	}

	public void endActiveFlowSession(String outcome, MutableAttributeMap output) throws IllegalStateException {
		MockFlowSession endingSession = getMockFlowExecutionContext().getMockActiveSession();
		endingSession.getDefinitionInternal().end(this, outcome, output);
		getMockFlowExecutionContext().setActiveSession(endingSession.getParent());
		if (!getMockFlowExecutionContext().hasEnded()) {
			handleEvent(new Event(endingSession.getState(), outcome, output));
		}
	}

	public boolean getRedirectOnPause() {
		return alwaysRedirectOnPause;
	}

	// implementation specific accessors for testing

	public void setAlwaysRedirectOnPause(boolean alwaysRedirectOnPause) {
		this.alwaysRedirectOnPause = alwaysRedirectOnPause;
	}

}