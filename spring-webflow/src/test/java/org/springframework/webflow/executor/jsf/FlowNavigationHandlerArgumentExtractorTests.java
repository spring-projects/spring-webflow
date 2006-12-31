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
package org.springframework.webflow.executor.jsf;

import junit.framework.TestCase;

import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractionException;

public class FlowNavigationHandlerArgumentExtractorTests extends TestCase {
	
	private FlowNavigationHandlerArgumentExtractor extractor = new FlowNavigationHandlerArgumentExtractor();

	public void testExtractFlowId() {
		JsfExternalContext context = new JsfExternalContext(new MockFacesContext(), "action", "flowId:foo");
		String flowId = extractor.extractFlowId(context);
		assertEquals("Wrong flow id", "foo", flowId);
	}

	public void testExtractFlowIdWrongFormat() {
		JsfExternalContext context = new JsfExternalContext(new MockFacesContext(), "action", "flow:foo");
		try {
			extractor.extractFlowId(context);
			fail();
		}
		catch (FlowExecutorArgumentExtractionException e) {
			// expected
		}
	}

	public void testExtractEventId() {
		JsfExternalContext context = new JsfExternalContext(new MockFacesContext(), "action", "submit");
		String eventId = extractor.extractEventId(context);
		assertEquals("Wrong event id", "submit", eventId);
	}
}