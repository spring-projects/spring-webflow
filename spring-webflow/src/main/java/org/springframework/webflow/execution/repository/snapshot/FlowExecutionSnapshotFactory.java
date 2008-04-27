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
	 * Restore a flow execution snapshot from a byte array.
	 * @param bytes the byte array
	 * @return the snapshot
	 * @throws SnapshotUnmarshalException if the snapshot could not be restored
	 */
	public FlowExecutionSnapshot restoreSnapshot(byte[] bytes) throws SnapshotUnmarshalException;
}