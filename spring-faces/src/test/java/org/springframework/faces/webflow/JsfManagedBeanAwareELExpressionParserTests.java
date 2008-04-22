package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class JsfManagedBeanAwareELExpressionParserTests extends TestCase {

	JSFMockHelper jsfMock = new JSFMockHelper();

	RequestContext requestContext = new MockRequestContext();

	ExpressionParser parser;

	protected void setUp() throws Exception {
		jsfMock.setUp();
		RequestContextHolder.setRequestContext(requestContext);
		parser = new JsfManagedBeanAwareELExpressionParser(new ExpressionFactoryImpl());
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public void testGetJSFBean() {
		jsfMock.externalContext().getRequestMap().put("myJsfBean", new Object());
		Expression expr = parser.parseExpression("myJsfBean", new FluentParserContext().evaluate(RequestContext.class));
		Object result = expr.getValue(requestContext);
		assertNotNull("The JSF Bean should not be null.", result);
	}
}
