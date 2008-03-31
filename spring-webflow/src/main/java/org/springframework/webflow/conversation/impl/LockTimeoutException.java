package org.springframework.webflow.conversation.impl;

import org.springframework.webflow.conversation.ConversationLockException;

/**
 * Thrown when a lock could not be acquired after a timeout period.
 * 
 * @author Keith Donald
 */
public class LockTimeoutException extends ConversationLockException {

	public LockTimeoutException(int timeoutSeconds) {
		super("Unable to acquire conversation lock after " + timeoutSeconds + " seconds");
	}

}
