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
package org.springframework.webflow.conversation;

/**
 * Thrown when no logical conversation exists with the specified <code>conversationId</code>. This might occur if the
 * conversation ended, expired, or was otherwise invalidated, but a client view still references it.
 * 
 * @author Keith Donald
 */
public class NoSuchConversationException extends ConversationException {

	/**
	 * The unique conversation identifier that was invalid.
	 */
	private ConversationId conversationId;

	/**
	 * Create a new conversation lookup exception.
	 * @param conversationId the conversation id
	 */
	public NoSuchConversationException(ConversationId conversationId) {
		super("No conversation could be found with id '" + conversationId
				+ "' -- perhaps this conversation has ended? ");
		this.conversationId = conversationId;
	}

	/**
	 * Returns the conversation id that was not found.
	 */
	public ConversationId getConversationId() {
		return conversationId;
	}
}