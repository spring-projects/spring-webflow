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
	public FlowExecutionKey getKey(FlowExecution execution);

	/**
	 * Update the snapshot indexed by key of the provided execution to capture the execution's current state.
	 * @param execution the flow execution
	 */
	public void updateFlowExecutionSnapshot(FlowExecution execution);

	/**
	 * Remove the snapshot indexed by the key of the provided flow execution from storage.
	 * @param execution the flow execution
	 */
	public void removeFlowExecutionSnapshot(FlowExecution execution);

	/**
	 * Remove all snapshots associated with the flow execution from storage.
	 * @param execution the flow execution
	 */
	public void removeAllFlowExecutionSnapshots(FlowExecution execution);

}
