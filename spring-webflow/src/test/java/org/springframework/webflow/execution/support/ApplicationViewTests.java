/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.execution.support;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ApplicationView}.
 */
public class ApplicationViewTests extends TestCase {

	public void testConstructAndAccess() {
		Map model = new HashMap();
		model.put("name", "value");
		ApplicationView view = new ApplicationView("view", model);
		assertEquals("view", view.getViewName());
		assertEquals(1, view.getModel().size());
		assertEquals("value", model.get("name"));
		try {
			view.getModel().put("foo", "bar");
		} catch (UnsupportedOperationException e) {
			
		}
	}
	
	public void testNullParams() {
		ApplicationView view = new ApplicationView(null, null);
		assertEquals(0, view.getModel().size());
		assertEquals(null, view.getViewName());
		ApplicationView view2 = new ApplicationView(null, null);
		assertEquals(view, view2);
	}
	
	public void testMapLookup() {
		ApplicationView view = new ApplicationView("view", null);
		Map map = new HashMap();
		map.put("view", view);
		assertSame(view, map.get("view"));
	}
}
