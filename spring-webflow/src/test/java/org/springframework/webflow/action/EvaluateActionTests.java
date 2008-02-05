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
package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit tests for {@link EvaluateAction}.
 * 
 * @author Jeremy Grelle
 */
public class EvaluateActionTests extends TestCase {

	private MockRequestContext context = new MockRequestContext();

	protected void setUp() throws Exception {
		context.getFlowScope().put("foo", "bar");
		context.getFlowScope().put("bean", new TestBean());
	}

	public void testEvaluateExpressionNoResult() throws Exception {
		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"));
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
		assertNull(context.getFlowScope().get("baz"));
	}

	public void testEvaluateExpressionResult_ScopeSpecfied() throws Exception {
		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{baz}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"), new ActionResultExposer(nameExpression,
				ScopeType.FLOW, null));
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
		assertEquals("bar", context.getFlowScope().get("baz"));
	}

	public void testBeanResult_ScopeSpecified() throws Exception {
		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{baz}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		TestBean bean = new TestBean();

		EvaluateAction action = new EvaluateAction(new StaticExpression(bean), new ActionResultExposer(nameExpression,
				ScopeType.FLOW, null));
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		assertEquals(bean, context.getFlowScope().get("baz"));
	}

	public void testStringResult_ScopeSpecifiedWithTypeConversion() throws Exception {
		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{baz}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		EvaluateAction action = new EvaluateAction(new StaticExpression("true"), new ActionResultExposer(
				nameExpression, ScopeType.FLOW, Boolean.class));
		Event result = action.execute(context);
		assertEquals("true", result.getId());
		assertEquals(Boolean.TRUE, context.getFlowScope().get("baz"));
	}

	public void testEvaluateExpressionResult_ScopeExpression() throws Exception {
		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{flowScope.baz}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"), new ActionResultExposer(nameExpression,
				null, null));
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
		assertEquals("bar", context.getFlowScope().get("baz"));
	}

	public void testEvaluateExpressionResult_ScopeSearch() throws Exception {
		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{baz.foo}", new ParserContextImpl()
				.eval(RequestContext.class));

		TestBean bean = new TestBean();
		context.getFlowScope().put("baz", bean);

		EvaluateAction action = new EvaluateAction(new StaticExpression("bar"), new ActionResultExposer(nameExpression,
				null, null));
		Event result = action.execute(context);
		assertEquals("bar", result.getId());
		assertEquals("bar", bean.getFoo());
	}

	public class TestBean {

		private String foo;

		public String getFoo() {
			return foo;
		}

		public void setFoo(String foo) {
			this.foo = foo;
		}
	}
}
