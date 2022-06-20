/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.conversation.impl;

import java.io.ObjectStreamException;

/**
 * A singleton lock that doesn't do anything. For use when conversations don't require or choose not to implement
 * locking.
 * 
 * @author Keith Donald
 */
public class NoOpConversationLock implements ConversationLock {

	/**
	 * The singleton instance.
	 */
	public static final NoOpConversationLock INSTANCE = new NoOpConversationLock();

	/**
	 * Private constructor to avoid instantiation.
	 */
	private NoOpConversationLock() {
	}

	public void lock() {
		// no-op
	}

	public void unlock() {
		// no-op
	}

	// resolve the singleton instance
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}
}