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
package org.springframework.webflow.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link LocalParameterMap}.
 */
public class LocalParameterMapTests {

	private LocalParameterMap parameterMap;

	@BeforeEach
	public void setUp() {
		Map<String, Object> map = new HashMap<>();
		map.put("string", "A string");
		map.put("integer", "12345");
		map.put("boolean", "true");
		map.put("stringArray", new String[] { "1", "2", "3" });
		map.put("emptyArray", new String[0]);
		map.put("multipartFile", EasyMock.createMock(MultipartFile.class));
		parameterMap = new LocalParameterMap(map);
	}

	@Test
	public void testSize() {
		assertTrue(!parameterMap.isEmpty());
		assertEquals(6, parameterMap.size());
	}

	@Test
	public void testGet() {
		String value = parameterMap.get("string");
		assertEquals("A string", value);
	}

	@Test
	public void testGetNull() {
		String value = parameterMap.get("bogus");
		assertNull(value);
	}

	@Test
	public void testGetRequired() {
		String value = parameterMap.getRequired("string");
		assertEquals("A string", value);
	}

	@Test
	public void testGetRequiredWithConversion() {
		Integer value = parameterMap.getRequired("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetRequiredNotPresent() {
		try {
			parameterMap.getRequired("bogus");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetWithDefaultOption() {
		String value = parameterMap.get("string", "default");
		assertEquals("A string", value);
	}

	@Test
	public void testGetWithDefault() {
		String value = parameterMap.get("bogus", "default");
		assertEquals("default", value);
	}

	@Test
	public void testGetWithDefaultAndConversion() {
		Object value = parameterMap.get("bogus", Integer.class, 1);
		assertEquals(1, value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetWithDefaultAndConversionNotAssignable() {
		try {
			parameterMap.get("bogus", (Class) Integer.class, "1");
			fail("'1' isn't a integer");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetArray() {
		String[] value = parameterMap.getArray("stringArray");
		assertEquals(3, value.length);
	}

	@Test
	public void testGetEmptyArray() {
		String[] array = parameterMap.getArray("emptyArray");
		assertEquals(0, array.length);
	}

	@Test
	public void testGetArrayNull() {
		String[] value = parameterMap.getArray("bogus");
		assertNull(value);
	}

	@Test
	public void testGetArrayRequired() {
		String[] value = parameterMap.getRequiredArray("stringArray");
		assertEquals(3, value.length);
	}

	public void getArrayWithConversion() {
		Integer[] values = parameterMap.getArray("stringArray", Integer.class);
		assertEquals(new Integer(1), values[0]);
		assertEquals(new Integer(2), values[1]);
		assertEquals(new Integer(3), values[2]);
	}

	@Test
	public void testGetRequiredArrayNotPresent() {
		try {
			parameterMap.getRequiredArray("bogus");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testGetSingleValueAsArray() {
		String[] value = parameterMap.getArray("string");
		assertEquals(1, value.length);
		assertEquals("A string", value[0]);
	}

	@Test
	public void testGetArrayAsSingleVaue() {
		String value = parameterMap.get("stringArray");
		assertEquals("1", value);
	}

	@Test
	public void testGetEmptyArrayAsSingleVaue() {
		String value = parameterMap.get("emptyArray");
		assertEquals(null, value);
	}

	@Test
	public void testGetConversion() {
		Integer i = parameterMap.getInteger("integer");
		assertEquals(new Integer(12345), i);
	}

	@Test
	public void testGetArrayConversion() {
		Integer[] i = parameterMap.getArray("stringArray", Integer.class);
		assertEquals(i.length, 3);
		assertEquals(new Integer(1), i[0]);
		assertEquals(new Integer(2), i[1]);
		assertEquals(new Integer(3), i[2]);
	}

	public void getRequiredArrayWithConversion() {
		Integer[] values = parameterMap.getRequiredArray("stringArray", Integer.class);
		assertEquals(new Integer(1), values[0]);
		assertEquals(new Integer(2), values[1]);
		assertEquals(new Integer(3), values[2]);
	}

	@Test
	public void testGetNumber() {
		Integer value = parameterMap.getNumber("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetRequiredNumber() {
		Integer value = parameterMap.getRequiredNumber("integer", Integer.class);
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetNumberWithDefault() {
		Integer value = parameterMap.getNumber("bogus", Integer.class, 12345);
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetInteger() {
		Integer value = parameterMap.getInteger("integer");
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetRequiredInteger() {
		Integer value = parameterMap.getRequiredInteger("integer");
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetIntegerWithDefault() {
		Integer value = parameterMap.getInteger("bogus", 12345);
		assertEquals(new Integer(12345), value);
	}

	@Test
	public void testGetLong() {
		Long value = parameterMap.getLong("integer");
		assertEquals(new Long(12345), value);
	}

	@Test
	public void testGetRequiredLong() {
		Long value = parameterMap.getRequiredLong("integer");
		assertEquals(new Long(12345), value);
	}

	@Test
	public void testGetLongWithDefault() {
		Long value = parameterMap.getLong("bogus", 12345L);
		assertEquals(new Long(12345), value);
	}

	@Test
	public void testGetBoolean() {
		Boolean value = parameterMap.getBoolean("boolean");
		assertEquals(Boolean.TRUE, value);
	}

	@Test
	public void testGetRequiredBoolean() {
		Boolean value = parameterMap.getRequiredBoolean("boolean");
		assertEquals(Boolean.TRUE, value);
	}

	@Test
	public void testGetBooleanWithDefault() {
		Boolean value = parameterMap.getBoolean("bogus", true);
		assertEquals(Boolean.TRUE, value);
	}

	@Test
	public void testGetMultipart() {
		MultipartFile file = parameterMap.getMultipartFile("multipartFile");
		assertNotNull(file);
	}

	@Test
	public void testGetRequiredMultipart() {
		MultipartFile file = parameterMap.getRequiredMultipartFile("multipartFile");
		assertNotNull(file);
	}

	@Test
	public void testEquality() {
		LocalParameterMap map1 = new LocalParameterMap(new HashMap<>(parameterMap.asMap()));
		assertEquals(parameterMap, map1);
	}

	@Test
	public void testAsAttributeMap() {
		AttributeMap<Object> map = parameterMap.asAttributeMap();
		assertEquals(map.asMap(), parameterMap.asMap());
	}
}
