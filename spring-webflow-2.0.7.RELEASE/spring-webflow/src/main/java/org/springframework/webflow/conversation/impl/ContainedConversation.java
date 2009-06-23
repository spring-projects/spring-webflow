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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Internal {@link Conversation} implementation used by the conversation container.
 * <p>
 * This is an internal helper class of the {@link SessionBindingConversationManager}.
 * 
 * @author Erwin Vervaet
 */
class ContainedConversation implements Conversation, Serializable {

	private static final Log logger = LogFactory.getLog(SessionBindingConversationManager.class);

	private ConversationContainer container;

	private ConversationId id;

	private ConversationLock lock;

	private Map attributes;

	/**
	 * Create a new contained conversation.
	 * @param container the container containing the conversation
	 * @param id the unique id assigned to the conversation
	 * @param lock the conversation lock
	 */
	public ContainedConversation(ConversationContainer container, ConversationId id, ConversationLock lock) {
		this.container = container;
		this.id = id;
		this.lock = lock;
		this.attributes = new HashMap();
	}

	public ConversationId getId() {
		return id;
	}

	public void lock() {
		if (logger.isDebugEnabled()) {
			logger.debug("Locking conversation " + id);
		}
		lock.lock();
	}

	public Object getAttribute(Object name) {
		return attributes.get(name);
	}

	public void putAttribute(Object name, Object value) {
		if (logger.isDebugEnabled()) {
			logger.debug("Putting conversation attribute '" + name + "' with value " + value);
		}
		attributes.put(name, value);
	}

	public void removeAttribute(Object name) {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing conversation attribute '" + name + "'");
		}
		attributes.remove(name);
	}

	public void end() {
		if (logger.isDebugEnabled()) {
			logger.debug("Ending conversation " + id);
		}
		container.removeConversation(getId());
	}

	public void unlock() {
		if (logger.isDebugEnabled()) {
			logger.debug("Unlocking conversation " + id);
		}
		lock.unlock();
		// re-bind the conversation container in the session
		// this is required to make session replication work correctly in
		// a clustered environment
		// we do this after releasing the lock since we're no longer
		// manipulating the contents of the conversation
		SharedAttributeMap sessionMap = ExternalContextHolder.getExternalContext().getSessionMap();
		synchronized (sessionMap.getMutex()) {
			sessionMap.put(container.getSessionKey(), container);
		}
	}

	public String toString() {
		return getId().toString();
	}

	// id based equality

	public boolean equals(Object obj) {
		if (!(obj instanceof ContainedConversation)) {
			return false;
		}
		return id.equals(((ContainedConversation) obj).id);
	}

	public int hashCode() {
		return id.hashCode();
	}

}