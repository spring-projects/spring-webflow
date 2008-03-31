package org.springframework.webflow.conversation;


/**
 * An exception occurred within the conversation locking system.
 * 
 * @author Keith Donald
 */
public abstract class ConversationLockException extends ConversationException {

	/**
	 * Creates a new lock exception.
	 * @param msg the <code>Exception</code> message
	 */
	public ConversationLockException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new lock exception.
	 * @param msg the <code>Exception</code> message
	 * @param cause the root cause of this <code>Exception</code>
	 */
	public ConversationLockException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
