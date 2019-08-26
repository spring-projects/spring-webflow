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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.WebUtils;

/**
 * Unit test for the {@link HttpSessionMap} class.
 * 
 * @author Ulrik Sandberg
 */
public class HttpSessionMapTests {

	private HttpSessionMap tested;

	private MockHttpServletRequest request;

	@BeforeEach
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		tested = new HttpSessionMap(request);
	}

	@AfterEach
	public void tearDown() throws Exception {
		request = null;
		tested = null;
	}

	@Test
	public void testGetAttribute() {
		request.getSession().setAttribute("Some key", "Some value");
		// perform test
		Object result = tested.getAttribute("Some key");
		assertEquals("Some value", result);
	}

	@Test
	public void testGetAttributeNullSession() {
		request.setSession(null);
		// perform test
		Object result = tested.getAttribute("Some key");
		assertNull(result, "No value expected");
	}

	@Test
	public void testSetAttribute() {
		// perform test
		tested.setAttribute("Some key", "Some value");
		assertEquals("Some value", request.getSession().getAttribute("Some key"));
	}

	@Test
	public void testRemoveAttribute() {
		request.getSession().setAttribute("Some key", "Some value");
		// perform test
		tested.removeAttribute("Some key");
		assertNull(request.getSession().getAttribute("Some key"));
	}

	@Test
	public void testRemoveAttributeNullSession() {
		request.setSession(null);
		// perform test
		tested.removeAttribute("Some key");
		assertNull(request.getSession().getAttribute("Some key"));
	}

	@Test
	public void testGetAttributeNames() {
		request.getSession().setAttribute("Some key", "Some value");
		// perform test
		Iterator<String> names = tested.getAttributeNames();
		assertNotNull(names, "Null result unexpected");
		assertTrue(names.hasNext(), "More elements");
		String name = names.next();
		assertEquals("Some key", name);
	}

	@Test
	public void testGetAttributeNamesNullSession() {
		request.setSession(null);
		// perform test
		Iterator<String> names = tested.getAttributeNames();
		assertNotNull(names, "Null result unexpected");
		assertFalse(names.hasNext(), "No elements expected");
	}

	@Test
	public void testGetSessionAsMutex() {
		Object mutex = tested.getMutex();
		assertSame(mutex, request.getSession());
	}

	@Test
	public void testGetSessionMutex() {
		Object object = new Object();
		request.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, object);
		Object mutex = tested.getMutex();
		assertSame(mutex, object);
	}
}
