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
package org.springframework.webflow.context.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit test for the {@link HttpServletRequestParameterMap} class.
 * 
 * @author Ulrik Sandberg
 */
public class HttpServletRequestParameterMapTests {

	private HttpServletRequestParameterMap tested;

	private MockHttpServletRequest request;

	@BeforeEach
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		tested = new HttpServletRequestParameterMap(request);
	}

	@AfterEach
	public void tearDown() throws Exception {
		request = null;
		tested = null;
	}

	@Test
	public void testGetAttribute() {
		request.setParameter("Some param", "Some value");
		// perform test
		Object result = tested.getAttribute("Some param");
		assertEquals("Some value", result);
	}

	@Test
	public void testSetAttribute() {
		// perform test
		try {
			tested.setAttribute("Some key", "Some value");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException expected) {
			// expected
		}
	}

	@Test
	public void testRemoveAttribute() {
		request.setParameter("Some param", "Some value");
		// perform test
		try {
			tested.removeAttribute("Some param");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException expected) {
			// expected
		}
	}

	@Test
	public void testGetAttributeNames() {
		request.setParameter("Some param", "Some value");
		// perform test
		Iterator<String> names = tested.getAttributeNames();
		assertNotNull(names, "Null result unexpected");
		assertTrue(names.hasNext(), "More elements");
		String name = names.next();
		assertEquals("Some param", name);
	}
}
