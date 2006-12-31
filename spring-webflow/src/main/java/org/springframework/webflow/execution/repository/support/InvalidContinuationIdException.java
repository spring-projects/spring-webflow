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
package org.springframework.webflow.execution.repository.support;

import java.io.Serializable;

import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;

/**
 * Thrown when no flow execution continuation exists with the provided id.
 * This might occur if the continuation has expired or was explictly invalidated
 * but a client's browser page cache still references it.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class InvalidContinuationIdException extends FlowExecutionRepositoryException {

	/**
	 * The unique continuation identifier that was invalid.
	 */
	private Serializable continuationId;

	/**
	 * Creates an invalid continuation id exception.
	 * @param continuationId the invalid continuation id
	 */
	public InvalidContinuationIdException(Serializable continuationId) {
		super("The continuation id '" + continuationId + "' is invalid.  Access to flow execution denied.");
	}

	/**
	 * Returns the continuation id.
	 */
	public Serializable getContinuationId() {
		return continuationId;
	}
}