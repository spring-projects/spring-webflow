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
package org.springframework.webflow.execution.repository.snapshot;

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;

/**
 * Thrown when a FlowExecutionContinuation could not be deserialized into a FlowExecution.
 * 
 * @see FlowExecutionSnapshot
 * @see FlowExecution
 * 
 * @author Keith Donald
 */
public class SnapshotUnmarshalException extends FlowExecutionRepositoryException {

	/**
	 * Creates a new flow execution unmarshalling exception.
	 * @param message the exception message
	 * @param cause the cause
	 */
	public SnapshotUnmarshalException(String message, Throwable cause) {
		super(message, cause);
	}
}