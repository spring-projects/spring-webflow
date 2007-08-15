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
package org.springframework.webflow.core.collection;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests for {@link LocalAttributeMap}.
 */
public class LocalAttributeMapTests extends TestCase {

	private LocalAttributeMap attributeMap = new LocalAttributeMap();

	public void setUp() {
		attributeMap.put("string", "A string");
		attributeMap.put("integer", new Integer(12345));
		attributeMap.put("boolean", Boolean.TRUE);
		attributeMap.put("long", new Long(12345));
		attributeMap.put("double", new Double(12345));
		attributeMap.put("float", new Float(12345));
		attributeMap.put("bigDecimal", new BigDecimal("12345.67"));
		attributeMap.put("bean", new TestBean());
		attributeMap.put("stringArray", new String[] { "1", "2", "3" });
		attributeMap.put("collection", new LinkedList());
	}

	public void testGet() {
		TestBean bean = (TestBean) attributeMap.get("bean");
		assertNotNull(bean);
	}

	public void testGetNull() {
		TestBean bean = (TestBean) attributeMap.get("bogus");
		assertNull(bean);
	}

	public void testGetRequiredType() {
		TestBean bean = (TestBean) attributeMap.get("bean", TestBean.class);
		assertNotNull(bean);
	}

	public void testGetWrongType() {
		try {
			attributeMap.get("bean", String.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetWithDefaultOption() {
		TestBean d = new TestBean();
		TestBean bean = (TestBean) attributeMap.get("bean", d);
		assertNotNull(bean);
		assertNotSame(bean, d);
	}

	public void testGetWithDefault() {
		TestBean d = new TestBean();
		TestBean bean = (TestBean) attributeMap.get("bogus", d);
		assertSame(bean, d);
	}

	public void testGetRequired() {
		TestBean bean = (TestBean) attributeMap.getRequired("bean");
		assertNotNull(bean);
	}

	public void testGetRequiredNotPresent() {
		try {
			attributeMap.getRequired("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetRequiredOfType() {
		TestBean bean = (TestBean) attributeMap.getRequired("bean", TestBean.class);
		assertNotNull(bean);
	}

	public void testGetRequiredWrongType() {
		try {
			attributeMap.getRequired("bean", String.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetNumber() {
		BigDecimal bd = (BigDecimal) attributeMap.getNumber("bigDecimal", BigDecimal.class);
		assertEquals(new BigDecimal("12345.67"), bd);
	}

	public void testGetNumberWrongType() {
		try {
			attributeMap.getNumber("bigDecimal", Integer.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetNumberWithDefaultOption() {
		BigDecimal d = new BigDecimal("1");
		BigDecimal bd = (BigDecimal) attributeMap.getNumber("bigDecimal", BigDecimal.class, d);
		assertEquals(new BigDecimal("12345.67"), bd);
		assertNotSame(d, bd);
	}

	public void testGetNumberWithDefault() {
		BigDecimal d = new BigDecimal("1");
		BigDecimal bd = (BigDecimal) attributeMap.getNumber("bogus", BigDecimal.class, d);
		assertEquals(d, bd);
		assertSame(d, bd);
	}

	public void testGetNumberRequired() {
		BigDecimal bd = (BigDecimal) attributeMap.getRequiredNumber("bigDecimal", BigDecimal.class);
		assertEquals(new BigDecimal("12345.67"), bd);
	}

	public void testGetNumberRequiredNotPresent() {
		try {
			attributeMap.getRequiredNumber("bogus", BigDecimal.class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetInteger() {
		Integer i = attributeMap.getInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	public void testGetIntegerNull() {
		Integer i = attributeMap.getInteger("bogus");
		assertNull(i);
	}

	public void testGetIntegerRequired() {
		Integer i = attributeMap.getRequiredInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	public void testGetIntegerRequiredNotPresent() {
		try {
			attributeMap.getRequiredInteger("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetLong() {
		Long i = attributeMap.getLong("long");
		assertEquals(new Long(12345), i);
	}

	public void testGetLongNull() {
		Long i = attributeMap.getLong("bogus");
		assertNull(i);
	}

	public void testGetLongRequired() {
		Long i = attributeMap.getRequiredLong("long");
		assertEquals(new Long(12345), i);
	}

	public void testGetLongRequiredNotPresent() {
		try {
			attributeMap.getRequiredLong("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetString() {
		String i = attributeMap.getString("string");
		assertEquals("A string", i);
	}

	public void testGetStringNull() {
		String i = attributeMap.getString("bogus");
		assertNull(i);
	}

	public void testGetStringRequired() {
		String i = attributeMap.getRequiredString("string");
		assertEquals("A string", i);
	}

	public void testGetStringRequiredNotPresent() {
		try {
			attributeMap.getRequiredString("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetBoolean() {
		Boolean i = attributeMap.getBoolean("boolean");
		assertEquals(Boolean.TRUE, i);
	}

	public void testGetBooleanNull() {
		Boolean i = attributeMap.getBoolean("bogus");
		assertNull(i);
	}

	public void testGetBooleanRequired() {
		Boolean i = attributeMap.getRequiredBoolean("boolean");
		assertEquals(Boolean.TRUE, i);
	}

	public void testGetBooleanRequiredNotPresent() {
		try {
			attributeMap.getRequiredBoolean("bogus");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetArray() {
		String[] i = (String[]) attributeMap.getArray("stringArray", String[].class);
		assertEquals(3, i.length);
	}

	public void testGetArrayNull() {
		String[] i = (String[]) attributeMap.getArray("A bogus array", String[].class);
		assertNull(i);
	}

	public void testGetArrayRequired() {
		String[] i = (String[]) attributeMap.getRequiredArray("stringArray", String[].class);
		assertEquals(3, i.length);
	}

	public void testGetArrayRequiredNotPresent() {
		try {
			attributeMap.getRequiredArray("A bogus array", String[].class);
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetCollection() {
		LinkedList i = (LinkedList) attributeMap.getCollection("collection", List.class);
		assertEquals(0, i.size());
	}

	public void testGetCollectionNull() {
		LinkedList i = (LinkedList) attributeMap.getCollection("bogus", List.class);
		assertNull(i);
	}

	public void testGetCollectionRequired() {
		LinkedList i = (LinkedList) attributeMap.getRequiredCollection("collection", List.class);
		assertEquals(0, i.size());
	}

	public void testGetCollectionRequiredNotPresent() {
		try {
			attributeMap.getRequiredCollection("A bogus collection");
			fail("Should've failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetMap() {
		Map map = attributeMap.asMap();
		assertEquals(10, map.size());
	}

	public void testUnion() {
		LocalAttributeMap one = new LocalAttributeMap();
		one.put("foo", "bar");
		one.put("bar", "baz");

		LocalAttributeMap two = new LocalAttributeMap();
		two.put("cat", "coz");
		two.put("bar", "boo");

		AttributeMap three = one.union(two);
		assertEquals(3, three.size());
		assertEquals("bar", three.get("foo"));
		assertEquals("coz", three.get("cat"));
		assertEquals("boo", three.get("bar"));
	}

	public void testEquality() {
		LocalAttributeMap map = new LocalAttributeMap();
		map.put("foo", "bar");

		LocalAttributeMap map2 = new LocalAttributeMap();
		map2.put("foo", "bar");

		assertEquals(map, map2);
	}

}