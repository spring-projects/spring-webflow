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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.test.MockRequestContext;

public class FlowDefinitionRedirectSelectorTests extends TestCase {
	ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser();

	public void testMakeSelection() {
		Expression exp = parser.parseExpression("${requestScope.flowIdVar}?a=b&c=${requestScope.bar}");
		FlowDefinitionRedirectSelector selector = new FlowDefinitionRedirectSelector(exp);
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("flowIdVar", "foo");
		context.getRequestScope().put("bar", "baz");
		ViewSelection selection = selector.makeEntrySelection(context);
		assertTrue(selection instanceof FlowDefinitionRedirect);
		FlowDefinitionRedirect redirect = (FlowDefinitionRedirect)selection;
		assertEquals("foo", redirect.getFlowDefinitionId());
		assertEquals("b", redirect.getExecutionInput().get("a"));
		assertEquals("baz", redirect.getExecutionInput().get("c"));
	}
	
	public void testMakeSelectionInvalidVariable() {
		Expression exp = parser.parseExpression("${flowScope.flowId}");
		FlowDefinitionRedirectSelector selector = new FlowDefinitionRedirectSelector(exp);
		MockRequestContext context = new MockRequestContext();
		try {
			ViewSelection selection = selector.makeEntrySelection(context);
			assertTrue(selection instanceof FlowDefinitionRedirect);
		} catch (IllegalStateException e) {
			
		}
	}
}