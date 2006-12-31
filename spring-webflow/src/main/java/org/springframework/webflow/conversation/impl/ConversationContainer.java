/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.conversation.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;

/**
 * Container for conversations that is stored in the session. When the
 * session expires this container will go with it, implicitly expiring all
 * contained conversations.
 * <p>
 * This is an internal helper class of the {@link SessionBindingConversationManager}.
 * 
 * @author Erwin Vervaet
 */
class ConversationContainer implements Serializable {
	
	/**
	 * Maximum number of conversations in this container. -1 for
	 * unlimited.
	 */
	private int maxConversations;

	/**
	 * The contained conversations. A list of {@link ContainedConversation} objects.
	 */
	private List conversations;

	/**
	 * Create a new conversation container.
	 * @param maxConversations the maximum number of allowed concurrent
	 * conversations, -1 for unlimited
	 */
	public ConversationContainer(int maxConversations) {
		this.maxConversations = maxConversations;
		this.conversations = new ArrayList();
	}

	/**
	 * Create a new conversation based on given parameters and add it to the
	 * container.
	 * @param id the unique id of the conversation
	 * @param parameters descriptive parameters
	 * @return the created conversation
	 */
	public synchronized Conversation createAndAddConversation(ConversationId id, ConversationParameters parameters) {
		ContainedConversation conversation = new ContainedConversation(this, id);
		conversations.add(conversation);
		if (maxExceeded()) {
			// end oldest conversation
			((Conversation)conversations.get(0)).end();
		}
		return conversation;
	}

	/**
	 * Return the identified conversation.
	 * @param id the id to lookup
	 * @return the conversation
	 * @throws NoSuchConversationException if the conversation cannot be
	 * found
	 */
	public synchronized Conversation getConversation(ConversationId id) throws NoSuchConversationException {
		for (Iterator it = conversations.iterator(); it.hasNext();) {
			ContainedConversation conversation = (ContainedConversation)it.next();
			if (conversation.getId().equals(id)) {
				return conversation;
			}
		}
		throw new NoSuchConversationException(id);
	}

	/**
	 * Remove identified conversation from this container.
	 */
	public synchronized void removeConversation(ConversationId id) {
		for (Iterator it = conversations.iterator(); it.hasNext();) {
			ContainedConversation conversation = (ContainedConversation)it.next();
			if (conversation.getId().equals(id)) {
				it.remove();
				break;
			}
		}
	}

	/**
	 * Has the maximum number of allowed concurrent conversations in the
	 * session been exceeded?
	 */
	private boolean maxExceeded() {
		return maxConversations > 0 && conversations.size() > maxConversations;
	}
}