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
package org.springframework.binding.convert.support;

import java.util.Date;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;

/**
 * Test case for {@link ConversionExecutorImpl}.
 */
public class ConversionExecutorImplTests extends TestCase {

	private ConversionExecutorImpl conversionExecutor;

	protected void setUp() throws Exception {
		conversionExecutor = new ConversionExecutorImpl(String.class, Date.class, new TestTextToDate());
	}

	public void testTypeConversion() {
		assertTrue(conversionExecutor.execute("10-10-2008").getClass().equals(Date.class));
	}

	public void testAssignmentCompatibleTypeConversion() {
		java.sql.Date date = new java.sql.Date(123L);
		assertSame(date, conversionExecutor.execute(date));
	}

	public void testConvertNull() {
		assertNull(conversionExecutor.execute(null));
	}

	public void testIllegalType() {
		try {
			conversionExecutor.execute(new StringBuffer());
			fail();
		} catch (ConversionException e) {
			// expected
		}
	}

	private class TestTextToDate extends AbstractConverter {

		public Class[] getSourceClasses() {
			return new Class[] { String.class };
		}

		public Class[] getTargetClasses() {
			return new Class[] { Date.class };
		}

		protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
			return source == null ? null : new Date();
		}
	}

}
