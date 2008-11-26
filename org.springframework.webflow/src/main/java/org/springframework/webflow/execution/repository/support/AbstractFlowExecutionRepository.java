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
package org.springframework.webflow.execution.repository.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.JdkVersion;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.repository.BadlyFormattedFlowExecutionKeyException;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;

/**
 * Abstract base class for flow execution repository implementations. Does not make any assumptions about the storage
 * medium used to store active flow executions. Mandates the use of a {@link FlowExecutionStateRestorer}, used to
 * rehydrate a flow execution after it has been obtained from storage from resume.
 * <p>
 * The configured {@link FlowExecutionStateRestorer} should be compatible with the chosen {@link FlowExecution}
 * implementation and its {@link FlowExecutionFactory}.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowExecutionRepository implements FlowExecutionRepository, FlowExecutionKeyFactory {

	/**
	 * Logger, usable in subclasses
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	private static final Integer ONE = new Integer(1);

	private ConversationManager conversationManager;

	private boolean alwaysGenerateNewNextKey = true;

	/**
	 * Constructor for use in subclasses.
	 * @param conversationManager the conversation manager to use
	 */
	protected AbstractFlowExecutionRepository(ConversationManager conversationManager) {
		Assert.notNull(conversationManager, "The conversation manager is required");
		this.conversationManager = conversationManager;
	}

	/**
	 * The conversation service to delegate to for managing conversations initiated by this repository.
	 */
	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	/**
	 * The flag indicating if a new {@link FlowExecutionKey} should always be generated before each put call.
	 */
	public boolean getAlwaysGenerateNewNextKey() {
		return alwaysGenerateNewNextKey;
	}

	/**
	 * Sets the flag indicating if a new {@link FlowExecutionKey} should always be generated before each put call. By
	 * setting this to false a FlowExecution can remain identified by the same key throughout its life.
	 */
	public void setAlwaysGenerateNewNextKey(boolean alwaysGenerateNewNextKey) {
		this.alwaysGenerateNewNextKey = alwaysGenerateNewNextKey;
	}

	// implementing flow execution key factory

	public FlowExecutionKey getKey(FlowExecution execution) {
		if (execution.getKey() == null) {
			Conversation conversation = beginConversation(execution);
			return new CompositeFlowExecutionKey(conversation.getId(), ONE);
		} else {
			return getNextKey(execution);
		}
	}

	// implementing flow execution repository

	public FlowExecutionKey parseFlowExecutionKey(String encodedKey) throws FlowExecutionRepositoryException {
		if (!StringUtils.hasText(encodedKey)) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey,
					"The string-encoded flow execution key is required");
		}
		String[] keyParts = CompositeFlowExecutionKey.keyParts(encodedKey);
		Serializable executionId = parseExecutionId(keyParts[0], encodedKey);
		Serializable snapshotId = parseSnapshotId(keyParts[1], encodedKey);
		return new CompositeFlowExecutionKey(executionId, snapshotId);
	}

	public FlowExecutionLock getLock(FlowExecutionKey key) throws FlowExecutionRepositoryException {
		return new ConversationBackedFlowExecutionLock(getConversation(key));
	}

	// abstract repository methods to be overridden by subclasses

	public abstract FlowExecution getFlowExecution(FlowExecutionKey key) throws FlowExecutionRepositoryException;

	public abstract void putFlowExecution(FlowExecution flowExecution) throws FlowExecutionRepositoryException;

	public void removeFlowExecution(FlowExecution flowExecution) throws FlowExecutionRepositoryException {
		assertKeySet(flowExecution);
		if (logger.isDebugEnabled()) {
			logger.debug("Removing flow execution '" + flowExecution + "' from repository");
		}
		endConversation(flowExecution);
	}

	// hooks for use in subclasses

	/**
	 * Factory method that maps a new flow execution to a descriptive
	 * {@link ConversationParameters conversation parameters} object.
	 * @param flowExecution the new flow execution
	 * @return the conversation parameters object to pass to the conversation manager when the conversation is started
	 */
	protected ConversationParameters createConversationParameters(FlowExecution flowExecution) {
		FlowDefinition flow = flowExecution.getDefinition();
		return new ConversationParameters(flow.getId(), flow.getCaption(), flow.getDescription());
	}

	/**
	 * Gets the next key to assign to the flow execution.
	 * @param execution
	 * @return the next flow execution
	 */
	protected FlowExecutionKey getNextKey(FlowExecution execution) {
		if (alwaysGenerateNewNextKey) {
			CompositeFlowExecutionKey currentKey = (CompositeFlowExecutionKey) execution.getKey();
			Integer currentSnapshotId = (Integer) currentKey.getSnapshotId();
			return new CompositeFlowExecutionKey(currentKey.getExecutionId(), nextSnapshotId(currentSnapshotId));
		} else {
			return execution.getKey();
		}
	}

	/**
	 * Returns the conversation governing the execution of the {@link FlowExecution} with the provided key.
	 * @param key the flow execution key
	 * @return the governing conversation
	 * @throws NoSuchFlowExecutionException when the conversation for identified flow execution cannot be found
	 */
	protected Conversation getConversation(FlowExecutionKey key) throws NoSuchFlowExecutionException {
		try {
			ConversationId conversationId = (ConversationId) ((CompositeFlowExecutionKey) key).getExecutionId();
			return conversationManager.getConversation(conversationId);
		} catch (NoSuchConversationException e) {
			throw new NoSuchFlowExecutionException(key, e);
		}
	}

	/**
	 * Assert that a flow execution key has been assigned to the execution.
	 * @param execution the flow execution
	 * @throws IllegalStateException if a key has not yet been assigned as expected
	 */
	protected void assertKeySet(FlowExecution execution) throws IllegalStateException {
		if (execution.getKey() == null) {
			throw new IllegalStateException(
					"The key for the flow execution is null; make sure the key is assigned first.  Execution Details = "
							+ execution);
		}
	}

	// internal helpers

	private Conversation beginConversation(FlowExecution execution) {
		ConversationParameters parameters = createConversationParameters(execution);
		Conversation conversation = conversationManager.beginConversation(parameters);
		return conversation;
	}

	private Integer nextSnapshotId(Integer currentSnapshotId) {
		if (JdkVersion.isAtLeastJava15()) {
			return Integer.valueOf(currentSnapshotId.intValue() + 1);
		} else {
			return new Integer(currentSnapshotId.intValue() + 1);
		}
	}

	private ConversationId parseExecutionId(String encodedId, String encodedKey)
			throws BadlyFormattedFlowExecutionKeyException {
		try {
			return conversationManager.parseConversationId(encodedId);
		} catch (ConversationException e) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey, CompositeFlowExecutionKey.getFormat(), e);
		}
	}

	private Serializable parseSnapshotId(String encodedId, String encodedKey)
			throws BadlyFormattedFlowExecutionKeyException {
		try {
			return Integer.valueOf(encodedId);
		} catch (NumberFormatException e) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey, CompositeFlowExecutionKey.getFormat(), e);
		}
	}

	private Conversation endConversation(FlowExecution flowExecution) {
		Conversation conversation = getConversation(flowExecution.getKey());
		conversation.end();
		return conversation;
	}

}