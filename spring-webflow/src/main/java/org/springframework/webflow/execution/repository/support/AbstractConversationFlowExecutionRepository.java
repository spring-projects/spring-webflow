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
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.BadlyFormattedFlowExecutionKeyException;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepositoryException;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;

/**
 * A convenient base class for flow execution repository implementations that delegate
 * to a conversation service for managing conversations that govern the
 * persistent state of paused flow executions.
 * 
 * @see ConversationManager
 * 
 * @author Keith Donald
 */
public abstract class AbstractConversationFlowExecutionRepository extends AbstractFlowExecutionRepository {
	
	/**
	 * Logger, usable in subclasses
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * The conversation attribute holding conversation scope ("scope").
	 */
	private static final String SCOPE_ATTRIBUTE = "scope";

	/**
	 * The conversation service to delegate to for managing conversations
	 * initiated by this repository.
	 */
	private ConversationManager conversationManager;

	/**
	 * Constructor for use in subclasses.
	 * @param executionStateRestorer the transient flow execution state restorer
	 * @param conversationManager the conversation manager to use
	 */
	protected AbstractConversationFlowExecutionRepository(FlowExecutionStateRestorer executionStateRestorer,
			ConversationManager conversationManager) {
		super(executionStateRestorer);
		setConversationManager(conversationManager);
	}

	/**
	 * Returns the configured conversation manager.
	 */
	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	/**
	 * Sets the conversation manager to use.
	 * @param conversationManager the conversation service, may not be null
	 */
	private void setConversationManager(ConversationManager conversationManager) {
		Assert.notNull(conversationManager, "The conversation manager is required");
		this.conversationManager = conversationManager;
	}
		
	public FlowExecutionKey generateKey(FlowExecution flowExecution) {
		// we need to generate a key for a new flow execution, so a new conversation has
		// started
		ConversationParameters parameters = createConversationParameters(flowExecution);
		Conversation conversation = conversationManager.beginConversation(parameters);
		onBegin(conversation);
		FlowExecutionKey key =
			new CompositeFlowExecutionKey(conversation.getId(), generateContinuationId(flowExecution));
		if (logger.isDebugEnabled()) {
			logger.debug("Generated new key for flow execution '" + flowExecution + "': '" + key + "'");
		}
		return key;
	}

	public FlowExecutionKey getNextKey(FlowExecution flowExecution, FlowExecutionKey previousKey) {
		CompositeFlowExecutionKey key = (CompositeFlowExecutionKey)previousKey;
		// the conversation id remains the same for the life of the flow execution
		// but the continuation id changes
		FlowExecutionKey nextKey =
			new CompositeFlowExecutionKey(key.getConversationId(), generateContinuationId(flowExecution));
		if (logger.isDebugEnabled()) {
			logger.debug("Generated next key for flow execution '" + flowExecution + "': '" + nextKey + "'; " +
					"previous key was '" + key + "'");
		}
		return nextKey;
	}

