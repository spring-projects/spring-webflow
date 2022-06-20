/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * Thrown when access to a flow execution was denied by a repository.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class PermissionDeniedFlowExecutionAccessException extends FlowExecutionAccessException {

	/**
	 * Creates a new flow execution restoration exception.
	 * @param flowExecutionKey the key of the execution that could not be accessed
	 * @param cause the root cause of the access failure
	 */
	public PermissionDeniedFlowExecutionAccessException(FlowExecutionKey flowExecutionKey, Exception cause) {
		super(flowExecutionKey, "Unable to restore flow execution with key '" + flowExecutionKey
				+ "' -- permission denied.", cause);
	}
}