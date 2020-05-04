/*
 * Copyright 2004-2020 the original author or authors.
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
package org.springframework.webflow.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LocalAttributeMap}.
 */
public class LocalAttributeMapTests {

	private LocalAttributeMap<Object> attributeMap = new LocalAttributeMap<>();

	@BeforeEach
	public void setUp() {
		attributeMap.put("string", "A string");
		attributeMap.put("integer", 12345);
		attributeMap.put("boolean", true);
		attributeMap.put("long", 12345L);
		attributeMap.put("double", 12345d);
		attributeMap.put("float", 12345f);
		attributeMap.put("bigDecimal", new BigDecimal("12345.67"));
		attributeMap.put("bean", new TestBean());
		attributeMap.put("stringArray", new String[] { "1", "2", "3" });
		attributeMap.put("collection", new LinkedList<>());
	}

	@Test
	public void testGet() {
		TestBean bean = (TestBean) attributeMap.get("bean");
		assertNotNull(bean);
	}

	@Test
	public void testGetNull() {
		TestBean bean = (TestBean) attributeMap.get("bogus");
		assertNull(bean);
	}

	@Test
	public void testGetRequiredType() {
		TestBean bean = attributeMap.get("bean", TestBean.class);
		assertNotNull(bean);
	}

	@Test
	public void testGetWrongType() {
		try {
			attributeMap.get("bean", String.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetWithDefaultOption() {
		TestBean d = new TestBean();
		TestBean bean = (TestBean) attributeMap.get("bean", d);
		assertNotNull(bean);
		assertNotSame(bean, d);
	}

	@Test
	public void testGetWithDefault() {
		TestBean d = new TestBean();
		TestBean bean = (TestBean) attributeMap.get("bogus", d);
		assertSame(bean, d);
	}

	@Test
	public void testGetRequired() {
		TestBean bean = (TestBean) attributeMap.getRequired("bean");
		assertNotNull(bean);
	}

	@Test
	public void testGetRequiredNotPresent() {
		try {
			attributeMap.getRequired("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetRequiredOfType() {
		TestBean bean = attributeMap.getRequired("bean", TestBean.class);
		assertNotNull(bean);
	}

	@Test
	public void testGetRequiredWrongType() {
		try {
			attributeMap.getRequired("bean", String.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetNumber() {
		BigDecimal bd = attributeMap.getNumber("bigDecimal", BigDecimal.class);
		assertEquals(new BigDecimal("12345.67"), bd);
	}

	@Test
	public void testGetNumberWrongType() {
		try {
			attributeMap.getNumber("bigDecimal", Integer.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetNumberWithDefaultOption() {
		BigDecimal d = new BigDecimal("1");
		BigDecimal bd = attributeMap.getNumber("bigDecimal", BigDecimal.class, d);
		assertEquals(new BigDecimal("12345.67"), bd);
		assertNotSame(d, bd);
	}

	@Test
	public void testGetNumberWithDefault() {
		BigDecimal d = new BigDecimal("1");
		BigDecimal bd = attributeMap.getNumber("bogus", BigDecimal.class, d);
		assertEquals(d, bd);
		assertSame(d, bd);
	}

	@Test
	public void testGetNumberRequired() {
		BigDecimal bd = attributeMap.getRequiredNumber("bigDecimal", BigDecimal.class);
		assertEquals(new BigDecimal("12345.67"), bd);
	}

	@Test
	public void testGetNumberRequiredNotPresent() {
		try {
			attributeMap.getRequiredNumber("bogus", BigDecimal.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetInteger() {
		Integer i = attributeMap.getInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	@Test
	public void testGetIntegerNull() {
		Integer i = attributeMap.getInteger("bogus");
		assertNull(i);
	}

	@Test
	public void testGetIntegerRequired() {
		Integer i = attributeMap.getRequiredInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	@Test
	public void testGetIntegerRequiredNotPresent() {
		try {
			attributeMap.getRequiredInteger("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetLong() {
		Long i = attributeMap.getLong("long");
		assertEquals(new Long(12345), i);
	}

	@Test
	public void testGetLongNull() {
		Long i = attributeMap.getLong("bogus");
		assertNull(i);
	}

	@Test
	public void testGetLongRequired() {
		Long i = attributeMap.getRequiredLong("long");
		assertEquals(new Long(12345), i);
	}

	@Test
	public void testGetLongRequiredNotPresent() {
		try {
			attributeMap.getRequiredLong("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetString() {
		String i = attributeMap.getString("string");
		assertEquals("A string", i);
	}

	@Test
	public void testGetStringNull() {
		String i = attributeMap.getString("bogus");
		assertNull(i);
	}

	@Test
	public void testGetStringRequired() {
		String i = attributeMap.getRequiredString("string");
		assertEquals("A string", i);
	}

	@Test
	public void testGetStringRequiredNotPresent() {
		try {
			attributeMap.getRequiredString("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetBoolean() {
		Boolean i = attributeMap.getBoolean("boolean");
		assertEquals(Boolean.TRUE, i);
	}

	@Test
	public void testGetBooleanNull() {
		Boolean i = attributeMap.getBoolean("bogus");
		assertNull(i);
	}

	@Test
	public void testGetBooleanRequired() {
		Boolean i = attributeMap.getRequiredBoolean("boolean");
		assertEquals(Boolean.TRUE, i);
	}

	@Test
	public void testGetBooleanRequiredNotPresent() {
		try {
			attributeMap.getRequiredBoolean("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetArray() {
		String[] i = attributeMap.getArray("stringArray", String[].class);
		assertEquals(3, i.length);
	}

	@Test
	public void testGetArrayNull() {
		String[] i = attributeMap.getArray("A bogus array", String[].class);
		assertNull(i);
	}

	@Test
	public void testGetArrayRequired() {
		String[] i = attributeMap.getRequiredArray("stringArray", String[].class);
		assertEquals(3, i.length);
	}

	@Test
	public void testGetArrayRequiredNotPresent() {
		try {
			attributeMap.getRequiredArray("A bogus array", String[].class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCollection() {
		List<Object> i = attributeMap.getCollection("collection", List.class);
		assertTrue(i instanceof LinkedList);
		assertEquals(0, i.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCollectionNull() {
		List<Object> i = attributeMap.getCollection("bogus", List.class);
		assertNull(i);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCollectionRequired() {
		List<Object> i = attributeMap.getRequiredCollection("collection", List.class);
		assertEquals(0, i.size());
	}

	@Test
	public void testGetCollectionRequiredNotPresent() {
		try {
			attributeMap.getRequiredCollection("A bogus collection");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetMap() {
		Map<String, Object> map = attributeMap.asMap();
		assertEquals(10, map.size());
	}

	@Test
	public void testUnion() {
		LocalAttributeMap<Object> one = new LocalAttributeMap<>();
		one.put("foo", "bar");
		one.put("bar", "baz");

		LocalAttributeMap<Object> two = new LocalAttributeMap<>();
		two.put("cat", "coz");
		two.put("bar", "boo");

		AttributeMap<Object> three = one.union(two);
		assertEquals(3, three.size());
		assertEquals("bar", three.get("foo"));
		assertEquals("coz", three.get("cat"));
		assertEquals("boo", three.get("bar"));
	}

	@Test
	public void testEquality() {
		LocalAttributeMap<String> map = new LocalAttributeMap<>();
		map.put("foo", "bar");

		LocalAttributeMap<Object> map2 = new LocalAttributeMap<>();
		map2.put("foo", "bar");

		assertEquals(map, map2);
	}

	@Test
	public void testExtract() {
		assertEquals("A string", attributeMap.extract("string"));
		assertFalse(attributeMap.contains("string"));
	}

}
