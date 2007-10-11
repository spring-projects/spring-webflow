package org.springframework.binding.message;

/**
 * A context for recording and retrieving messages for display.
 */
public interface MessageContext {

	/**
	 * Get all messages in this context. The messages returned should be suitable for display as-is.
	 * @return the messages
	 */
	public Message[] getMessages();

	/**
	 * Get all messages in this context from the source provided.
	 * @param source the source that recorded the message
	 * @return the source's messages
	 */
	public Message[] getMessages(Object source);

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
