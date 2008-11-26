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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.JdkVersion;

/**
 * Simple utility class for creating conversation lock instances based on the current execution environment.
 * 
 * @author Keith Donald
 * @author Rob Harrop
 */
class ConversationLockFactory {

	private static final Log logger = LogFactory.getLog(ConversationLockFactory.class);

	private static boolean backportConcurrentPresent;

	static {
		try {
			Class.forName("edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock");
			backportConcurrentPresent = true;
		} catch (ClassNotFoundException ex) {
			backportConcurrentPresent = false;
		}
	}

	private int timeoutSeconds = 30;

	/**
	 * Returns the period of time that can elapse before a lock attempt times out for locks created by this factory.
	 */
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	/**
	 * Sets the period of time that can elapse before a lock attempt times out for locks created by this factory.
	 * @param timeoutSeconds the timeout period in seconds
	 */
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	/**
	 * When running on Java 1.5+, returns a jdk5 concurrent lock. When running on older JDKs with the
	 * 'backport-util-concurrent' package available, returns a backport concurrent lock. In all other cases a "no-op"
	 * lock is returned.
	 */
	public ConversationLock createLock() {
		if (JdkVersion.getMajorJavaVersion() >= JdkVersion.JAVA_15) {
			return new JdkConcurrentConversationLock(timeoutSeconds);
		} else if (backportConcurrentPresent) {
			return new JdkBackportConcurrentConversationLock(timeoutSeconds);
		} else {
			logger.warn("Unable to enable conversation locking. Switch to Java 5 or above, "
					+ "or put the 'backport-util-concurrent' package on the classpath "
					+ "to enable locking in your Java 1.4 environment.");
			return NoOpConversationLock.INSTANCE;
		}
	}
}