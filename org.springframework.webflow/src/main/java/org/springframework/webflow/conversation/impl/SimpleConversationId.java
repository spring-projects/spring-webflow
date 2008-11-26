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

import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;

/**
 * An id that uniquely identifies a conversation managed by a {@link ConversationManager}.
 * 
 * @author Ben Hale
 */
public class SimpleConversationId extends ConversationId {

	/**
	 * The id value.
	 */
	private Serializable id;

	/**
	 * Creates a new simple conversation id.
	 * @param id the id value
	 */
	public SimpleConversationId(Serializable id) {
		this.id = id;
	}

	public boolean equals(Object o) {
		if (!(o instanceof SimpleConversationId)) {
			return false;
		}
		return id.equals(((SimpleConversationId) o).id);
	}

	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return id.toString();
	}
}