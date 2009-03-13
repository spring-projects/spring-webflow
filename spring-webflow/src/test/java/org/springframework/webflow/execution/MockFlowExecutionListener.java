/*
 * Copyright 2004-2008 the original author or authors.
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
import org.springframework.webflow.definition.TransitionDefinition;

/**
 * Mock implementation of the <code>FlowExecutionListener</code> interface for use in unit tests.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public class MockFlowExecutionListener extends FlowExecutionListenerAdapter {

	private boolean sessionStarting;

	private int sessionCreatingCount;

	private int sessionStartingCount;

	private int sessionStartedCount;

	private boolean started;

	private boolean executing;

	private int stateEnteringCount;

	private int stateEnteredCount;

	private int transitionExecutingCount;

	private int resumingCount;

	private boolean paused;

	private int pausedCount;

	private int flowNestingLevel;

	private boolean requestInProcess;

	private int requestsSubmittedCount;

	private int requestsProcessedCount;

	private int eventSignaledCount;

	private boolean stateEntering;

	private boolean sessionEnding;

	private int sessionEndingCount;

	private int sessionEndedCount;

	private int exceptionThrownCount;

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
	 * Returns the nesting level of the currently active flow in the flow execution. The root flow is at level 0, a sub
	 * flow of the root flow is at level 1, and so on.
	 */
	public int getFlowNestingLevel() {
		return flowNestingLevel;
	}

	/**
	 * Checks if a request is in process. A request is in process if it was submitted but has not yet completed
	 * processing.
	 */
	public boolean isRequestInProcess() {
		return requestInProcess;
	}

	/**
	 * Returns the number of requests submitted so far.
	 */
	public int getRequestsSubmittedCount() {
		return requestsSubmittedCount;
	}

	/**
	 * Returns the number of requests processed so far.
	 */
	public int getRequestsProcessedCount() {
		return requestsProcessedCount;
	}

	/**
	 * Returns the number of sessions that have attempted to be created so far.
	 */
	public int getSessionCreatingCount() {
		return sessionCreatingCount;
	}

	/**
	 * Returns the number of sessions that have attempted to start so far.
	 */
	public int getSessionStartingCount() {
		return sessionStartingCount;
	}

	/**
	 * Returns the number of sessions that started so far.
	 */
	public int getSessionStartedCount() {
		return sessionStartedCount;
	}

	/**
	 * Returns the number of state entries attempted so far.
	 */
	public int getStateEnteringCount() {
		return stateEnteringCount;
	}

	/**
	 * Returns the number of states entered so far.
	 */
	public int getStateEnteredCount() {
		return stateEnteredCount;
	}

	/**
	 * Returns the number of transitions entered so far.
	 */
	public int getTransitionExecutingCount() {
		return transitionExecutingCount;
	}

	/**
	 * Returns the number of events signaled so far.
	 */
	public int getEventSignaledCount() {
		return eventSignaledCount;
	}

	/**
	 * Returns the number of times the flow execution has paused.
	 */
	public int getPausedCount() {
		return pausedCount;
	}

	/**
	 * Returns the number of times the flow execution has resumed.
	 */
	public int getResumingCount() {
		return resumingCount;
	}

	/**
	 * Returns the number of sessions that have attempted to end so far.
	 */
	public int getSessionEndingCount() {
		return sessionEndingCount;
	}

	/**
	 * Returns the number of sessions that end so far.
	 */
	public int getSessionEndedCount() {
		return sessionEndedCount;
	}

	/**
	 * Returns the number of exceptions thrown.
	 */
	public int getExceptionThrownCount() {
		return exceptionThrownCount;
	}

	public void requestSubmitted(RequestContext context) {
		Assert.state(!requestInProcess, "There is already a request being processed");
		requestsSubmittedCount++;
		requestInProcess = true;
	}

	public void sessionCreating(RequestContext context, FlowDefinition definition) {
		if (!context.getFlowExecutionContext().isActive()) {
			Assert.state(!started, "The flow execution was already started");
			started = true;
		}
		sessionCreatingCount++;
	}

	public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input) {
		sessionStartingCount++;
		sessionStarting = true;
		flowNestingLevel++;
	}

	public void sessionStarted(RequestContext context, FlowSession session) {
		Assert.state(sessionStarting, "The session should've been starting...");
		sessionStarting = false;
		sessionStartedCount++;
	}

	public void requestProcessed(RequestContext context) {
		Assert.state(requestInProcess, "There is no request being processed");
		requestsProcessedCount++;
		requestInProcess = false;
	}

	public void eventSignaled(RequestContext context, Event event) {
		eventSignaledCount++;
	}

	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
		stateEntering = true;
		stateEnteringCount++;
	}

	public void stateEntered(RequestContext context, StateDefinition newState, StateDefinition previousState) {
		Assert.state(stateEntering, "State should've entering...");
		stateEntering = false;
		stateEnteredCount++;
	}

	public void transitionExecuting(RequestContext context, TransitionDefinition transition) {
		transitionExecutingCount++;
	}

	public void paused(RequestContext context) {
		executing = false;
		paused = true;
		pausedCount++;
	}

	public void resuming(RequestContext context) {
		executing = true;
		paused = false;
		resumingCount++;
	}

	public void sessionEnding(RequestContext context, FlowSession session, String outcome, MutableAttributeMap output) {
		sessionEnding = true;
		sessionEndingCount++;
		flowNestingLevel--;
	}

	public void sessionEnded(RequestContext context, FlowSession session, String outcome, AttributeMap output) {
		assertStarted();
		Assert.state(sessionEnding, "Should have been ending");
		sessionEnding = false;
		sessionEndedCount++;
		if (session.isRoot()) {
			Assert.state(flowNestingLevel == 0, "The flow execution should have ended");
			started = false;
			executing = false;
		} else {
			Assert.state(started, "The flow execution prematurely ended");
		}
	}

	public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
		exceptionThrownCount++;
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
		requestsSubmittedCount = 0;
		requestsProcessedCount = 0;
		sessionCreatingCount = 0;
		sessionStartingCount = 0;
		sessionStartedCount = 0;
		stateEnteringCount = 0;
		stateEnteredCount = 0;
		transitionExecutingCount = 0;
		eventSignaledCount = 0;
		pausedCount = 0;
		resumingCount = 0;
		sessionEndingCount = 0;
		sessionEndedCount = 0;
		exceptionThrownCount = 0;
		flowNestingLevel = 0;
	}
}