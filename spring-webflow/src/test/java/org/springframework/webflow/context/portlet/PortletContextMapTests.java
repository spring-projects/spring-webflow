/*
 * Copyright 2002-2006 the original author or authors.
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

import org.springframework.mock.web.portlet.MockPortletContext;

/**
 * Unit test for the {@link PortletContextMap} class.
 * 
 * @author Ulrik Sandberg
 */
public class PortletContextMapTests extends TestCase {

	private PortletContextMap tested;

	private MockPortletContext context;

	protected void setUp() throws Exception {
		super.setUp();
		context = new MockPortletContext();
		tested = new PortletContextMap(context);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		context = null;
		tested = null;
	}

	public void testGetAttribute() {
		context.setAttribute("Some key", "Some value");
		// perform test
		Object result = tested.getAttribute("Some key");
		assertEquals("Some value", result);
	}

	public void testSetAttribute() {
		// perform test
		tested.setAttribute("Some key", "Some value");
		assertEquals("Some value", context.getAttribute("Some key"));
	}

	public void testRemoveAttribute() {
		context.setAttribute("Some key", "Some value");
		// perform test
		tested.removeAttribute("Some key");
		assertNull(context.getAttribute("Some key"));
	}

	public void testGetAttributeNames() {
		context.setAttribute("Some key", "Some value");
		context.removeAttribute("javax.servlet.context.tempdir");
		// perform test
		Iterator names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertTrue("More elements", names.hasNext());
		String name = (String)names.next();
		assertEquals("Some key", name);
	}
}