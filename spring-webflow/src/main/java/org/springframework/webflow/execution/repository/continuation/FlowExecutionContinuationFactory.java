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

import org.springframework.webflow.execution.FlowExecution;

/**
 * A factory for creating different {@link FlowExecutionContinuation} implementations.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowExecutionContinuationFactory {

	/**
	 * Creates a new flow execution continuation for given flow execution.
	 * @param flowExecution the flow execution
	 * @return the continuation
	 * @throws ContinuationCreationException when the continuation cannot be created
	 */
	public FlowExecutionContinuation createContinuation(FlowExecution flowExecution)
			throws ContinuationCreationException;

	/**
	 * Restore a flow execution continuation object from the provided byte array.
	 * @param bytes the flow execution byte array
	 * @return the continuation
	 * @throws ContinuationUnmarshalException when the continuation cannot be restored
	 */
	public FlowExecutionContinuation restoreContinuation(byte[] bytes) throws ContinuationUnmarshalException;
}