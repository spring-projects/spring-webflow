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
package org.springframework.webflow.execution.repository.impl;

import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;
import org.springframework.webflow.execution.repository.snapshot.AbstractSnapshottingFlowExecutionRepository;
import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshot;
import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshotFactory;
import org.springframework.webflow.execution.repository.snapshot.SnapshotNotFoundException;

/**
 * The default flow execution repository implementation. Takes <i>one to {@link #getMaxSnapshots() max}</i> flow
 * execution snapshots, where each snapshot represents a copy of a {@link FlowExecution} taken at a point in time.
 * <p>
 * The set of active flow executions are managed by a {@link ConversationManager} implementation, which this repository
 * delegates to.
 * <p>
 * This repository is responsible for:
 * <ul>
 * <li>Beginning a new {@link Conversation} when a {@link FlowExecution} is assigned a persistent key. Each
 * conversation is assigned a unique conversation id which forms one part of the flow execution key.
 * <li>Taking {@link FlowExecutionSnapshot execution snapshots} to persist flow execution state. A snapshot is a copy
 * of the execution created at a point in time <i>that can be restored and continued</i>. Snapshotting supports users
 * going back in their browser to continue their flow execution from a previoius point.
 * <li>Ending conversations when flow executions end.
 * </ul>
 * <p>
 * This repository implementation also provides support for <i>execution invalidation after completion</i>, where once
 * a logical flow execution completes, it and all of its snapshots are removed. This cleans up memory and prevents the
 * possibility of duplicate submission after completion.
 * 
 * @author Keith Donald
 */
public class DefaultFlowExecutionRepository extends AbstractSnapshottingFlowExecutionRepository {

	/**
	 * The conversation attribute that stores the group of flow execution snapshots.
	 */
	private static final String SNAPSHOT_GROUP_ATTRIBUTE = "flowExecutionSnapshotGroup";

	/**
	 * The maximum number of snapshots that can be taken per execution. The default is 30, which is generally high
	 * enough not to interfere with the user experience of normal users using the back button, but low enough to avoid
	 * excessive resource usage or denial of service attacks.
	 */
	private int maxSnapshots = 30;

	/**
	 * Create a new default flow execution repository using the given state restorer, conversation manager, and snapshot
	 * factory.
	 * @param conversationManager the conversation manager to use
	 * @param snapshotFactory the flow execution snapshot factory to use
	 */
	public DefaultFlowExecutionRepository(ConversationManager conversationManager,
			FlowExecutionSnapshotFactory snapshotFactory) {
		super(conversationManager, snapshotFactory);
	}

	/**
	 * Returns the max number of snapshots allowed per flow execution by this repository.
	 */
	public int getMaxSnapshots() {
		return maxSnapshots;
	}

	/**
	 * Sets the maximum number of snapshots allowed per flow execution by this repository. Use -1 for unlimited. The
	 * default is 30.
	 */
	public void setMaxSnapshots(int maxSnapshots) {
		this.maxSnapshots = maxSnapshots;
	}

	// implementing flow execution repository

	public FlowExecution getFlowExecution(FlowExecutionKey key) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting flow execution with key '" + key + "'");
		}
		Conversation conversation = getConversation(key);
		FlowExecutionSnapshot snapshot;
		try {
			snapshot = getSnapshotGroup(conversation).getSnapshot(getSnapshotId(key));
		} catch (SnapshotNotFoundException e) {
			throw new FlowExecutionRestorationFailureException(key, e);
		}
		return restoreFlowExecution(snapshot, key, conversation);
	}

	public void putFlowExecution(FlowExecution flowExecution) {
		assertKeySet(flowExecution);
		if (logger.isDebugEnabled()) {
			logger.debug("Putting flow execution '" + flowExecution + "' into repository");
		}
		FlowExecutionKey key = flowExecution.getKey();
		Conversation conversation = getConversation(key);
		FlowExecutionSnapshotGroup snapshotGroup = getSnapshotGroup(conversation);
		FlowExecutionSnapshot snapshot = snapshot(flowExecution);
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new snapshot to group with id " + getSnapshotId(key));
		}
		snapshotGroup.addSnapshot(getSnapshotId(key), snapshot);
		putConversationScope(flowExecution, conversation);
	}

	// implementing flow execution key factory

	public void updateFlowExecutionSnapshot(FlowExecution execution) {
		FlowExecutionKey key = execution.getKey();
		Conversation conversation = getConversation(key);
		getSnapshotGroup(conversation).updateSnapshot(getSnapshotId(key), snapshot(execution));
	}

	public void removeFlowExecutionSnapshot(FlowExecution execution) {
		FlowExecutionKey key = execution.getKey();
		Conversation conversation = getConversation(key);
		getSnapshotGroup(conversation).removeSnapshot(getSnapshotId(key));
	}

	public void removeAllFlowExecutionSnapshots(FlowExecution execution) {
		Conversation conversation = getConversation(execution.getKey());
		getSnapshotGroup(conversation).removeAllSnapshots();
	}

	// hooks for subclassing

	protected FlowExecutionSnapshotGroup createFlowExecutionSnapshotGroup() {
		SimpleFlowExecutionSnapshotGroup group = new SimpleFlowExecutionSnapshotGroup();
		group.setMaxSnapshots(maxSnapshots);
		return group;
	}

	/**
	 * Returns the snapshot group associated with the governing conversation.
	 * @param conversation the conversation where the snapshot group is stored
	 * @return the snapshot group
	 */
	protected FlowExecutionSnapshotGroup getSnapshotGroup(Conversation conversation) {
		FlowExecutionSnapshotGroup group = (FlowExecutionSnapshotGroup) conversation
				.getAttribute(SNAPSHOT_GROUP_ATTRIBUTE);
		if (group == null) {
			group = createFlowExecutionSnapshotGroup();
			conversation.putAttribute(SNAPSHOT_GROUP_ATTRIBUTE, group);
		}
		return group;
	}
}