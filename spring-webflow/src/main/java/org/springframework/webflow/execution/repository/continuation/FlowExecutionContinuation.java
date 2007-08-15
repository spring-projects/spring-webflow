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

import org.springframework.webflow.execution.FlowExecution;

/**
 * A snapshot of a flow execution that can be restored from and serialized to a byte array.
 * 
 * @see FlowExecutionContinuationFactory
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public abstract class FlowExecutionContinuation implements Serializable {

	/**
	 * Restores the flow execution wrapped in this continuation.
	 * @return the unmarshalled flow execution
	 * @throws ContinuationUnmarshalException when there is a problem unmarshalling this continuation
	 */
	public abstract FlowExecution unmarshal() throws ContinuationUnmarshalException;

	/**
	 * Converts this continuation to a byte array for convenient serialization.
	 * @return this as a byte array
	 */
	public abstract byte[] toByteArray();

}