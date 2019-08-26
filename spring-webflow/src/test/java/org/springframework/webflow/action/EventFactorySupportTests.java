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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.execution.Event;

/**
 * Unit tests for {@link EventFactorySupport}.
 */
public class EventFactorySupportTests {

	private EventFactorySupport support = new EventFactorySupport();

	private Object source = new Object();

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void testSuccess() {
		Event e = support.success(source);
		assertEquals("success", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testSuccessWithResult() {
		Object result = new Object();
		Event e = support.success(source, result);
		assertEquals("success", e.getId());
		assertSame(source, e.getSource());
		assertSame(result, e.getAttributes().get("result"));
	}

	@Test
	public void testError() {
		Event e = support.error(source);
		assertEquals("error", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testErrorWithException() {
		Exception ex = new Exception();
		Event e = support.error(source, ex);
		assertEquals("error", e.getId());
		assertSame(source, e.getSource());
		assertSame(ex, e.getAttributes().get("exception"));
	}

	@Test
	public void testYes() {
		Event e = support.yes(source);
		assertEquals("yes", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testNo() {
		Event e = support.no(source);
		assertEquals("no", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testBooleanTrueEvent() {
		Event e = support.event(source, true);
		assertEquals("yes", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testBooleanFalseEvent() {
		Event e = support.event(source, false);
		assertEquals("no", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testEvent() {
		Event e = support.event(source, "no");
		assertEquals("no", e.getId());
		assertSame(source, e.getSource());
	}

	@Test
	public void testEventWithAttrs() {
		Event e = support.event(source, "no", "foo", "bar");
		assertEquals("no", e.getId());
		assertEquals("bar", e.getAttributes().get("foo"));
		assertSame(source, e.getSource());
	}

}
