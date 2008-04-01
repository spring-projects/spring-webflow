package org.springframework.binding.message;

/**
 * A predicate used to select mapping result objects in a call to {@link MessageContext#getMessagesByCriteria(MessageCriteria)}.
 * @author Keith Donald
 */
public interface MessageCriteria {

	/**
	 * Tests if the message meets this criteria.
	 * @param message the message
	 * @return true if this criteria is met for the message, false if not
	 */
	public boolean test(Message message);
}
