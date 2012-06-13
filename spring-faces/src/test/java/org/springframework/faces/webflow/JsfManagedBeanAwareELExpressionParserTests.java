package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.apache.el.ExpressionFactoryImpl;
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
		this.jsfMock.setUp();
		RequestContextHolder.setRequestContext(this.requestContext);
		this.parser = new JsfManagedBeanAwareELExpressionParser(new ExpressionFactoryImpl());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public void testGetJSFBean() {
		this.jsfMock.externalContext().getRequestMap().put("myJsfBean", new Object());
		Expression expr = this.parser.parseExpression("myJsfBean", new FluentParserContext().evaluate(RequestContext.class));
		Object result = expr.getValue(this.requestContext);
		assertNotNull("The JSF Bean should not be null.", result);
	}
}
