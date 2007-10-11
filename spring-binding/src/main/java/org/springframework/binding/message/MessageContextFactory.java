package org.springframework.binding.message;

/**
 * A factory for creating message context's whose internal state can be externally managed. Encapsulates the message
 * context implementation used in a given environment.
 * 
 * @author Keith Donald
 */
public interface MessageContextFactory {

	/**
	 * Create a new message context.
	 * @return the message context, initially empty, capable of having its state managed by an external care-taker.
	 */
	public StateManageableMessageContext createMessageContext();
}
