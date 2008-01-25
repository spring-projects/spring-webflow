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
package org.springframework.binding.method;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;

/**
 * Unit tests for {@link org.springframework.binding.method.MethodInvoker}.
 * 
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public class MethodInvokerTests extends TestCase {

	private MethodInvoker methodInvoker;

	protected void setUp() throws Exception {
		this.methodInvoker = new MethodInvoker();
	}

	public void testInvocationTargetException() {
		try {
			methodInvoker.invoke(new MethodSignature("test"), new TestObject(), null);
			fail();
		} catch (MethodInvocationException e) {
			assertTrue(e.getTargetException() instanceof IllegalArgumentException);
			assertEquals("just testing", e.getTargetException().getMessage());
		}
	}

	public void testInvalidMethod() {
		try {
			methodInvoker.invoke(new MethodSignature("bogus"), new TestObject(), null);
			fail();
		} catch (MethodInvocationException e) {
			assertTrue(e.getTargetException() instanceof InvalidMethodKeyException);
		}
	}

	public void testBeanArg() {
		Parameters parameters = new Parameters();
		Bean bean = new Bean();
		parameters.add(new Parameter(Bean.class, new StaticExpression(bean)));
		MethodSignature method = new MethodSignature("testBeanArg", parameters, null);
		assertSame(bean, methodInvoker.invoke(method, new TestObject(), null));
	}

	public void testPrimitiveArg() {
		Parameters parameters = new Parameters();
		parameters.add(new Parameter(Boolean.class, new StaticExpression(Boolean.TRUE)));
		MethodSignature method = new MethodSignature("testPrimitiveArg", parameters, null);
		assertEquals(Boolean.TRUE, methodInvoker.invoke(method, new TestObject(), null));
	}

	public void testResultConversion() {
		Parameters parameters = Parameters.NONE;
		MethodSignature method = new MethodSignature("testConvertResult", parameters, Class.class);
		assertEquals(Object.class, methodInvoker.invoke(method, new TestObject(), null));
	}

	private static class TestObject {

		public void test() {
			throw new IllegalArgumentException("just testing");
		}

		public Object testBeanArg(Bean bean) {
			return bean;
		}

		public String testConvertResult() {
			return "java.lang.Object";
		}

		public boolean testPrimitiveArg(boolean primitive) {
			return primitive;
		}
	}

	private static class Bean {
		String value;
	}
}
