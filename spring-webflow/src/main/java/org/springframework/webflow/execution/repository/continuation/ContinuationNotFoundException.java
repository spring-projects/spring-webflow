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
package org.springframework.webflow.execution.repository.continuation;

import java.io.Serializable;

import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;

/**
 * Thrown when no flow execution continuation exists within a continuation group with a particular id. This might occur
 * if the continuation has expired or was explictly invalidated but a client's browser page cache still references it.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ContinuationNotFoundException extends FlowExecutionRepositoryException {

	/**
	 * The unique continuation identifier that was not found.
	 */
	private Serializable continuationId;

	/**
	 * Creates a continuation not found exception.
	 * @param continuationId the continuation id that could not be found
	 */
	public ContinuationNotFoundException(Serializable continuationId) {
		super("No flow execution continuation could be found in this group with id '" + continuationId
				+ "' -- perhaps the continuation has expired or has been invalidated? ");
		this.continuationId = continuationId;
	}

	/**
	 * Returns the continuation id that could not be found.
	 */
	public Serializable getContinuationId() {
		return continuationId;
	}
}