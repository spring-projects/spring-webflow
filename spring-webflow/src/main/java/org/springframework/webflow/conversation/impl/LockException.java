package org.springframework.webflow.conversation.impl;

import org.springframework.webflow.conversation.ConversationException;

public class LockException extends ConversationException {
	public LockException(String message) {
		super(message);
	}
}
