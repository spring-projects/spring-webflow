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
package org.springframework.binding.message;

/**
 * A context for recording and retrieving messages for display.
 */
public interface MessageContext {

	/**
	 * Get all messages in this context. The messages returned should be suitable for display as-is.
	 * @return the messages
	 */
	public Message[] getAllMessages();

	/**
	 * Get all messages in this context for the source provided.
	 * @param source the source associated with messages, or null for global messages
	 * @return the source's messages
	 */
	public Message[] getMessagesBySource(Object source);

	/**
	 * Get all messages that meet the given result criteria.
	 * @param criteria the message criteria
	 */
	public Message[] getMessagesByCriteria(MessageCriteria criteria);

	/**
	 * Returns true if there are error messages in this context.
	 * @return error messages
	 */
	public boolean hasErrorMessages();

	/**
	 * Add a new message to this context.
	 * @param messageResolver the resolver that will resolve the message to be added
	 */
	public void addMessage(MessageResolver messageResolver);

	/**
	 * Clear all messages added to this context.
	 */
	public void clearMessages();

}
