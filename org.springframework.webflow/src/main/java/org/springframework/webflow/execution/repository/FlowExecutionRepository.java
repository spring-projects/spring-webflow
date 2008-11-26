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

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;

/**
 * Central subsystem interface responsible for the saving and restoring of flow executions, where each flow execution
 * represents a state of an active flow definition.
 * <p>
 * Flow execution repositories are responsible for managing the storage, restoration and removal of flow executions
 * launched by clients of the Spring Web Flow system.
 * <p>
 * When placed in a repository a {@link FlowExecution} object representing the state of a flow at a point in time is
 * indexed under a unique {@link FlowExecutionKey}.
 * 
 * @see FlowExecution
 * @see FlowExecutionKey
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public interface FlowExecutionRepository {

	/**
	 * Parse the string-encoded flow execution key into its object form. Essentially, the reverse of
	 * {@link FlowExecutionKey#toString()}.
	 * @param encodedKey the string encoded key
	 * @return the parsed flow execution key, the persistent identifier for exactly one flow execution
	 */
	public FlowExecutionKey parseFlowExecutionKey(String encodedKey) throws FlowExecutionRepositoryException;

	/**
	 * Return the lock for the flow execution, allowing for the lock to be acquired or released. Caution: care should be
	 * made not to allow for a deadlock situation. If you acquire a lock make sure you release it when you are done. The
	 * general pattern for safely doing work against a locked conversation follows:
	 * 
	 * <pre>
	 * FlowExecutionLock lock = repository.getLock(key);
	 * lock.lock();
	 * try {
	 * 	FlowExecution execution = repository.getFlowExecution(key);
	 * 	// do work
	 * } finally {
	 * 	lock.unlock();
	 * }
	 * </pre>
	 * 
	 * @param key the identifier of the flow execution to lock
	 * @return the lock
	 * @throws FlowExecutionRepositoryException a problem occurred accessing the lock object
	 */
	public FlowExecutionLock getLock(FlowExecutionKey key) throws FlowExecutionRepositoryException;

	/**
	 * Return the <code>FlowExecution</code> indexed by the provided key. The returned flow execution represents the
	 * restored state of an executing flow from a point in time. This should be called to resume a persistent flow
	 * execution. Before calling this method, you should acquire the lock for the keyed flow execution.
	 * @param key the flow execution key
	 * @return the flow execution, fully hydrated and ready to resume
	 * @throws FlowExecutionRepositoryException if no flow execution was indexed with the key provided
	 */
	public FlowExecution getFlowExecution(FlowExecutionKey key) throws FlowExecutionRepositoryException;

	/**
	 * Place the <code>FlowExecution</code> in this repository under the provided key. This should be called to save
	 * or update the persistent state of an active (but paused) flow execution. Before calling this method, you should
	 * acquire the lock for the keyed flow execution.
	 * @param flowExecution the flow execution
	 * @throws FlowExecutionRepositoryException the flow execution could not be stored
	 */
	public void putFlowExecution(FlowExecution flowExecution) throws FlowExecutionRepositoryException;

	/**
	 * Remove the flow execution from the repository. This should be called when the flow execution ends (is no longer
	 * active). Before calling this method, you should acquire the lock for the keyed flow execution.
	 * @param flowExecution the flow execution
	 * @throws FlowExecutionRepositoryException the flow execution could not be removed.
	 */
	public void removeFlowExecution(FlowExecution flowExecution) throws FlowExecutionRepositoryException;

}