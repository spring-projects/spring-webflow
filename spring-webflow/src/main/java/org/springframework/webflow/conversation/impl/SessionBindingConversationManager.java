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
package org.springframework.webflow.conversation.impl;

import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Simple implementation of a conversation manager that stores conversations in the session attribute map.
 * <p>
 * Using the {@link #setMaxConversations(int) maxConversations} property, you can limit the number of concurrently
 * active conversations allowed in a single session. If the maximum is exceeded, the conversation manager will
 * automatically end the oldest conversation. The default is 5, which should be fine for most situations. Set it to -1
 * for no limit. Setting maxConversations to 1 allows easy resource cleanup in situations where there should only be one
 * active conversation per session.
 * 
 * @author Erwin Vervaet
 */
public class SessionBindingConversationManager implements ConversationManager {

	/**
	 * The name of the session attribute that will hold the conversation container used by this conversation manager.
	 * 
	 * To support multiple independent conversation containers in the same web application, for example, for use with
	 * multiple flow executors each configured with their own session-binding conversation manager, set this field's
	 * value to something unique.
	 * @see #setSessionKey(String)
	 */
	private String sessionKey = "webflowConversationContainer";

	/**
	 * The maximum number of active conversations allowed in a session. The default is 5. This is high enough for most
	 * practical situations and low enough to avoid excessive resource usage or easy denial of service attacks.
	 */
	private int maxConversations = 5;

	/**
	 * The factory for creating conversation lock objects.
	 */
	private ConversationLockFactory conversationLockFactory = new ConversationLockFactory();

	/**
	 * Returns the key this conversation manager uses to store conversation data in the session.
	 * @return the session key
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * Sets the key this conversation manager uses to store conversation data in the session. If multiple session
	 * binding conversation managers are used in the same web application to back independent flow executors, this value
	 * should be unique among them.
	 * @param sessionKey the session key
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * Returns the maximum number of allowed concurrent conversations. The default is 5.
	 */
	public int getMaxConversations() {
		return maxConversations;
	}

	/**
	 * Set the maximum number of allowed concurrent conversations. Set to -1 for no limit. The default is 5.
	 */
	public void setMaxConversations(int maxConversations) {
		this.maxConversations = maxConversations;
	}

	/**
	 * Returns the time period that can elapse before a timeout occurs on an attempt to acquire a conversation lock. The
	 * default is 30 seconds.
	 */
	public int getLockTimeoutSeconds() {
		return conversationLockFactory.getTimeoutSeconds();
	}

	/**
	 * Sets the time period that can elapse before a timeout occurs on an attempt to acquire a conversation lock. The
	 * default is 30 seconds.
	 * @param timeoutSeconds the timeout period in seconds
	 */
	public void setLockTimeoutSeconds(int timeoutSeconds) {
		conversationLockFactory.setTimeoutSeconds(timeoutSeconds);
	}

	// implementing conversation manager

	public Conversation beginConversation(ConversationParameters conversationParameters) throws ConversationException {
		return getConversationContainer().createConversation(conversationParameters, conversationLockFactory);
	}

	public Conversation getConversation(ConversationId id) throws ConversationException {
		return getConversationContainer().getConversation(id);
	}

	public ConversationId parseConversationId(String encodedId) throws ConversationException {
		try {
			return new SimpleConversationId(Integer.valueOf(encodedId));
		} catch (NumberFormatException e) {
			throw new BadlyFormattedConversationIdException(encodedId, e);
		}
	}

	// hooks for subclassing

	protected ConversationContainer createConversationContainer() {
		return new ConversationContainer(maxConversations, sessionKey);
	}

	// internal helpers

	/**
	 * Obtain the conversation container from the session. Create a new empty container and add it to the session if no
	 * existing container can be found.
	 */
	private ConversationContainer getConversationContainer() {
		SharedAttributeMap sessionMap = ExternalContextHolder.getExternalContext().getSessionMap();
		synchronized (sessionMap.getMutex()) {
			ConversationContainer container = (ConversationContainer) sessionMap.get(sessionKey);
			if (container == null) {
				container = createConversationContainer();
				sessionMap.put(sessionKey, container);
			}
			return container;
		}
	}
}