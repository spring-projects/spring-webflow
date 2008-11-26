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

import org.springframework.webflow.conversation.ConversationException;

/**
 * Thrown if a conversation id could not be parsed.
 * 
 * @author Keith Donald
 */
public class BadlyFormattedConversationIdException extends ConversationException {

	/**
	 * Creates a new badly formatted conversation id exception.
	 * @param encodedId the badly formatted id
	 * @param parseException the cause
	 */
	public BadlyFormattedConversationIdException(String encodedId, Throwable parseException) {
		super("Unable to parse string-encoded conversationId + '" + encodedId + "'", parseException);
	}
}
