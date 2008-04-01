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
	 * Add a new message to this context.
	 * @param messageResolver the resolver that will resolve the message to be added
	 */
	public void addMessage(MessageResolver messageResolver);

	/**
	 * Clear all messages added to this context.
	 */
	public void clearMessages();

}
