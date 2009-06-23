package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.test.MockRequestContext;

public class ActionResultExposerTests extends TestCase {

	public void testEvaluateExpressionResult() throws Exception {
		StaticExpression resultExpression = new StaticExpression("");
		ActionResultExposer exposer = new ActionResultExposer(resultExpression, null, null);
		MockRequestContext context = new MockRequestContext();
		exposer.exposeResult("foo", context);
		assertEquals("foo", resultExpression.getValue(null));
	}

	public void testEvaluateExpressionNullResult() throws Exception {
		StaticExpression resultExpression = new StaticExpression("");
		ActionResultExposer exposer = new ActionResultExposer(resultExpression, null, null);
		MockRequestContext context = new MockRequestContext();
		exposer.exposeResult(null, context);
		assertEquals(null, resultExpression.getValue(null));
	}

	public void testEvaluateExpressionResultExposerWithTypeConversion() throws Exception {
		StaticExpression resultExpression = new StaticExpression("");
		ActionResultExposer exposer = new ActionResultExposer(resultExpression, Integer.class,
				new DefaultConversionService());
		MockRequestContext context = new MockRequestContext();
		exposer.exposeResult("3", context);
		assertEquals(new Integer(3), resultExpression.getValue(null));
	}

	public void testEvaluateExpressionResultExposerWithTypeConversionForgotArgument() throws Exception {
		StaticExpression resultExpression = new StaticExpression("");
		try {
			new ActionResultExposer(resultExpression, Integer.class, null);
			fail("should have failed iae");
		} catch (IllegalArgumentException e) {

		}
	}

}
