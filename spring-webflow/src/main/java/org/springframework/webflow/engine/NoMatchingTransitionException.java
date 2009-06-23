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

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * Thrown when no transition can be matched given the occurence of an event in the context of a flow execution request.
 * <p>
 * Typically this happens because there is no "handler" transition for the last event that occured.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class NoMatchingTransitionException extends FlowExecutionException {

	/**
	 * The event that occurred that could not be matched to a Transition.
	 */
	private Event event;

	/**
	 * Create a new no matching transition exception.
	 * @param flowId the current flow
	 * @param stateId the state that could not be transitioned out of
	 * @param event the event that occured that could not be matched to a transition
	 * @param message the message
	 */
	public NoMatchingTransitionException(String flowId, String stateId, Event event, String message) {
		super(flowId, stateId, message);
		this.event = event;
	}

	/**
	 * Create a new no matching transition exception.
	 * @param flowId the current flow
	 * @param stateId the state that could not be transitioned out of
	 * @param event the event that occured that could not be matched to a transition
	 * @param message the message
	 * @param cause the underlying cause
	 */
	public NoMatchingTransitionException(String flowId, String stateId, Event event, String message, Throwable cause) {
		super(flowId, stateId, message, cause);
		this.event = event;
	}

	/**
	 * Returns the event for the current request that did not trigger any supported transition.
	 */
	public Event getEvent() {
		return event;
	}
}