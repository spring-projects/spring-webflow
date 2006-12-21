/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.webflow.execution;

import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;

/**
 * Mock implementation of the <code>FlowExecutionListener</code> interface for
 * use in unit tests.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class MockFlowExecutionListener extends FlowExecutionListenerAdapter {

	private boolean sessionStarting;
	
	private boolean started;

	private boolean executing;

	private boolean paused;

	private int flowNestingLevel;

	private boolean requestInProcess;

	private int requestsSubmitted;

	private int requestsProcessed;

	private int eventsSignaled;

	private boolean stateEntering;
	
	private int stateTransitions;

	private boolean sessionEnding;
	
	private int exceptionsThrown;
	
	/**
	 * Is the flow execution running: it has started but not yet ended.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Is the flow execution executing?
	 */
	public boolean isExecuting() {
		return executing;
	}

	/**
	 * Is the flow execution paused?
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Returns the nesting level of the currently active flow in the flow
	 * execution. The root flow is at level 0, a sub flow of the root flow is at
	 * level 1, and so on.
	 */
	public int getFlowNestingLevel() {
		return flowNestingLevel;
	}

	/**
	 * Checks if a request is in process. A request is in process if it was
	 * submitted but has not yet completed processing.
	 */
	public boolean isRequestInProcess() {
		return requestInProcess;
	}

	/**
	 * Returns the number of requests submitted so far.
	 */
	public int getRequestsSubmittedCount() {
		return requestsSubmitted;
	}

	/**
	 * Returns the number of requests processed so far.
	 */
	public int getRequestsProcessedCount() {
		return requestsProcessed;
	}

	/**
	 * Returns the number of events signaled so far.
	 */
	public int getEventsSignaledCount() {
		return eventsSignaled;
	}

	/**
	 * Returns the number of state transitions executed so far.
	 */
	public int getTransitionCount() {
		return stateTransitions;
	}

	/**
	 * Returns the number of exceptions thrown.
	 */
	public int getExceptionsThrown() {
		return exceptionsThrown;
	}

	public void requestSubmitted(RequestContext context) {
		Assert.state(!requestInProcess, "There is already a request being processed");
		requestsSubmitted++;
		requestInProcess = true;
	}

	public void sessionStarting(RequestContext context, FlowDefinition definition, MutableAttributeMap input) {
		if (!context.getFlowExecutionContext().isActive()) {
			Assert.state(!started, "The flow execution was already started");
			flowNestingLevel = 0;
			eventsSignaled = 0;
			stateTransitions = 0;
		}
		sessionStarting = true;
	}

	public void sessionStarted(RequestContext context, FlowSession session) {
		Assert.state(sessionStarting, "The session should've been starting...");
		sessionStarting = false;
		if (session.isRoot()) {
			Assert.state(!started, "The flow execution was already started");
			started = true;
			executing = true;
		}
		else {
			assertStarted();
			flowNestingLevel++;
		}
	}

	public void requestProcessed(RequestContext context) {
		Assert.state(requestInProcess, "There is no request being processed");
		requestsProcessed++;
		requestInProcess = false;
	}

	public void eventSignaled(RequestContext context, Event event) {
		eventsSignaled++;
	}

	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
		stateEntering = true;
	}

	public void stateEntered(RequestContext context, StateDefinition newState, StateDefinition previousState) {
		Assert.state(stateEntering, "State should've entering...");
		stateEntering = false;
		stateTransitions++;
	}

	public void paused(RequestContext context, ViewSelection selectedView) {
		executing = false;
		paused = true;
	}

	public void resumed(RequestContext context) {
		executing = true;
		paused = false;
	}

	public void sessionEnding(RequestContext context, FlowSession session, MutableAttributeMap output) {
		sessionEnding = true;
	}
	
	public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
		assertStarted();
		Assert.state(sessionEnding, "Should have been ending");
		sessionEnding = false;
		if (session.isRoot()) {
			Assert.state(flowNestingLevel == 0, "The flow execution should have ended");
			started = false;
			executing = false;
		}
		else {
			flowNestingLevel--;
			Assert.state(started, "The flow execution prematurely ended");
		}
	}

	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
		exceptionsThrown++;
	}

	/**
	 * Make sure the flow execution has already been started.
	 */
	protected void assertStarted() {
		Assert.state(started, "The flow execution has not yet been started");
	}

	/**
	 * Reset all state collected by this listener.
	 */
	public void reset() {
		started = false;
		executing = false;
		requestsSubmitted = 0;
		requestsProcessed = 0;
		exceptionsThrown = 0;
	}
}