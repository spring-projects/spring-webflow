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
import org.springframework.webflow.execution.FlowSession;

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

	private boolean flowExecutionRedirectSent;

	private boolean alwaysRedirectOnPause;

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

	public void start(Flow flow, MutableAttributeMap input) throws IllegalStateException {
		MockFlowSession session = new MockFlowSession(flow, input);
		if (getFlowExecutionContext().isActive()) {
			session.setParent(getFlowExecutionContext().getActiveSession());
		}
		getMockFlowExecutionContext().setActiveSession(session);
		flow.start(this, input);
	}

	public boolean handleEvent(Event event) {
		setLastEvent(event);
		return ((Flow) getActiveFlow()).handleEvent(this);
	}

	public FlowSession endActiveFlowSession(MutableAttributeMap output) throws IllegalStateException {
		MockFlowSession endingSession = getMockFlowExecutionContext().getMockActiveSession();
		endingSession.getDefinitionInternal().end(this, output);
		getMockFlowExecutionContext().setActiveSession(endingSession.getParent());
		return endingSession;
	}

	public boolean execute(Transition transition) {
		return transition.execute((TransitionableState) getCurrentState(), this);
	}

	public FlowExecutionKey assignFlowExecutionKey() {
		GeneratedFlowExecutionKey key = new GeneratedFlowExecutionKey();
		getMockFlowExecutionContext().setKey(key);
		return key;
	}

	public boolean getAlwaysRedirectOnPause() {
		return alwaysRedirectOnPause;
	}

	public boolean getFlowExecutionRedirectSent() {
		return this.flowExecutionRedirectSent;
	}

	public void setAlwaysRedirectOnPause(boolean alwaysRedirectOnPause) {
		this.alwaysRedirectOnPause = alwaysRedirectOnPause;
	}

}