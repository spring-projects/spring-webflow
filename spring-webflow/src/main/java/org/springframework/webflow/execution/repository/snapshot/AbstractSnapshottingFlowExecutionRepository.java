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

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.support.AbstractFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.CompositeFlowExecutionKey;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;

/**
 * Base class for repositories that take flow execution snapshots using a {@link FlowExecutionSnapshotFactory}.
 * 
 * @author Keith Donald
 */
public abstract class AbstractSnapshottingFlowExecutionRepository extends AbstractFlowExecutionRepository {

	/**
	 * The factory to use to take flow execution snapshots.
	 */
	private FlowExecutionSnapshotFactory executionSnapshotFactory;

	/**
	 * Creates a new snapshotting flow execution repository.
	 * @param conversationManager the conversation manager
	 * @param executionStateRestorer the execution state restorer
	 * @param executionSnapshotFactory the execution snapshot factory
	 */
	public AbstractSnapshottingFlowExecutionRepository(ConversationManager conversationManager,
			FlowExecutionStateRestorer executionStateRestorer, FlowExecutionSnapshotFactory executionSnapshotFactory) {
		super(conversationManager, executionStateRestorer);
		Assert.notNull(executionSnapshotFactory, "The flow execution snapshot factory is required");
		this.executionSnapshotFactory = executionSnapshotFactory;
	}

	/**
	 * Returns the configured flow execution snapshot factory.
	 * @return the snapshot factory
	 */
	public FlowExecutionSnapshotFactory getExecutionSnapshotFactory() {
		return executionSnapshotFactory;
	}

	/**
	 * Returns the snapshotId portion of the flow execution key.
	 * @param key the execution key
	 */
	protected Serializable getSnapshotId(FlowExecutionKey key) {
		return ((CompositeFlowExecutionKey) key).getSnapshotId();
	}

	/**
	 * Take a new flow execution snapshot.
	 * @param flowExecution the execution to snapshot
	 * @return the snapshot
	 */
	protected FlowExecutionSnapshot snapshot(FlowExecution flowExecution) {
		return executionSnapshotFactory.createSnapshot(flowExecution);
	}

	/**
	 * Deserialize a serialized flow execution.
	 * @param snapshotBytes the flow execution snapshot byte array
	 * @return the deserialized flow execution
	 */
	protected FlowExecution deserializeExecution(byte[] snapshotBytes) {
		return executionSnapshotFactory.restoreSnapshot(snapshotBytes).unmarshal();
	}
}