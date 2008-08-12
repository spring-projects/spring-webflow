/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.binding.convert.converters;

import junit.framework.TestCase;

/**
 * Test case for the default conversion service.
 * 
 * @author Scott Andrews
 */
public class StringToEnumTests extends TestCase {

	StringToEnum converter = new StringToEnum();

	public void testStringToEnum() throws Exception {
		TestEnum test = (TestEnum) converter.toObject("SUCCESS", TestEnum.class);
		assertEquals(TestEnum.SUCCESS, test);
	}

	public void testBadStringToEnum() throws Exception {
		try {
			converter.toObject("FAIL", TestEnum.class);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testEnumToString() throws Exception {
		String test = converter.toString(TestEnum.SUCCESS);
		assertEquals("SUCCESS", test);
	}

	enum TestEnum {
		SUCCESS;
	}

}
