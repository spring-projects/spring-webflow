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
package org.springframework.webflow.context.servlet;

import java.util.Iterator;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit test for the {@link HttpServletRequestParameterMap} class.
 * 
 * @author Ulrik Sandberg
 */
public class HttpServletRequestParameterMapTests extends TestCase {

	private HttpServletRequestParameterMap tested;

	private MockHttpServletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockHttpServletRequest();
		tested = new HttpServletRequestParameterMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		tested = null;
	}

	public void testGetAttribute() {
		request.setParameter("Some param", "Some value");
		// perform test
		Object result = tested.getAttribute("Some param");
		assertEquals("Some value", result);
	}

	public void testSetAttribute() {
		// perform test
		try {
			tested.setAttribute("Some key", "Some value");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException expected) {
			// expected
		}
	}

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

	public void testGetAttributeNames() {
		request.setParameter("Some param", "Some value");
		// perform test
		Iterator names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertTrue("More elements", names.hasNext());
		String name = (String) names.next();
		assertEquals("Some param", name);
	}
}