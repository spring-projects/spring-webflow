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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.support.StaticExpression;

/**
 * Unit tests for {@link org.springframework.binding.method.MethodInvoker}.
 * 
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public class MethodInvokerTests {

	private MethodInvoker methodInvoker;

	@BeforeEach
	public void setUp() {
		this.methodInvoker = new MethodInvoker();
	}

	@Test
	public void testInvocationTargetException() {
		try {
			methodInvoker.invoke(new MethodSignature("test"), new TestObject(), null);
			fail();
		} catch (MethodInvocationException e) {
			assertTrue(e.getTargetException() instanceof IllegalArgumentException);
			assertEquals("just testing", e.getTargetException().getMessage());
		}
	}

	@Test
	public void testInvalidMethod() {
		try {
			methodInvoker.invoke(new MethodSignature("bogus"), new TestObject(), null);
			fail();
		} catch (MethodInvocationException e) {
			assertTrue(e.getTargetException() instanceof InvalidMethodKeyException);
		}
	}

	@Test
	public void testBeanArg() {
		Parameters parameters = new Parameters();
		Bean bean = new Bean();
		parameters.add(new Parameter(Bean.class, new StaticExpression(bean)));
		MethodSignature method = new MethodSignature("testBeanArg", parameters);
		assertSame(bean, methodInvoker.invoke(method, new TestObject(), null));
	}

	@Test
	public void testPrimitiveArg() {
		Parameters parameters = new Parameters();
		parameters.add(new Parameter(Boolean.class, new StaticExpression(true)));
		MethodSignature method = new MethodSignature("testPrimitiveArg", parameters);
		assertEquals(Boolean.TRUE, methodInvoker.invoke(method, new TestObject(), null));
	}

	static class TestObject {

		public void test() {
			throw new IllegalArgumentException("just testing");
		}

		public Object testBeanArg(Bean bean) {
			return bean;
		}

		public boolean testPrimitiveArg(boolean primitive) {
			return primitive;
		}
	}

	static class Bean {
		String value;
	}
}
