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
package org.springframework.webflow.conversation.impl;

import org.springframework.webflow.conversation.ConversationLockException;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;

/**
 * A conversation lock that relies on backport-concurrent. For use on JDK 1.4
 * 
 * @author Keith Donald
 * @author Rob Harrop
 */
class JdkBackportConcurrentConversationLock implements ConversationLock {

	private Lock lock = new ReentrantLock();

	private int timeoutSeconds;

	public JdkBackportConcurrentConversationLock(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public void lock() throws ConversationLockException {
		try {
			boolean acquired = lock.tryLock(timeoutSeconds, TimeUnit.SECONDS);
			if (!acquired) {
				throw new LockTimeoutException(timeoutSeconds);
			}
		} catch (InterruptedException e) {
			throw new LockInterruptedException(e);
		}
	}

	/**
	 * Releases the lock.
	 */
	public void unlock() {
		lock.unlock();
	}
}