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

import org.springframework.webflow.core.FlowException;

/**
 * The root of the {@link FlowExecutionRepository} exception hierarchy. Indicates a problem occured either saving,
 * restoring, or invalidating a managed flow execution.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public abstract class FlowExecutionRepositoryException extends FlowException {

	/**
	 * Creates a new flow execution repository exception.
	 * @param message a descriptive message
	 */
	public FlowExecutionRepositoryException(String message) {
		super(message);
	}

	/**
	 * Creates a new flow execution repository exception.
	 * @param message a descriptive message
	 * @param cause the root cause of the problem
	 */
	public FlowExecutionRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}
}