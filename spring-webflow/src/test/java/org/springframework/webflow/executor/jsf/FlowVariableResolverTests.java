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
package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionKey;

/**
 * Unit tests for the FlowVariableResolver class.
 * 
 * @author Ulrik Sandberg
 */
public class FlowVariableResolverTests extends TestCase {

	private FlowVariableResolver tested;

	private TestableVariableResolver variableResolver;

	private MockFacesContext mockFacesContext;

	private MockJsfExternalContext mockJsfExternalContext;

	protected void setUp() throws Exception {
		super.setUp();
		mockFacesContext = new MockFacesContext();
		mockJsfExternalContext = new MockJsfExternalContext();
		mockFacesContext.setExternalContext(mockJsfExternalContext);
		variableResolver = new TestableVariableResolver();
		tested = new FlowVariableResolver(variableResolver);
	}

	public void testResolveVariableNotFlowScope() {
		Object result = tested.resolveVariable(mockFacesContext, "some name");
		assertTrue("not resolved using delegate", variableResolver.resolvedUsingDelegate);
		assertSame(variableResolver.expected, result);
	}

	public void testResolveVariableFlowScopeWithNoThreadLocal() {
		try {
			tested.resolveVariable(mockFacesContext, "flowScope");
			fail("EvaluationException expected");
		}
		catch (EvaluationException expected) {
			assertEquals(
					"'flowScope' variable prefix specified, but a FlowExecution is not bound to current thread context as it should be",
					expected.getMessage());
		}
		assertFalse("resolved using delegate", variableResolver.resolvedUsingDelegate);
	}

	public void testResolveVariableFlowScopeWithThreadLocal() {
		FlowExecution flowExecutionMock = (FlowExecution)EasyMock.createMock(FlowExecution.class);
		FlowExecutionKey key = null;
		FlowExecutionHolder holder = new FlowExecutionHolder(key, flowExecutionMock);
		FlowExecutionHolderUtils.setFlowExecutionHolder(holder, mockFacesContext);
		EasyMock.replay(new Object[] { flowExecutionMock });

		Object result = tested.resolveVariable(mockFacesContext, "flowScope");

		EasyMock.verify(new Object[] { flowExecutionMock });
		assertFalse("resolved using delegate", variableResolver.resolvedUsingDelegate);
		assertSame(flowExecutionMock, result);
	}

	private static class TestableVariableResolver extends VariableResolver {
		private boolean resolvedUsingDelegate;

		private Object expected = new Object();

		public Object resolveVariable(FacesContext arg0, String arg1) throws EvaluationException {
			resolvedUsingDelegate = true;
			return expected;
		}
	}
}
