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
package org.springframework.webflow.conversation;

/**
 * A service interface for working with state associated with a single logical user interaction called a "conversation"
 * in the scope of a single request. Conversation objects are not thread safe and should not be shared among multiple
 * threads.
 * <p>
 * A conversation provides a "task" context that is begun and eventually ends. Between the beginning and the end
 * attributes can be placed in and read from a conversation's context.
 * <p>
 * A conversation needs to be {@link #lock() locked} to obtain exclusive access to it before it can be manipulated. Once
 * manipulation is finished, you need to {@link #unlock() unlock} the conversation. So code interacting with a
 * conversation always looks like this:
 * 
 * <pre>
 * Conversation conv = ...;
 * conv.lock();
 * try {
 *    // work with the Conversation object, calling methods like
 *    // getAttribute(), putAttribute() and end()
 * }
 * finally {
 *    conv.unlock();
 * }
 * </pre>
 * 
 * <p>
 * Note that the attributes associated with a conversation are not "conversation scope" as defined for a flow execution.
 * They can be any attributes, possibly technical in nature, associated with the conversation.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface Conversation {

	/**
	 * Returns the unique id assigned to this conversation. This id remains the same throughout the life of the
	 * conversation. This method can be safely called without owning the lock of this conversation.
	 * @return the conversation id
	 */
	public ConversationId getId();

	/**
	 * Lock this conversation. May block until the lock is available, if someone else has acquired the lock.
	 * @throws ConversationLockException if the lock could not be acquired
	 */
	public void lock() throws ConversationLockException;

	/**
	 * Returns the conversation attribute with the specified name. You need to acquire the lock on this conversation
	 * before calling this method.
	 * @param name the attribute name
	 * @return the attribute value
	 */
	public Object getAttribute(Object name);

	/**
	 * Puts a conversation attribute into this context. You need to acquire the lock on this conversation before calling
	 * this method.
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void putAttribute(Object name, Object value);

	/**
	 * Removes a conversation attribute. You need to acquire the lock on this conversation before calling this method.
	 * @param name the attribute name
	 */
	public void removeAttribute(Object name);

	/**
	 * Ends this conversation. This method should only be called once to terminate the conversation and cleanup any
	 * allocated resources. You need to aquire the lock on this conversation before calling this method.
	 */
	public void end();

	/**
	 * Unlock this conversation, making it available to others for manipulation.
	 */
	public void unlock();

}