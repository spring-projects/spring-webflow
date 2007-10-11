package org.springframework.binding.message;

import java.io.Serializable;

/**
 * A message context whose internal state can be managed by an external care-taker. State management employs the GOF
 * Memento pattern. This context can produce a serializable memento representing its internal state at any time. A
 * care-taker can then use that memento at a later time to restore any context instance to a previous state.
 * 
 * @author Keith Donald
 */
public interface StateManageableMessageContext extends MessageContext {

	/**
	 * Create a serializable memento (token) representing a snapshot of the internal state of this message context.
	 * @return the messages memento
	 */
	public Serializable createMessagesMemento();

	/**
	 * Set the state of this context from the memento provided. After this call, the messages in this context will match
	 * what is encapsulated inside the memento. Any previous state will be overridden.
	 * @param messagesMemento the messages memento
	 */
	public void restoreMessages(Serializable messagesMemento);
}
