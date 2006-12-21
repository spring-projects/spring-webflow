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
package org.springframework.webflow.execution.support;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests for {@link FlowDefinitionRedirect}.
 */
public class FlowDefinitionRedirectTests extends TestCase {

	public void testConstructAndAccess() {
		Map input = new HashMap();
		input.put("name", "value");
		FlowDefinitionRedirect redirect = new FlowDefinitionRedirect("foo", input);
		assertEquals("foo", redirect.getFlowDefinitionId());
		assertEquals(1, redirect.getExecutionInput().size());
		assertEquals("value", redirect.getExecutionInput().get("name"));
		try {
			redirect.getExecutionInput().put("foo", "bar");
		} catch (UnsupportedOperationException e) {
			
		}
	}
	
	public void testNullParams() {
		try {
			new FlowDefinitionRedirect(null, null);
			fail("was null");
		} catch (IllegalArgumentException e) {
			
		}

	}
	
	public void testMapLookup() {
		FlowDefinitionRedirect redirect = new FlowDefinitionRedirect("foo", null);
		Map map = new HashMap();
		map.put("redirect", redirect);
		assertSame(redirect, map.get("redirect"));
	}
}