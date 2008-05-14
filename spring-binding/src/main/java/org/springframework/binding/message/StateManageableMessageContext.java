/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.message;

import java.io.Serializable;

import org.springframework.context.MessageSource;

/**
 * A message context whose internal state can be managed by an external care-taker. State management employs the GOF
 * Memento pattern. This context can produce a serializable memento representing its internal state at any time. A
 * care-taker can then use that memento at a later time to restore any context instance to a previous state.
 * 
 * @author Keith Donald
 */
public interface StateManageableMessageContext extends MessageContext {

	/**
	 * Create a serializable memento, or token representing a snapshot of the internal state of this message context.
	 * @return the messages memento
	 */
	public Serializable createMessagesMemento();

	/**
	 * Set the state of this context from the memento provided. After this call, the messages in this context will match
	 * what is encapsulated inside the memento. Any previous state will be overridden.
	 * @param messagesMemento the messages memento
	 */
	public void restoreMessages(Serializable messagesMemento);

	/**
	 * Configure the message source used to resolve messages added to this context. May be set at any time to change how
	 * coded messages are resolved.
	 * @param messageSource the message source
	 * @see MessageContext#addMessage(MessageResolver)
	 */
	public void setMessageSource(MessageSource messageSource);
}
