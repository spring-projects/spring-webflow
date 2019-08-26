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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DispatchMethodInvokerTests {

	private MockClass mockClass;

	@BeforeEach
	public void setUp() {
		mockClass = new MockClass();
	}

	@Test
	public void testInvokeWithExplicitParameters() throws Exception {
		DispatchMethodInvoker invoker = new DispatchMethodInvoker(mockClass, Object.class);
		invoker.invoke("argumentMethod", "testValue");
		assertTrue(mockClass.getMethodCalled(), "Method should have been called successfully");
	}

	@Test
	public void testInvokeWithAssignableParameters() throws Exception {
		DispatchMethodInvoker invoker = new DispatchMethodInvoker(mockClass, String.class);
		invoker.invoke("argumentMethod", "testValue");
		assertTrue(mockClass.getMethodCalled(), "Method should have been called successfully");
	}

	@Test
	public void testInvokeWithNoParameters() throws Exception {
		DispatchMethodInvoker invoker = new DispatchMethodInvoker(mockClass);
		invoker.invoke("noArgumentMethod");
		assertTrue(mockClass.getMethodCalled(), "Method should have been called successfully");
	}

	@Test
	public void testInvokeWithException() {
		DispatchMethodInvoker invoker = new DispatchMethodInvoker(mockClass, Object.class);
		try {
			invoker.invoke("exceptionMethod", "testValue");
			fail("Should have thrown an exception");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unused")
	private class MockClass {
		private boolean methodCalled = false;

		public boolean getMethodCalled() {
			return methodCalled;
		}

		public void argumentMethod(Object o) {
			methodCalled = true;
		}

		public void noArgumentMethod() {
			methodCalled = true;
		}

		public void exceptionMethod(Object o) throws Exception {
			throw new Exception("expected exception");
		}
	}

}
