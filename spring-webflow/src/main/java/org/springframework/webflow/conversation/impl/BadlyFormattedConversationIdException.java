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
