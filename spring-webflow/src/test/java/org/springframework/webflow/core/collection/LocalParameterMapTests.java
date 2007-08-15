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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link LocalParameterMap}.
 */
public class LocalParameterMapTests extends TestCase {

	private LocalParameterMap parameterMap;

	public void setUp() {
		Map map = new HashMap();
		map.put("string", "A string");
		map.put("integer", "12345");
		map.put("boolean", "true");
		map.put("stringArray", new String[] { "1", "2", "3" });
		map.put("emptyArray", new String[0]);
		map.put("multipartFile", EasyMock.createMock(MultipartFile.class));
		parameterMap = new LocalParameterMap(map);
	}

	public void testSize() {
		assertTrue(!parameterMap.isEmpty());
		assertEquals(6, parameterMap.size());
	}

	public void testGet() {
		String value = parameterMap.get("string");
		assertEquals("A string", value);
	}

	public void testGetNull() {
		String value = parameterMap.get("bogus");
		assertNull(value);
	}

	public void testGetRequired() {
		String value = parameterMap.getRequired("string");
		assertEquals("A string", value);
	}

	public void testGetRequiredWithConversion() {
		Integer value = (Integer) parameterMap.getRequired("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	public void testGetRequiredNotPresent() {
		try {
			parameterMap.getRequired("bogus");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetWithDefaultOption() {
		String value = parameterMap.get("string", "default");
		assertEquals("A string", value);
	}

	public void testGetWithDefault() {
		String value = parameterMap.get("bogus", "default");
		assertEquals("default", value);
	}

	public void testGetWithDefaultAndConversion() {
		Object value = parameterMap.get("bogus", Integer.class, new Integer(1));
		assertEquals(new Integer(1), value);
	}

	public void testGetWithDefaultAndConversionNotAssignable() {
		try {
			parameterMap.get("bogus", Integer.class, "1");
			fail("'1' isn't a integer");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetArray() {
		String[] value = parameterMap.getArray("stringArray");
		assertEquals(3, value.length);
	}

	public void testGetEmptyArray() {
		String[] array = parameterMap.getArray("emptyArray");
		assertEquals(0, array.length);
	}

	public void testGetArrayNull() {
		String[] value = parameterMap.getArray("bogus");
		assertNull(value);
	}

	public void testGetArrayRequired() {
		String[] value = parameterMap.getRequiredArray("stringArray");
		assertEquals(3, value.length);
	}

	public void getArrayWithConversion() {
		Integer[] values = (Integer[]) parameterMap.getArray("stringArray", Integer.class);
		assertEquals(new Integer(1), values[0]);
		assertEquals(new Integer(2), values[1]);
		assertEquals(new Integer(3), values[2]);
	}

	public void testGetRequiredArrayNotPresent() {
		try {
			parameterMap.getRequiredArray("bogus");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testGetSingleValueAsArray() {
		String[] value = parameterMap.getArray("string");
		assertEquals(1, value.length);
		assertEquals("A string", value[0]);
	}

	public void testGetArrayAsSingleVaue() {
		String value = parameterMap.get("stringArray");
		assertEquals("1", value);
	}

	public void testGetEmptyArrayAsSingleVaue() {
		String value = parameterMap.get("emptyArray");
		assertEquals(null, value);
	}

	public void testGetConversion() {
		Integer i = parameterMap.getInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	public void testGetArrayConversion() {
		Integer[] i = (Integer[]) parameterMap.getArray("stringArray", Integer.class);
		assertEquals(i.length, 3);
		assertEquals(new Integer(1), i[0]);
		assertEquals(new Integer(2), i[1]);
		assertEquals(new Integer(3), i[2]);
	}

	public void getRequiredArrayWithConversion() {
		Integer[] values = (Integer[]) parameterMap.getRequiredArray("stringArray", Integer.class);
		assertEquals(new Integer(1), values[0]);
		assertEquals(new Integer(2), values[1]);
		assertEquals(new Integer(3), values[2]);
	}

	public void testGetNumber() {
		Integer value = (Integer) parameterMap.getNumber("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	public void testGetRequiredNumber() {
		Integer value = (Integer) parameterMap.getRequiredNumber("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	public void testGetNumberWithDefault() {
		Integer value = (Integer) parameterMap.getNumber("bogus", Integer.class, new Integer(12345));
		assertEquals(new Integer(12345), value);
	}

	public void testGetInteger() {
		Integer value = parameterMap.getInteger("integer");
		assertEquals(new Integer(12345), value);
	}

	public void testGetRequiredInteger() {
		Integer value = parameterMap.getRequiredInteger("integer");
		assertEquals(new Integer(12345), value);
	}

	public void testGetIntegerWithDefault() {
		Integer value = parameterMap.getInteger("bogus", new Integer(12345));
		assertEquals(new Integer(12345), value);
	}

	public void testGetLong() {
		Long value = parameterMap.getLong("integer");
		assertEquals(new Long(12345), value);
	}

	public void testGetRequiredLong() {
		Long value = parameterMap.getRequiredLong("integer");
		assertEquals(new Long(12345), value);
	}

	public void testGetLongWithDefault() {
		Long value = parameterMap.getLong("bogus", new Long(12345));
		assertEquals(new Long(12345), value);
	}

	public void testGetBoolean() {
		Boolean value = parameterMap.getBoolean("boolean");
		assertEquals(Boolean.TRUE, value);
	}

	public void testGetRequiredBoolean() {
		Boolean value = parameterMap.getRequiredBoolean("boolean");
		assertEquals(Boolean.TRUE, value);
	}

	public void testGetBooleanWithDefault() {
		Boolean value = parameterMap.getBoolean("bogus", Boolean.TRUE);
		assertEquals(Boolean.TRUE, value);
	}

	public void testGetMultipart() {
		MultipartFile file = parameterMap.getMultipartFile("multipartFile");
		assertNotNull(file);
	}

	public void testGetRequiredMultipart() {
		MultipartFile file = parameterMap.getRequiredMultipartFile("multipartFile");
		assertNotNull(file);
	}

	public void testEquality() {
		LocalParameterMap map1 = new LocalParameterMap(new HashMap(parameterMap.asMap()));
		assertEquals(parameterMap, map1);
	}

	public void testAsAttributeMap() {
		AttributeMap map = parameterMap.asAttributeMap();
		assertEquals(map.asMap(), parameterMap.asMap());
	}
}