	public FlowExecutionLock getLock(FlowExecutionKey key) throws FlowExecutionRepositoryException {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting lock for flow execution with key '" + key + "'");
		}
		// lock the entire conversation
		return new ConversationBackedFlowExecutionLock(getConversation(key));
	}

	public abstract FlowExecution getFlowExecution(FlowExecutionKey key) throws FlowExecutionRepositoryException;

	public abstract void putFlowExecution(FlowExecutionKey key, FlowExecution flowExecution)
			throws FlowExecutionRepositoryException;

	public void removeFlowExecution(FlowExecutionKey key) throws FlowExecutionRepositoryException {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing flow execution with key '" + key + "' from repository");
		}
		
		// end the governing conversation
		Conversation conversation = getConversation(key);
		conversation.end();
		onEnd(conversation);
	}

	public FlowExecutionKey parseFlowExecutionKey(String encodedKey) throws FlowExecutionRepositoryException {
		Assert.hasText(encodedKey, "The string encoded flow execution key is required");
		
		String[] keyParts = CompositeFlowExecutionKey.keyParts(encodedKey);
		
		// parse out the conversation id
		ConversationId conversationId;
		try {
			conversationId = conversationManager.parseConversationId(keyParts[0]);
		}
		catch (ConversationException e) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey,
					"The conversation id '" + keyParts[0] + "' contained in the composite flow execution key '"
					+ encodedKey + "' is invalid", e);
		}
		
		// parse out the continuation id
		Serializable continuationId;
		try {
			continuationId = parseContinuationId(keyParts[1]);
		}
		catch (FlowExecutionRepositoryException e) {
			throw new BadlyFormattedFlowExecutionKeyException(encodedKey,
					"The continuation id '" + keyParts[1] + "' contained in the composite flow execution key '"
					+ encodedKey + "' is invalid", e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Parsed encoded flow execution key '" + encodedKey + "', extracted conversation id '"
					+ conversationId + "' and continuation id '" + continuationId + "'");
		}
		
		return new CompositeFlowExecutionKey(conversationId, continuationId);
	}

	// overridable hooks for use in subclasses

	/**
	 * Factory method that maps a new flow execution to a descriptive
	 * {@link ConversationParameters conversation parameters} object.
	 * @param flowExecution the new flow execution
	 * @return the conversation parameters object to pass to the conversation
	 * manager when the conversation is started
	 */
	protected ConversationParameters createConversationParameters(FlowExecution flowExecution) {
		FlowDefinition flow = flowExecution.getDefinition();
		return new ConversationParameters(flow.getId(), flow.getCaption(), flow.getDescription());
	}

	/**
	 * An "on begin conversation" callback, allowing for insertion of custom
	 * logic after a new conversation has begun.
	 * This implementation is emtpy.
	 * @param conversation the conversation that has begun
	 */
	protected void onBegin(Conversation conversation) {
	}
	
	/**
	 * An "on conversation end" callback, allowing for insertion of custom logic
	 * after a conversation has ended (it's {@link Conversation#end()} method has been
	 * called).
	 * This implementation is empty.
	 * @param conversation the conversation that has ended
	 */
	protected void onEnd(Conversation conversation) {
	}

	/**
	 * Returns the conversation id part of given composite flow execution key.
	 * @param key the composite key
	 * @return the conversationId key part
	 */
	protected ConversationId getConversationId(FlowExecutionKey key) {
		return ((CompositeFlowExecutionKey)key).getConversationId();
	}

	/**
	 * Returns the continuation id part of given composite flow execution key.
	 * @param key the composite key
	 * @return the continuation id key part
	 */
	protected Serializable getContinuationId(FlowExecutionKey key) {
		return ((CompositeFlowExecutionKey)key).getContinuationId();
	}

	/**
	 * Returns the conversation governing the execution of the
	 * {@link FlowExecution} with the provided key.
	 * @param key the flow execution key
	 * @return the governing conversation
	 * @throws NoSuchFlowExecutionException when the conversation for identified
	 * flow execution cannot be found
	 */
	protected Conversation getConversation(FlowExecutionKey key) throws NoSuchFlowExecutionException {
		try {
			return getConversationManager().getConversation(getConversationId(key));
		}
		catch (NoSuchConversationException e) {
			throw new NoSuchFlowExecutionException(key, e);
		}
	}

	/**
	 * Returns the "conversation scope" for the flow execution with the
	 * key provided. This is mainly useful for reinitialisation of a flow execution
	 * after restoration from the repository.
	 * @param key the flow execution key
	 * @return the execution's conversation scope
	 */
	protected MutableAttributeMap getConversationScope(FlowExecutionKey key) {
		return (MutableAttributeMap)getConversation(key).getAttribute(SCOPE_ATTRIBUTE);
	}

	/**
	 * Sets the conversation scope attribute for the flow execution with the key
	 * provided.
	 * @param key the flow execution key
	 * @param scope the execution's conversation scope
	 */
	protected void putConversationScope(FlowExecutionKey key, MutableAttributeMap scope) {
		Assert.notNull(scope, "The conversation scope attribute map is required");
		getConversation(key).putAttribute(SCOPE_ATTRIBUTE, scope);
	}

	// abstract template methods

	/**
	 * Template method used to generate a new continuation id for given flow
	 * execution. Subclasses must override.
	 * @param flowExecution the flow execution
	 * @return the continuation id
	 */
	protected abstract Serializable generateContinuationId(FlowExecution flowExecution);

	/**
	 * Template method to parse the continuation id from the encoded string.
	 * @param encodedId the string identifier
	 * @return the parsed continuation id
	 */
	protected abstract Serializable parseContinuationId(String encodedId) throws FlowExecutionRepositoryException;

}