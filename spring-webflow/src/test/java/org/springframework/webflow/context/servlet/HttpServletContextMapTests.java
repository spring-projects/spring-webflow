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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

	@Before
	public void setUp() throws Exception {
		context = new MockServletContext();
		// a fresh MockServletContext seems to already contain an element;
		// that's confusing, so we remove it
		context.removeAttribute("javax.servlet.context.tempdir");
		tested = new HttpServletContextMap(context);
		tested.put("SomeKey", "SomeValue");
	}

	@After
	public void tearDown() throws Exception {
		context = null;
		tested = null;
	}

	@Test
	public void testIsEmpty() {
		tested.remove("SomeKey");
		assertEquals("size,", 0, tested.size());
		assertEquals("isEmpty,", true, tested.isEmpty());
	}

	@Test
	public void testSizeAddOne() {
		assertEquals("size,", 1, tested.size());
	}

	@Test
	public void testSizeAddTwo() {
		tested.put("SomeOtherKey", "SomeOtherValue");
		assertEquals("size,", 2, tested.size());
	}

	@Test
	public void testContainsKey() {
		assertEquals("containsKey,", true, tested.containsKey("SomeKey"));
	}

	@Test
	public void testContainsValue() {
		assertTrue(tested.containsValue("SomeValue"));
	}

	@Test
	public void testGet() {
		assertEquals("get,", "SomeValue", tested.get("SomeKey"));
	}

	@Test
	public void testPut() {
		Object old = tested.put("SomeKey", "SomeNewValue");

		assertEquals("old value,", "SomeValue", old);
		assertEquals("new value,", "SomeNewValue", tested.get("SomeKey"));
	}

	@Test
	public void testRemove() {
		Object old = tested.remove("SomeKey");

		assertEquals("old value,", "SomeValue", old);
		assertNull("should be gone", tested.get("SomeKey"));
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
