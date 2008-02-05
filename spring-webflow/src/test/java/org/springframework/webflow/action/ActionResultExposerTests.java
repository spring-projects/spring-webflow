package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.expression.WebFlowOgnlExpressionParser;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

public class ActionResultExposerTests extends TestCase {

	public void testExposeResult_ScopeSpecified() {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{foo}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, ScopeType.REQUEST, null);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExposeResult_ScopeSpecifiedWithTypeConversion() {

		String valueToSet = "true";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{foo}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, ScopeType.REQUEST, Boolean.class);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", Boolean.TRUE, context.getRequestScope().get("foo"));
	}

	public void testExposeResult_ScopeExpression() {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{requestScope.foo}", new ParserContextImpl()
				.eval(RequestContext.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, null, null);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExposeResult_ScopeExpressionWithTypeConversion() {

		String valueToSet = "true";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{requestScope.foo}", new ParserContextImpl()
				.eval(RequestContext.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, null, Boolean.class);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", Boolean.TRUE, context.getRequestScope().get("foo"));
	}

	public void testExposeResult_SearchExpression() {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());
		Expression nameExpression = parser.parseExpression("#{bean.foo}", new ParserContextImpl()
				.eval(RequestContext.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, null, null);

		RequestContext context = new MockRequestContext();
		TestBean bean = new TestBean();
		context.getRequestScope().put("bean", bean);

		exposer.exposeResult(valueToSet, context);

		assertEquals("Value of foo is incorrect", valueToSet, bean.getFoo());
	}

	public void testExposeResult_OGNL_ScopeSpecified() {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowOgnlExpressionParser();
		Expression nameExpression = parser.parseExpression("${foo}", new ParserContextImpl()
				.eval(MutableAttributeMap.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, ScopeType.REQUEST, null);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", valueToSet, context.getRequestScope().get("foo"));
	}

	public void testExposeResult_OGNL_ScopeExpression() {

		String valueToSet = "myValue";

		ExpressionParser parser = new WebFlowOgnlExpressionParser();
		Expression nameExpression = parser.parseExpression("${requestScope.foo}", new ParserContextImpl()
				.eval(RequestContext.class));

		ActionResultExposer exposer = new ActionResultExposer(nameExpression, null, null);

		RequestContext context = new MockRequestContext();

		exposer.exposeResult(valueToSet, context);

		assertTrue("Key 'foo' not found in request scope", context.getRequestScope().contains("foo"));
		assertEquals("Value stored at key 'foo' is incorrect", valueToSet, context.getRequestScope().get("foo"));
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
