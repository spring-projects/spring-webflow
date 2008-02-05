package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ognl.OgnlExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.expression.WebFlowOgnlExpressionParser;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

public class SetActionTests extends TestCase {

	public void testExecute_AttrExpression_ScopeSpecified() throws Exception {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

		Expression attributeExpression = parser.parseExpression("#{foo}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));
		ScopeType scope = ScopeType.REQUEST;
		Expression valueExpression = parser.parseExpression(valueToSet, new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, scope, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExecute_ScopeExpression() throws Exception {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

		Expression attributeExpression = parser.parseExpression("#{requestScope.foo}", new ParserContextImpl()
				.eval(RequestContext.class));
		ScopeType scope = null;
		Expression valueExpression = parser.parseExpression(valueToSet, new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, scope, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExecute_SearchExpression() throws Exception {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

		Expression attributeExpression = parser.parseExpression("#{bean.foo}", new ParserContextImpl()
				.eval(RequestContext.class));
		ScopeType scope = null;
		Expression valueExpression = parser.parseExpression(valueToSet, new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, scope, valueExpression);

		RequestContext context = new MockRequestContext();
		TestBean bean = new TestBean();
		context.getRequestScope().put("bean", bean);

		action.execute(context);

		assertEquals(valueToSet, bean.getFoo());
	}

	public void testExecute_OGNL_ScopeSpecified() throws Exception {

		String valueToSet = "myValue";

		OgnlExpressionParser parser = new WebFlowOgnlExpressionParser();

		Expression attributeExpression = parser.parseExpression("${foo}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));
		ScopeType scope = ScopeType.REQUEST;
		Expression valueExpression = parser.parseExpression(valueToSet, new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, scope, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExecute_OGNL_ScopeExpression() throws Exception {

		String valueToSet = "myValue";

		OgnlExpressionParser parser = new WebFlowOgnlExpressionParser();

		Expression attributeExpression = parser.parseExpression("${requestScope.foo}", new ParserContextImpl()
				.eval(RequestContext.class));
		Expression valueExpression = parser.parseExpression(valueToSet, new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, null, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExecute_LegacyOGNL_ScopeSpecified() throws Exception {

		String valueToSet = "myValue";

		OgnlExpressionParser parser = new WebFlowOgnlExpressionParser();
		parser.setAllowUndelimitedEvalExpressions(true);

		Expression attributeExpression = parser.parseExpression("foo", new ParserContextImpl()
				.eval(MutableAttributeMap.class));
		ScopeType scope = ScopeType.REQUEST;
		Expression valueExpression = parser.parseExpression("'" + valueToSet + "'", new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, scope, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExecute_LegacyOGNL_ScopeExpression() throws Exception {

		String valueToSet = "myValue";

		OgnlExpressionParser parser = new WebFlowOgnlExpressionParser();
		parser.setAllowUndelimitedEvalExpressions(true);

		Expression attributeExpression = parser.parseExpression("requestScope.foo", new ParserContextImpl()
				.eval(RequestContext.class));
		Expression valueExpression = parser.parseExpression("'" + valueToSet + "'", new ParserContextImpl()
				.eval(RequestContext.class));

		SetAction action = new SetAction(attributeExpression, null, valueExpression);

		RequestContext context = new MockRequestContext();
		action.execute(context);

		assertTrue(context.getRequestScope().contains("foo"));
		assertEquals(valueToSet, context.getRequestScope().get("foo"));
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
