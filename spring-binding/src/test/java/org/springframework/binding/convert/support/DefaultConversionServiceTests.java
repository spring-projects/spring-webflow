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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.Converter;

/**
 * Test case for the default conversion service.
 * 
 * @author Keith Donald
 */
public class DefaultConversionServiceTests extends TestCase {

	public void testConvertCompatibleTypes() {
		DefaultConversionService service = new DefaultConversionService();
		service.addAlias("list", List.class);

		List lst = new ArrayList();
		assertSame(lst, service.getConversionExecutor(ArrayList.class, List.class).execute(lst));
		assertSame(lst, service.getConversionExecutorByTargetAlias(ArrayList.class, "list").execute(lst));

		try {
			service.getConversionExecutor(List.class, ArrayList.class);
			fail();
		} catch (ConversionException e) {
			// expected
		}
	}

	public void testOverrideConverter() {
		Converter customConverter = new TextToBoolean("ja", "nee");

		DefaultConversionService service = new DefaultConversionService();

		ConversionExecutorImpl executor = (ConversionExecutorImpl) service.getConversionExecutor(String.class,
				Boolean.class);
		assertNotSame(customConverter, executor.getConverter());
		try {
			executor.execute("ja");
			fail();
		} catch (ConversionException e) {
			// expected
		}

		service.addConverter(customConverter);

		executor = (ConversionExecutorImpl) service.getConversionExecutor(String.class, Boolean.class);
		assertSame(customConverter, executor.getConverter());
		assertTrue(((Boolean) executor.execute("ja")).booleanValue());
	}

	public void testTargetClassNotSupported() {
		DefaultConversionService service = new DefaultConversionService();
		try {
			service.getConversionExecutor(String.class, HashMap.class);
			fail("Should have thrown an exception");
		} catch (ConversionException e) {
		}
	}

	public void testValidConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer.class);
		Integer three = (Integer) executor.execute("3");
		assertEquals(3, three.intValue());
	}
}