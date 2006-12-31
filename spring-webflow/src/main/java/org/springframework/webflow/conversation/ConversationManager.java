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
package org.springframework.webflow.conversation;

/**
 * A service for managing conversations. This interface is the entry point into
 * the conversation subsystem.
 * 
 * @author Keith Donald
 */
public interface ConversationManager {

	/**
	 * Begin a new conversation.
	 * @param conversationParameters descriptive conversation parameters
	 * @return a service interface allowing access to the conversation context
	 * @throws ConversationException an exception occured
	 */
	public Conversation beginConversation(ConversationParameters conversationParameters) throws ConversationException;

	/**
	 * Get the conversation with the provided id.
	 * @param id the conversation id
	 * @return the conversation
	 * @throws NoSuchConversationException the id provided was invalid
	 */
	public Conversation getConversation(ConversationId id) throws ConversationException;

	/**
	 * Parse the string-encoded conversationId into its object form.
	 * Essentially, the reverse of {@link ConversationId#toString()}.
	 * @param encodedId the encoded id
	 * @return the parsed conversation id
	 * @throws ConversationException an exception occured parsing the id
	 */
	public ConversationId parseConversationId(String encodedId) throws ConversationException;
}