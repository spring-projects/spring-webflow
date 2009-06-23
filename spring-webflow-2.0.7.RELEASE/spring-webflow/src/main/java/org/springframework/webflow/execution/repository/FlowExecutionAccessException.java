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
package org.springframework.webflow.execution.repository;

import org.springframework.webflow.execution.FlowExecutionKey;

/**
 * Base class for exceptions that indicate a flow execution could not be accessed within a repository.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class FlowExecutionAccessException extends FlowExecutionRepositoryException {

	/**
	 * The key of the execution that could not be accessed.
	 */
	private FlowExecutionKey flowExecutionKey;

	/**
	 * Creates a new flow execution access exception.
	 * @param flowExecutionKey the key of the execution that could not be accessed
	 * @param message a descriptive message
	 */
	public FlowExecutionAccessException(FlowExecutionKey flowExecutionKey, String message) {
		this(flowExecutionKey, message, null);
	}

	/**
	 * Creates a new flow execution access exception.
	 * @param flowExecutionKey the key of the execution that could not be accessed
	 * @param message a descriptive message
	 * @param cause the root cause of the access failure
	 */
	public FlowExecutionAccessException(FlowExecutionKey flowExecutionKey, String message, Exception cause) {
		super(message, cause);
		this.flowExecutionKey = flowExecutionKey;
	}

	/**
	 * Returns key of the flow execution that could not be accessed.
	 */
	public FlowExecutionKey getFlowExecutionKey() {
		return flowExecutionKey;
	}
}