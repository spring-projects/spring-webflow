package org.springframework.faces.webflow;

import static org.junit.Assert.assertNotNull;

import org.apache.el.ExpressionFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class JsfManagedBeanAwareELExpressionParserTests {

	JSFMockHelper jsfMock = new JSFMockHelper();

	RequestContext requestContext = new MockRequestContext();

	ExpressionParser parser;

	@Before
	public void setUp() throws Exception {
		this.jsfMock.setUp();
		RequestContextHolder.setRequestContext(this.requestContext);
		this.parser = new JsfManagedBeanAwareELExpressionParser(new ExpressionFactoryImpl());
	}

	@After
	public void tearDown() throws Exception {
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJSFBean() {
		this.jsfMock.externalContext().getRequestMap().put("myJsfBean", new Object());
		Expression expr = this.parser.parseExpression("myJsfBean", new FluentParserContext().evaluate(RequestContext.class));
		Object result = expr.getValue(this.requestContext);
		assertNotNull("The JSF Bean should not be null.", result);
	}
}
