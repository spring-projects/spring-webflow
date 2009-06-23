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
package org.springframework.webflow.context.portlet;

import java.util.Iterator;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.web.util.WebUtils;

/**
 * Unit test for the {@link PortletSessionMap} class.
 * 
 * @author Ulrik Sandberg
 * @author Scott Andrews
 */
public class PortletSessionMapTests extends TestCase {

	private PortletSessionMap tested;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		tested = new PortletSessionMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		tested = null;
	}

	public void testGetAttribute() {
		request.getPortletSession().setAttribute("Some key", "Some value");
		// perform test
		Object result = tested.getAttribute("Some key");
		assertEquals("Some value", result);
	}

	public void testGetAttributeNullSession() {
		request.setSession(null);
		// perform test
		Object result = tested.getAttribute("Some key");
		assertNull("No value expected", result);
	}

	public void testSetAttribute() {
		// perform test
		tested.setAttribute("Some key", "Some value");
		assertEquals("Some value", request.getPortletSession().getAttribute("Some key"));
	}

	public void testRemoveAttribute() {
		request.getPortletSession().setAttribute("Some key", "Some value");
		// perform test
		tested.removeAttribute("Some key");
		assertNull(request.getPortletSession().getAttribute("Some key"));
	}

	public void testRemoveAttributeNullSession() {
		request.setSession(null);
		// perform test
		tested.removeAttribute("Some key");
		assertNull(request.getPortletSession().getAttribute("Some key"));
	}

	public void testGetAttributeNames() {
		request.getPortletSession().setAttribute("Some key", "Some value");
		// perform test
		Iterator names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertTrue("More elements", names.hasNext());
		String name = (String) names.next();
		assertEquals("Some key", name);
	}

	public void testGetAttributeNamesNullSession() {
		request.setSession(null);
		// perform test
		Iterator names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertFalse("No elements expected", names.hasNext());
	}

	public void testGetSessionAsMutex() {
		Object mutex = tested.getMutex();
		assertSame(mutex, request.getPortletSession());
	}

	public void testGetSessionMutex() {
		Object object = new Object();
		request.getPortletSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, object);
		Object mutex = tested.getMutex();
		assertSame(mutex, object);
	}
}