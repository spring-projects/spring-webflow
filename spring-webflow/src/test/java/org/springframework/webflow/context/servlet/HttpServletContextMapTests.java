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
package org.springframework.webflow.context.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

/**
 * Test case for the {@link HttpServletContextMap} class.
 * 
 * @author Ulrik Sandberg
 * @author Erwin Vervaet
 */
public class HttpServletContextMapTests {

	private HttpServletContextMap tested;

	private MockServletContext context;

	@BeforeEach
	public void setUp() throws Exception {
		context = new MockServletContext();
		// a fresh MockServletContext seems to already contain an element;
		// that's confusing, so we remove it
		context.removeAttribute("jakarta.servlet.context.tempdir");
		tested = new HttpServletContextMap(context);
		tested.put("SomeKey", "SomeValue");
	}

	@AfterEach
	public void tearDown() throws Exception {
		context = null;
		tested = null;
	}

	@Test
	public void testIsEmpty() {
		tested.remove("SomeKey");
		assertEquals(0, tested.size(), "size,");
		assertEquals(true, tested.isEmpty(), "isEmpty,");
	}

	@Test
	public void testSizeAddOne() {
		assertEquals(1, tested.size(), "size,");
	}

	@Test
	public void testSizeAddTwo() {
		tested.put("SomeOtherKey", "SomeOtherValue");
		assertEquals(2, tested.size(), "size,");
	}

	@Test
	public void testContainsKey() {
		assertEquals(true, tested.containsKey("SomeKey"), "containsKey,");
	}

	@Test
	public void testContainsValue() {
		assertTrue(tested.containsValue("SomeValue"));
	}

	@Test
	public void testGet() {
		assertEquals("SomeValue", tested.get("SomeKey"), "get,");
	}

	@Test
	public void testPut() {
		Object old = tested.put("SomeKey", "SomeNewValue");

		assertEquals("SomeValue", old, "old value,");
		assertEquals("SomeNewValue", tested.get("SomeKey"), "new value,");
	}

	@Test
	public void testRemove() {
		Object old = tested.remove("SomeKey");

		assertEquals("SomeValue", old, "old value,");
		assertNull(tested.get("SomeKey"), "should be gone");
	}

	@Test
	public void testPutAll() {
		Map<String, Object> otherMap = new HashMap<>();
		otherMap.put("SomeOtherKey", "SomeOtherValue");
		otherMap.put("SomeKey", "SomeUpdatedValue");
		tested.putAll(otherMap);
		assertEquals("SomeOtherValue", tested.get("SomeOtherKey"));
		assertEquals("SomeUpdatedValue", tested.get("SomeKey"));
	}

	@Test
	public void testClear() {
		tested.clear();
		assertTrue(tested.isEmpty());
	}

	@Test
	public void testKeySet() {
		assertEquals(1, tested.keySet().size());
		assertTrue(tested.keySet().contains("SomeKey"));
	}

	@Test
	public void testValues() {
		assertEquals(1, tested.values().size());
		assertTrue(tested.values().contains("SomeValue"));
	}

	@Test
	public void testEntrySet() {
		assertEquals(1, tested.entrySet().size());
		assertEquals("SomeKey", tested.entrySet().iterator().next().getKey());
		assertEquals("SomeValue", tested.entrySet().iterator().next().getValue());
	}
}
