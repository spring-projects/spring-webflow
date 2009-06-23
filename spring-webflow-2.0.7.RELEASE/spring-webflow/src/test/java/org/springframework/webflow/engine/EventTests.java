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
package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class EventTests extends TestCase {

	public void testNewEvent() {
		Event event = new Event(this, "id");
		assertEquals("id", event.getId());
		assertTrue(event.getTimestamp() > 0);
		assertTrue(event.getAttributes().isEmpty());
	}

	public void testEventNullSource() {
		try {
			new Event(null, "id");
			fail("null source");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testEventNullId() {
		try {
			new Event(this, null);
			fail("null id");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testNewEventWithAttributes() {
		LocalAttributeMap attrs = new LocalAttributeMap();
		attrs.put("name", "value");
		Event event = new Event(this, "id", attrs);
		assertTrue(!event.getAttributes().isEmpty());
		assertEquals(1, event.getAttributes().size());
	}

	public void testNewEventNullAttributes() {
		Event event = new Event(this, "id", null);
		assertTrue(event.getAttributes().isEmpty());
	}

}
