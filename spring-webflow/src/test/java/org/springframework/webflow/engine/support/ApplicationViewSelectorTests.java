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
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockFlowExecutionContext;
import org.springframework.webflow.test.MockRequestContext;

public class ApplicationViewSelectorTests extends TestCase {
	ExpressionParser parser = DefaultExpressionParserFactory.getExpressionParser();

	public void testMakeSelection() {
		Expression exp = parser.parseExpression("${requestScope.viewVar}");
		ApplicationViewSelector selector = new ApplicationViewSelector(exp);
		MockRequestContext context = new MockRequestContext();
		context.getRequestScope().put("viewVar", "view");
		context.getRequestScope().put("foo", "bar");
		context.getFlowScope().put("foo", "bar2");
		context.getFlowScope().put("foo2", "bar");
		context.getConversationScope().put("foo", "bar3");
		context.getConversationScope().put("foo3", "bar");
		ViewSelection selection = selector.makeEntrySelection(context);
		assertTrue(selection instanceof ApplicationView);
		ApplicationView view = (ApplicationView)selection;
		assertEquals("view", view.getViewName());
		assertEquals("bar", view.getModel().get("foo"));
		assertEquals("bar", view.getModel().get("foo2"));
		assertEquals("bar", view.getModel().get("foo3"));
	}

	public void testMakeNullSelection() {
		ApplicationViewSelector selector = new ApplicationViewSelector(new StaticExpression(null));
		MockRequestContext context = new MockRequestContext();
		try {
			selector.makeEntrySelection(context);
			fail();
		}
		catch (IllegalStateException e) {
			//expected
		}
	}

	public void testMakeNullSelectionEmptyString() {
		ApplicationViewSelector selector = new ApplicationViewSelector(new StaticExpression(""));
		MockRequestContext context = new MockRequestContext();
		try {
			selector.makeEntrySelection(context);
			fail();
		}
		catch (IllegalStateException e) {
			//expected
		}
	}

	public void testIsEntrySelectionRenderable() {
		ApplicationViewSelector selector = new ApplicationViewSelector(new StaticExpression(null));
		MockRequestContext context = new MockRequestContext();
		assertTrue(selector.isEntrySelectionRenderable(context));
	}

	public void testIsEntrySelectionRenderableRedirect() {
		ApplicationViewSelector selector = new ApplicationViewSelector(new StaticExpression(null), true);
		MockRequestContext context = new MockRequestContext();
		assertFalse(selector.isEntrySelectionRenderable(context));
	}

	public void testIsEntrySelectionRenderableAlwaysRedirectOnPause() {
		ApplicationViewSelector selector = new ApplicationViewSelector(new StaticExpression(null));
		MockRequestContext requestContext = new MockRequestContext();
		MockFlowExecutionContext flowExecutionContext = new MockFlowExecutionContext();
		flowExecutionContext.putAttribute("alwaysRedirectOnPause", Boolean.TRUE);
		requestContext.setFlowExecutionContext(flowExecutionContext);
		assertFalse(selector.isEntrySelectionRenderable(requestContext));
	}
}