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

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;

/**
 * A factory for creating different {@link FlowExecutionSnapshot} implementations.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowExecutionSnapshotFactory {

	/**
	 * Takes a snapshot of the flow execution.
	 * @param flowExecution the flow execution
	 * @return the new snapshot
	 * @throws SnapshotCreationException if the snapshot could not be created
	 */
	public FlowExecutionSnapshot createSnapshot(FlowExecution flowExecution) throws SnapshotCreationException;

	/**
	 * Restores a flow execution from a previously taken snapshot.
	 * @param snapshot the previously taken snapshot
	 * @param flowId the id of the root flow definition
	 * @param key the flow execution key
	 * @param conversationScope conversation scope
	 * @param keyFactory factory for creating new snapshot keys
	 * @return the restored flow execution
	 * @throws FlowExecutionRestorationFailureException if flow execution restoration fails
	 */
	public FlowExecution restoreExecution(FlowExecutionSnapshot snapshot, String flowId, FlowExecutionKey key,
			MutableAttributeMap conversationScope, FlowExecutionKeyFactory keyFactory)
			throws FlowExecutionRestorationFailureException;
}