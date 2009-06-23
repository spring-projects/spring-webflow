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

/**
 * <code>Exception</code> indicating that some {@link Thread} was {@link Thread#interrupt() interrupted} during
 * processing and as such processing was halted.
 * <p>
 * Only used to wrap the checked {@link InterruptedException java.lang.InterruptedException}.
 */
public class LockInterruptedException extends ConversationLockException {

	/**
	 * Creates a new <code>SystemInterruptedException</code>.
	 * @param cause the root cause of this <code>Exception</code>
	 */
	public LockInterruptedException(InterruptedException cause) {
		super("Unable to acquire conversation lock - thread interrupted", cause);
	}
}