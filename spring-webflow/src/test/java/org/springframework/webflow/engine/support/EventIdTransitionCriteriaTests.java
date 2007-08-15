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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

public class EventIdTransitionCriteriaTests extends TestCase {
	public void testTestCriteria() {
		EventIdTransitionCriteria c = new EventIdTransitionCriteria("foo");
		MockRequestContext context = new MockRequestContext();
		context.setLastEvent(new Event(this, "foo"));
		assertEquals(true, c.test(context));
		context.setLastEvent(new Event(this, "FOO"));
		assertEquals(false, c.test(context)); // case sensitive
		context.setLastEvent(new Event(this, "bar"));
		assertEquals(false, c.test(context));
	}

	public void testNullLastEventId() {
		EventIdTransitionCriteria c = new EventIdTransitionCriteria("foo");
		MockRequestContext context = new MockRequestContext();
		context.setLastEvent(null);
		assertEquals(false, c.test(context));
	}

	public void testIllegalArg() {
		try {
			new EventIdTransitionCriteria(null);
			fail("was null");
		} catch (IllegalArgumentException e) {

		}
		try {
			new EventIdTransitionCriteria("");
			fail("was blank");
		} catch (IllegalArgumentException e) {

		}
	}
}
