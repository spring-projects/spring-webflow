/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.execution.repository.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.webflow.conversation.impl.SimpleConversationId;

public class CompositeFlowExecutionKeyTests {

	@Test
	public void testToString() {
		CompositeFlowExecutionKey key = new CompositeFlowExecutionKey(new SimpleConversationId("1"), "1");
		assertEquals("e1s1", key.toString());
	}

	@Test
	public void testEquals() {
		CompositeFlowExecutionKey key = new CompositeFlowExecutionKey(new SimpleConversationId("foo"), "bar");
		CompositeFlowExecutionKey key2 = new CompositeFlowExecutionKey(new SimpleConversationId("foo"), "bar");
		assertEquals(key, key2);
	}

}
