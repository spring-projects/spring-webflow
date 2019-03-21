/*
 * Copyright 2004-2015 the original author or authors.
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
package org.springframework.webflow.conversation.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;

/**
 * Container for conversations that is stored in the session. When the session
 * expires this container will go with it, implicitly expiring all contained
 * conversations.
 *
 * <p>This is an internal helper class of the
 * {@link SessionBindingConversationManager}.
 *
 * @author Erwin Vervaet
 * @author Rossen Stoyanchev
 */
public class ConversationContainer implements Serializable {

	private static final Log logger = LogFactory.getLog(ConversationContainer.class);

	/** Maximum number of conversations in this container. -1 for unlimited. */
	private int maxConversations;

	/** The key of this conversation container in the session. */
	private String sessionKey;

	/** The contained conversations. A list of {@link ContainedConversation} objects. */
	private List<ContainedConversation> conversations;

	/** The sequence for unique conversation identifiers within this container. */
	private int conversationIdSequence;


	/**
	 * Create a new conversation container.
	 * @param maxConversations the max number of allowed concurrent conversations, -1 for unlimited
	 * @param sessionKey the key of this conversation container in the session
	 */
	public ConversationContainer(int maxConversations, String sessionKey) {
		this.maxConversations = maxConversations;
		this.sessionKey = sessionKey;
		this.conversations = new CopyOnWriteArrayList<>();
	}

	/**
	 * Return the key of this conversation container in the session.
	 * For package level use only.
	 */
	String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Return the current size of the conversation container:
	 * the number of conversations contained within it.
	 */
	public int size() {
		return conversations.size();
	}

	/**
	 * Create a new conversation based on given parameters and add it to the container.
	 * @param parameters descriptive conversation parameters
	 * @param lock the conversation lock
	 * @return the created conversation
	 */
	public synchronized Conversation createConversation(ConversationParameters parameters, ConversationLock lock) {
		ContainedConversation conversation = createContainedConversation(nextId(), lock);
		conversation.putAttribute("name", parameters.getName());
		conversation.putAttribute("caption", parameters.getCaption());
		conversation.putAttribute("description", parameters.getDescription());
		conversations.add(conversation);
		if (maxExceeded()) {
			if (logger.isDebugEnabled()) {
				logger.debug("The max number of flow executions has been exceeded for the current user. " +
						"Removing the oldest conversation with id: " + conversations.get(0).getId());
			}
			// end oldest conversation
			conversations.get(0).end();
		}
		return conversation;
	}

	protected ConversationId nextId() {
		return new SimpleConversationId(++conversationIdSequence);
	}

	/**
	 * Return the identified conversation.
	 * @param id the id to lookup
	 * @return the conversation
	 * @throws NoSuchConversationException if the conversation cannot be found
	 */
	public synchronized Conversation getConversation(ConversationId id) throws NoSuchConversationException {
		for (ContainedConversation conversation : conversations) {
			if (conversation.getId().equals(id)) {
				return conversation;
			}
		}
		throw new NoSuchConversationException(id);
	}

	protected final List<ContainedConversation> getConversations() {
		return conversations;
	}

	/**
	 * Remove identified conversation from this container.
	 */
	public synchronized void removeConversation(ConversationId id) {
		for (Iterator<ContainedConversation> it = conversations.iterator(); it.hasNext();) {
			ContainedConversation conversation = it.next();
			if (conversation.getId().equals(id)) {
				conversations.remove(conversation);
				break;
			}
		}
	}

	/**
	 * Has the maximum number of allowed concurrent conversations in the session been exceeded?
	 */
	private boolean maxExceeded() {
		return maxConversations > 0 && conversations.size() > maxConversations;
	}

	// Hook methods

	protected ContainedConversation createContainedConversation(ConversationId id, ConversationLock lock) {
		return new ContainedConversation(this, id, lock);
	}
}
