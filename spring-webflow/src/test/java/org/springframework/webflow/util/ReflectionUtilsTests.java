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
package org.springframework.webflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Test case for {@link org.springframework.webflow.util.ReflectionUtils}.
 * 
 * @author Erwin Vervaet
 */
public class ReflectionUtilsTests extends TestCase {

	public void testInvokeStatic() throws Exception {
		Method currentTimeMillis = System.class.getMethod("currentTimeMillis", null);
		Object res = ReflectionUtils.invokeMethod(currentTimeMillis, null);
		assertNotNull(res);
		assertTrue(res instanceof Long);
	}

	public void testInvoke() throws Exception {
		Method substring = String.class.getMethod("substring", new Class[] { Integer.TYPE });
		Object res = ReflectionUtils.invokeMethod(substring, "abc123", new Object[] { new Integer(3) });
		assertNotNull(res);
		assertTrue(res instanceof String);
		assertEquals("123", res);
	}

	public void testInvokeProblem() throws Exception {
		Method substring = String.class.getMethod("substring", new Class[] { Integer.TYPE });
		try {
			ReflectionUtils.invokeMethod(substring, new Date());
			fail();
		} catch (RuntimeException e) {
		}

		try {
			ReflectionUtils.invokeMethod(substring, "abc");
		} catch (RuntimeException e) {
		}
	}

	public void testInvokeRuntimeException() throws Exception {
		Method substring = String.class.getMethod("substring", new Class[] { Integer.TYPE });
		try {
			ReflectionUtils.invokeMethod(substring, "abc", new Object[] { new Integer(10) });
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void testInvokeCheckedException() throws Exception {
		Method m = ReflectionUtilsTests.class.getMethod("methodThatThrowsCheckedException", null);
		try {
			ReflectionUtils.invokeMethod(m, null);
			fail();
		} catch (RuntimeException e) {
		}
	}

	public static void methodThatThrowsCheckedException() throws IOException {
		new FileInputStream(new File("bogus"));
	}
}
