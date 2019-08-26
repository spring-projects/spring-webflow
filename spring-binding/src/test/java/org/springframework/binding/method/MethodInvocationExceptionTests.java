/*
 * Copyright 2004-2008 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MethodInvocationException}.
 * 
 * @author Erwin Vervaet
 */
public class MethodInvocationExceptionTests {

	@Test
	public void testGetTargetException() {
		// runtime exception
		IllegalArgumentException iae = new IllegalArgumentException("test");
		MethodInvocationException ex = testException(iae);
		assertSame(iae, ex.getTargetException());

		// exception
		IOException ioe = new IOException("test");
		ex = testException(ioe);
		assertSame(ioe, ex.getTargetException());

		// nested
		InvocationTargetException ite = new InvocationTargetException(ioe);
		ex = testException(ite);
		assertSame(ioe, ex.getTargetException());

		// deep nesting
		ite = new InvocationTargetException(new InvocationTargetException(ioe));
		ex = testException(ite);
		assertSame(ioe, ex.getTargetException());
	}

	// internal helpers

	private MethodInvocationException testException(Throwable cause) {
		return new MethodInvocationException(new MethodSignature("test"), null, cause);
	}
}
