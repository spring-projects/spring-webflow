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
package org.springframework.webflow.execution;

/**
 * A factory for creating flow execution keys. Used to generate a persistent identity for a flow execution that needs to
 * be persisted.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionKeyFactory {

	/**
	 * Get the key to assign to the flow execution. This factory simply generates the key to assign, it does not
	 * actually perform the key assignment.
	 * @param execution the flow execution
	 * @return the key to assign to the flow execution
	 */
	FlowExecutionKey getKey(FlowExecution execution);

	/**
	 * Capture the current state of the flow execution by updating its snapshot in storage. Does nothing if the no key
	 * has been assigned or no snapshot has already been taken.
	 * @param execution the flow execution
	 */
	void updateFlowExecutionSnapshot(FlowExecution execution);

	/**
	 * Remove the snapshot that was used to restore this flow execution, discarding it for future use. Does nothing if
	 * the no key been assigned or no snapshot has been taken.
	 * @param execution the flow execution
	 */
	void removeFlowExecutionSnapshot(FlowExecution execution);

	/**
	 * Remove all snapshots associated with the flow execution from storage, invalidating all history. Does nothing if
	 * no key has been assigned or no snapshots have been taken.
	 * @param execution the flow execution
	 */
	void removeAllFlowExecutionSnapshots(FlowExecution execution);

}
