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
package org.springframework.binding.method;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

/**
 * @author Rob Harrop
 * @since 1.0
 */
public class MethodKeyTests {

	private static final Method LIST_NO_ARGS = safeGetMethod(File.class, "list", null);

	private static final Method LIST_FILENAME_FILTER = safeGetMethod(File.class, "list",
			new Class[] { FilenameFilter.class });

	@Test
	public void testGetMethodWithNoArgs() {
		MethodKey key = new MethodKey(File.class, "list");
		Method m = key.getMethod();
		assertEquals(LIST_NO_ARGS, m);
	}

	@Test
	public void testGetMoreGenericMethod() {
		MethodKey key = new MethodKey(Object.class, "equals", Long.class);
		assertEquals(safeGetMethod(Object.class, "equals", new Class[] { Object.class }), key.getMethod());
	}

	@Test
	public void testGetMethodWithSingleArg() {
		MethodKey key = new MethodKey(File.class, "list", FilenameFilter.class);
		Method m = key.getMethod();
		assertEquals(LIST_FILENAME_FILTER, m);
	}

	@Test
	public void testGetMethodWithSingleNullArgAndValidMatch() {
		MethodKey key = new MethodKey(File.class, "list", new Class[] { null });
		Method m = key.getMethod();
		assertEquals(LIST_FILENAME_FILTER, m);
	}

	@Test
	public void testGetMethodWithSingleNullAndUnclearMatch() {
		new MethodKey(File.class, "listFiles", new Class[] { null });
	}

	private static Method safeGetMethod(Class<?> type, String name, Class<?>[] argTypes) {
		try {
			return type.getMethod(name, argTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Unable to safely access a known method via reflection. " + e.getMessage());
		}
	}
}
