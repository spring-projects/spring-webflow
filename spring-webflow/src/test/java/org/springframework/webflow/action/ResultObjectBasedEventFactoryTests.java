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
package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Test case for {@link ResultObjectBasedEventFactory}.
 * 
 * @author Erwin Vervaet
 */
public class ResultObjectBasedEventFactoryTests {

	private ResultObjectBasedEventFactory factory = new ResultObjectBasedEventFactory();

	@Test
	public void testNull() {
		Event event = factory.createResultEvent(this, null, new MockRequestContext());
		assertEquals(factory.getNullEventId(), event.getId());
	}

	@Test
	public void testBoolean() {
		Event event = factory.createResultEvent(this, true, new MockRequestContext());
		assertEquals(factory.getYesEventId(), event.getId());
		event = factory.createResultEvent(this, false, new MockRequestContext());
		assertEquals(factory.getNoEventId(), event.getId());
	}

	@Test
	public void testLabeledEnum() {
		Event event = factory.createResultEvent(this, MyLabeledEnum.A, new MockRequestContext());
		assertEquals("A", event.getId());
		assertSame(MyLabeledEnum.A, event.getAttributes().get("result"));
	}

	public enum MyLabeledEnum {
		A, B;
	}

	/*
	 * public void testJava5Enum() { Event event = factory.createResultEvent(this, MyEnum.A, new MockRequestContext());
	 * assertEquals("A", event.getId()); assertSame(MyEnum.A, event.getAttributes().get("result")); }
	 * 
	 * public static enum MyEnum { A, B;
	 * 
	 * public String toString() { return "MyEnum " + name(); } }
	 */

	@Test
	public void testString() {
		Event event = factory.createResultEvent(this, "foobar", new MockRequestContext());
		assertEquals("foobar", event.getId());
	}

	@Test
	public void testEvent() {
		Event orig = new Event(this, "test");
		Event event = factory.createResultEvent(this, orig, new MockRequestContext());
		assertSame(orig, event);
	}

	@Test
	public void testUnsupported() {
		try {
			factory.createResultEvent(this, new Date(), new MockRequestContext());
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
