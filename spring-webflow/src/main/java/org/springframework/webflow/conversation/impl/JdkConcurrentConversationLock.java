/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.conversation.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A conversation lock that relies on a {@link ReentrantLock} within Java 5's <code>util.concurrent.locks</code>
 * package.
 * 
 * @author Keith Donald
 */
class JdkConcurrentConversationLock implements ConversationLock, Serializable {

	/**
	 * The lock.
	 */
	private Lock lock = new ReentrantLock();

	private int timeoutSeconds = 30;

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public void lock() {
		try {
			boolean acquired = lock.tryLock(timeoutSeconds, TimeUnit.SECONDS);
			if (!acquired) {
				throw new LockException("Unable to acquire conversation lock after " + timeoutSeconds + " seconds");
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException("Unable to acquire conversation lock - thread interrupted", e);
		}
	}

	public void unlock() {
		lock.unlock();
	}
}