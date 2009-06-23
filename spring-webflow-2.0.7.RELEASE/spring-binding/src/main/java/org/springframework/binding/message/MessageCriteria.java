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

/**
 * A predicate used to select mapping result objects in a call to
 * {@link MessageContext#getMessagesByCriteria(MessageCriteria)}.
 * @author Keith Donald
 */
public interface MessageCriteria {

	/**
	 * Tests if the message meets this criteria.
	 * @param message the message
	 * @return true if this criteria is met for the message, false if not
	 */
	public boolean test(Message message);
}
