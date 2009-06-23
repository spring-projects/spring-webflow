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

import org.springframework.webflow.core.FlowException;

/**
 * Base class for exceptions that occur within a flow while it is executing. Can be used directly, but you are
 * encouraged to create a specific subclass for a particular use case.
 * <p>
 * Execution exceptions occur at runtime when the flow is executing requests on behalf of a client. They signal that an
 * execution problem occurred: e.g. action execution failed or no transition matched the current request context.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class FlowExecutionException extends FlowException {

	/**
	 * The id of the flow definition in which the exception occurred.
	 */
	private String flowId;

	/**
	 * The state of the flow where the exception occurred (optional).
	 */
	private String stateId;

	/**
	 * Creates a new flow execution exception.
	 * @param flowId the flow where the exception occurred
	 * @param stateId the state where the exception occurred
	 * @param message a descriptive message
	 */
	public FlowExecutionException(String flowId, String stateId, String message) {
		super(message);
		this.stateId = stateId;
		this.flowId = flowId;
	}

	/**
	 * Creates a new flow execution exception.
	 * @param flowId the flow where the exception occured
	 * @param stateId the state where the exception occured
	 * @param message a descriptive message
	 * @param cause the root cause
	 */
	public FlowExecutionException(String flowId, String stateId, String message, Throwable cause) {
		super(message, cause);
		this.stateId = stateId;
		this.flowId = flowId;
	}

	/**
	 * Returns the id of the flow definition that was executing when this exception occured.
	 */
	public String getFlowId() {
		return flowId;
	}

	/**
	 * Returns the id of the state definition where the exception occured. Could be null if no state was active at the
	 * time when the exception was thrown.
	 */
	public String getStateId() {
		return stateId;
	}
}