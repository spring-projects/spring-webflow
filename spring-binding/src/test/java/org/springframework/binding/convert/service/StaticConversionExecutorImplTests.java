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
package org.springframework.binding.convert.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.converters.StringToDate;

public class StaticConversionExecutorImplTests {

	private StaticConversionExecutor conversionExecutor;

	@BeforeEach
	public void setUp() {
		StringToDate stringToDate = new StringToDate();
		conversionExecutor = new StaticConversionExecutor(String.class, Date.class, stringToDate);
	}

	@Test
	public void testTypeConversion() {
		assertTrue(conversionExecutor.execute("2008-10-10").getClass().equals(Date.class));
	}

	@Test
	public void testAssignmentCompatibleTypeConversion() {
		java.sql.Date date = new java.sql.Date(123L);
		try {
			assertSame(date, conversionExecutor.execute(date));
			fail("Should have failed");
		} catch (ConversionExecutionException e) {

		}
	}

	@Test
	public void testConvertNull() {
		assertNull(conversionExecutor.execute(null));
	}

	@Test
	public void testIllegalType() {
		try {
			conversionExecutor.execute(new StringBuilder());
			fail();
		} catch (ConversionExecutionException e) {
			// expected
		}
	}
}
