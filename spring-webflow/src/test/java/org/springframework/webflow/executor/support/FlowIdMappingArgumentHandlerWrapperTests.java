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
package org.springframework.webflow.executor.support;

import java.util.Collections;
import java.util.Properties;

import junit.framework.TestCase;

import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Test case for {@link FlowIdMappingArgumentHandlerWrapper}.
 * 
 * @author Erwin Vervaet
 */
public class FlowIdMappingArgumentHandlerWrapperTests extends TestCase {

	private FlowIdMappingArgumentHandlerWrapper argumentHandler;

	protected void setUp() throws Exception {
		this.argumentHandler = new FlowIdMappingArgumentHandlerWrapper();
		this.argumentHandler.setArgumentHandler(new RequestPathFlowExecutorArgumentHandler());
		Properties mappings = new Properties();
		mappings.setProperty("A", "X");
		mappings.setProperty("B", "Y");
		argumentHandler.setMappings(mappings);
		argumentHandler.addMapping("C", "X");
	}

	public void testMappingNoFallback() {
		argumentHandler.setFallback(false);

		assertTrue(argumentHandler.isFlowIdPresent(context("A")));
		assertEquals("X", argumentHandler.extractFlowId(context("A")));
		assertTrue(argumentHandler.isFlowIdPresent(context("B")));
		assertEquals("Y", argumentHandler.extractFlowId(context("B")));
		assertTrue(argumentHandler.isFlowIdPresent(context("C")));
		assertEquals("X", argumentHandler.extractFlowId(context("C")));
		assertFalse(argumentHandler.isFlowIdPresent(context("X")));
		assertFalse(argumentHandler.isFlowIdPresent(context("Y")));
		try {
			argumentHandler.extractFlowId(context("X"));
			fail();
		} catch (FlowExecutorArgumentExtractionException e) {
			// expected
		}
		try {
			argumentHandler.extractFlowId(context(""));
			fail();
		} catch (FlowExecutorArgumentExtractionException e) {
			// expected
		}
	}

	public void testMappingFallback() {
		argumentHandler.setFallback(true);

		assertTrue(argumentHandler.isFlowIdPresent(context("A")));
		assertEquals("X", argumentHandler.extractFlowId(context("A")));
		assertTrue(argumentHandler.isFlowIdPresent(context("B")));
		assertEquals("Y", argumentHandler.extractFlowId(context("B")));
		assertTrue(argumentHandler.isFlowIdPresent(context("C")));
		assertEquals("X", argumentHandler.extractFlowId(context("C")));
		assertTrue(argumentHandler.isFlowIdPresent(context("X")));
		assertEquals("X", argumentHandler.extractFlowId(context("X")));
		assertTrue(argumentHandler.isFlowIdPresent(context("Y")));
		assertEquals("Y", argumentHandler.extractFlowId(context("Y")));
		try {
			argumentHandler.extractFlowId(context(""));
			fail();
		} catch (FlowExecutorArgumentExtractionException e) {
			// expected
		}
	}

	public void testReverseMappingNoFallBack() {
		argumentHandler.setFallback(false);

		assertEquals("/app/flows/C", argumentHandler.createFlowDefinitionUrl(redirect("X"), context()));
		assertEquals("/app/flows/B", argumentHandler.createFlowDefinitionUrl(redirect("Y"), context()));

		try {
			argumentHandler.createFlowDefinitionUrl(redirect("Z"), context());
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testReverseMappingFallback() {
		argumentHandler.setFallback(true);

		assertEquals("/app/flows/C", argumentHandler.createFlowDefinitionUrl(redirect("X"), context()));
		assertEquals("/app/flows/B", argumentHandler.createFlowDefinitionUrl(redirect("Y"), context()));
		assertEquals("/app/flows/Z", argumentHandler.createFlowDefinitionUrl(redirect("Z"), context()));
	}

	public void testWithRequestParameters() {
		argumentHandler.setArgumentHandler(new RequestParameterFlowExecutorArgumentHandler());

		// mapping
		assertTrue(argumentHandler.isFlowIdPresent(contextWithParam("A")));
		assertEquals("X", argumentHandler.extractFlowId(contextWithParam("A")));

		// reverse mapping
		assertEquals("/app/flows?_flowId=C", argumentHandler.createFlowDefinitionUrl(redirect("X"), context()));
	}

	// internal helpers

	private MockExternalContext context() {
		return context("");
	}

	private MockExternalContext context(String flowId) {
		MockExternalContext context = new MockExternalContext();
		context.setContextPath("/app");
		context.setDispatcherPath("/flows");
		if (StringUtils.hasText(flowId)) {
			context.setRequestPathInfo("/" + flowId);
		}
		return context;
	}

	private MockExternalContext contextWithParam(String flowId) {
		MockExternalContext context = context();
		context.putRequestParameter("_flowId", flowId);
		return context;
	}

	private FlowDefinitionRedirect redirect(String flowId) {
		return new FlowDefinitionRedirect(flowId, Collections.EMPTY_MAP);
	}
}
