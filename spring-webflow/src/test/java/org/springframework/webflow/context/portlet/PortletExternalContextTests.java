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

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockPortletResponse;

/**
 * Unit tests for {@link PortletExternalContext}.
 */
public class PortletExternalContextTests extends TestCase {
	
	private PortletExternalContext context = new PortletExternalContext(new MockPortletContext(),
			new MockPortletRequest(), new MockPortletResponse());
	
	public void testApplicationMap() {
		assertEquals(1, context.getApplicationMap().size());
		context.getApplicationMap().put("foo", "bar");
		assertEquals("bar", context.getApplicationMap().get("foo"));
		assertEquals("bar", context.getContext().getAttribute("foo"));
	}

	public void testSessionMap() {
		assertEquals(0, context.getSessionMap().size());
		context.getSessionMap().put("foo", "bar");
		assertEquals("bar", context.getSessionMap().get("foo"));
		assertEquals("bar", context.getRequest().getPortletSession().getAttribute("foo"));
	}

	public void testRequestMap() {
		assertEquals(0, context.getRequestMap().size());
		context.getRequestMap().put("foo", "bar");
		assertEquals("bar", context.getRequestMap().get("foo"));
		assertEquals("bar", context.getRequest().getAttribute("foo"));
	}
	
	public void testOther() {
		assertNull(context.getRequestPathInfo());
		assertNull(context.getDispatcherPath());
		assertNotNull(context.getResponse());
	}
}
