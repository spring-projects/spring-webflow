package org.springframework.webflow.conversation.impl;

import org.springframework.webflow.conversation.ConversationLockException;

/**
 * <code>Exception</code> indicating that some {@link Thread} was {@link Thread#interrupt() interrupted} during
 * processing and as such processing was halted.
 * <p>
 * Only used to wrap the checked {@link InterruptedException java.lang.InterruptedException}.
 */
public class LockInterruptedException extends ConversationLockException {

	/**
	 * Creates a new <code>SystemInterruptedException</code>.
	 * @param cause the root cause of this <code>Exception</code>
	 */
	public LockInterruptedException(InterruptedException cause) {
		super("Unable to acquire conversation lock - thread interrupted", cause);
	}
}