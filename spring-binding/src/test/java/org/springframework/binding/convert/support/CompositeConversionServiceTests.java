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

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;

/**
 * Test case for the {@link CompositeConversionService}.
 * 
 * @author Erwin Vervaet
 */
public class CompositeConversionServiceTests extends TestCase {

	private CompositeConversionService service;

	protected void setUp() throws Exception {
		GenericConversionService first = new GenericConversionService();
		first.addConverter(new TextToClass());
		first.addConverter(new TextToBoolean("ja", "nee"));
		first.addDefaultAlias(Boolean.class);
		GenericConversionService second = new GenericConversionService();
		second.addConverter(new TextToNumber());
		second.addConverter(new TextToBoolean());
		second.addDefaultAlias(Integer.class);
		service = new CompositeConversionService(new ConversionService[] { first, second });
	}

	public void testGetConversionExecutor() {
		assertNotNull(service.getConversionExecutor(String.class, Class.class));
		assertNotNull(service.getConversionExecutor(String.class, Boolean.class));
		assertEquals(Boolean.TRUE, service.getConversionExecutor(String.class, Boolean.class).execute("ja"));
		assertNotNull(service.getConversionExecutor(String.class, Integer.class));
		try {
			service.getConversionExecutor(String.class, Date.class);
			fail();
		} catch (ConversionException e) {
			// expected
		}
	}

	public void testGetConversionExecutorByTargetAlias() {
		assertNotNull(service.getConversionExecutorByTargetAlias(String.class, "boolean"));
		assertEquals(Boolean.TRUE, service.getConversionExecutorByTargetAlias(String.class, "boolean").execute("ja"));
		assertNotNull(service.getConversionExecutorByTargetAlias(String.class, "integer"));
		assertNull(service.getConversionExecutorByTargetAlias(String.class, "class"));
	}

	public void testGetConversionExecutorsForSource() {
		assertEquals(new TextToClass().getTargetClasses().length + new TextToBoolean().getTargetClasses().length
				+ new TextToNumber().getTargetClasses().length,
				service.getConversionExecutorsForSource(String.class).length);
		assertEquals(0, service.getConversionExecutorsForSource(Date.class).length);
		ConversionExecutor[] fromStringConversionExecutors = service.getConversionExecutorsForSource(String.class);
		ConversionExecutorImpl booleanConversionExecutor = null;
		for (int i = 0; i < fromStringConversionExecutors.length; i++) {
			if (((ConversionExecutorImpl) fromStringConversionExecutors[i]).getConverter() instanceof TextToBoolean) {
				booleanConversionExecutor = (ConversionExecutorImpl) fromStringConversionExecutors[i];
			}
		}
		assertEquals(Boolean.TRUE, booleanConversionExecutor.execute("ja"));
	}

	public void testGetClassByAlias() {
		assertEquals(Boolean.class, service.getClassByAlias("boolean"));
		assertEquals(Integer.class, service.getClassByAlias("integer"));
		assertNull(service.getClassByAlias("class"));
	}
}
