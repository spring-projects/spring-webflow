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
 * Thrown when a lock could not be acquired after a timeout period.
 * 
 * @author Keith Donald
 */
public class LockTimeoutException extends ConversationLockException {

	public LockTimeoutException(int timeoutSeconds) {
		super("Unable to acquire conversation lock after " + timeoutSeconds + " seconds");
	}

}
