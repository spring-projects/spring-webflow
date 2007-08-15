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
package org.springframework.webflow.context.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.core.collection.AttributeMapBindingEvent;
import org.springframework.webflow.core.collection.AttributeMapBindingListener;

/**
 * Unit tests for {@link HttpSessionMapBindingListener}.
 * 
 * @author Erwin Vervaet
 */
public class HttpSessionMapBindingListenerTests extends TestCase {

	private HttpServletRequest request;
	private HttpSession session;
	private TestAttributeMapBindingListener value;

	protected void setUp() throws Exception {
		request = new MockHttpServletRequest();
		session = request.getSession(true);
		value = new TestAttributeMapBindingListener();
	}

	public void testValueBoundUnBound() {
		value.valueBoundEvent = null;
		value.valueUnboundEvent = null;
		session.setAttribute("key", new HttpSessionMapBindingListener(value, new HttpSessionMap(request)));
		assertNotNull(value.valueBoundEvent);
		assertNull(value.valueUnboundEvent);
		value.valueBoundEvent = null;
		value.valueUnboundEvent = null;
		session.removeAttribute("key");
		assertNull(value.valueBoundEvent);
		assertNotNull(value.valueUnboundEvent);
	}

	private static class TestAttributeMapBindingListener implements AttributeMapBindingListener {

		public AttributeMapBindingEvent valueBoundEvent;
		public AttributeMapBindingEvent valueUnboundEvent;

		public void valueBound(AttributeMapBindingEvent event) {
			this.valueBoundEvent = event;
			assertEquals("key", event.getAttributeName());
			assertSame(event.getAttributeValue(), this);
		}

		public void valueUnbound(AttributeMapBindingEvent event) {
			this.valueUnboundEvent = event;
			assertEquals("key", event.getAttributeName());
			assertSame(event.getAttributeValue(), this);
		}
	}
}
