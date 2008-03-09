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
package org.springframework.webflow.execution.repository.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
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
 * implementation and is configuration as done by a {@link FlowExecutionFactory} (listeners, execution attributes, ...).
 * 
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowExecutionRepository implements FlowExecutionRepository, FlowExecutionKeyFactory {

	/**
	 * Logger, usable in subclasses
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * The conversation service to delegate to for managing conversations initiated by this repository.
	 */
	private ConversationManager conversationManager;

	/**
	 * The flow execution state restorer for restoring transient execution state.
	 */
	private FlowExecutionStateRestorer executionStateRestorer;

	/**
	 * Flag to indicate whether or not a new flow execution key should always be generated before each put call. Default
	 * is true.
	 */
	private boolean alwaysGenerateNewNextKey = true;

	/**
	 * Constructor for use in subclasses.
	 * @param conversationManager the conversation manager to use
	 */
	protected AbstractFlowExecutionRepository(ConversationManager conversationManager,
			FlowExecutionStateRestorer executionStateRestorer) {
		Assert.notNull(conversationManager, "The conversation manager is required");
		Assert.notNull(executionStateRestorer, "The execution state restorer is required");
		this.conversationManager = conversationManager;
		this.executionStateRestorer = executionStateRestorer;
	}

	/**
	 * Sets a flag indicating if a new {@link FlowExecutionKey} should always be generated before each put call. By
	 * setting this to false a FlowExecution can remain identified by the same key throughout its life.
	 */
	public void setAlwaysGenerateNewNextKey(boolean alwaysGenerateNewNextKey) {
		this.alwaysGenerateNewNextKey = alwaysGenerateNewNextKey;
	}

	public FlowExecutionKey getKey(FlowExecution execution) {
		if (execution.getKey() == null) {
			Conversation conversation = beginConversation(execution);
			return new CompositeFlowExecutionKey(conversation.getId(), Integer.valueOf(1));
		} else {
			return getNextKey(execution);
		}
	}

	public FlowExecutionKey parseFlowExecutionKey(String encodedKey) throws FlowExecutionRepositoryException {
		if (!StringUtils.hasText(encodedKey)) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey,
					"The string-encoded flow execution key is required");
		}
		String[] keyParts = CompositeFlowExecutionKey.keyParts(encodedKey);
		ConversationId conversationId;
		try {
			conversationId = conversationManager.parseConversationId(keyParts[0]);
		} catch (ConversationException e) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey, CompositeFlowExecutionKey.getFormat(), e);
		}
		Serializable continuationId = parseContinuationId(keyParts[1], encodedKey);
		return new CompositeFlowExecutionKey(conversationId, continuationId);
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
		return new ConversationParameters(flow.getId().toString(), flow.getCaption(), flow.getDescription());
	}

	/**
	 * Gets the next key to assign to the flow execution.
	 * @param execution
	 * @return the next flow execution
	 */
	protected FlowExecutionKey getNextKey(FlowExecution execution) {
		if (alwaysGenerateNewNextKey) {
			CompositeFlowExecutionKey key = (CompositeFlowExecutionKey) execution.getKey();
			Integer continuationId = (Integer) key.getContinuationId();
			Integer nextId = Integer.valueOf(continuationId.intValue() + 1);
			return new CompositeFlowExecutionKey(key.getConversationId(), nextId);
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
			return conversationManager.getConversation(getConversationId(key));
		} catch (NoSuchConversationException e) {
			throw new NoSuchFlowExecutionException(key, e);
		}
	}

	/**
	 * Returns the conversationId portion of the flow execution key.
	 * @param key the execution key
	 */
	protected ConversationId getConversationId(FlowExecutionKey key) {
		return ((CompositeFlowExecutionKey) key).getConversationId();
	}

	/**
	 * Returns the continuationId portion of the flow execution key.
	 * @param key the execution key
	 */
	protected Serializable getContinuationId(FlowExecutionKey key) {
		return ((CompositeFlowExecutionKey) key).getContinuationId();
	}

	/**
	 * Returns the transient state of the flow execution after potential deserialization.
	 * @param execution the flow execution
	 * @param key the flow execution key
	 */
	protected FlowExecution restoreTransientState(FlowExecution execution, FlowExecutionKey key) {
		return executionStateRestorer.restoreState(execution, key, getConversationScope(key), this);
	}

	/**
	 * Puts the value of conversation scope in the conversation object.
	 * @param flowExecution the flow execution holding a reference to conversation scope
	 */
	protected void putConversationScope(FlowExecution flowExecution) {
		getConversation(flowExecution.getKey()).putAttribute("scope", flowExecution.getConversationScope());
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

	private Serializable parseContinuationId(String encodedId, String encodedKey) {
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

	private MutableAttributeMap getConversationScope(FlowExecutionKey key) {
		return (MutableAttributeMap) getConversation(key).getAttribute("scope");
	}

}