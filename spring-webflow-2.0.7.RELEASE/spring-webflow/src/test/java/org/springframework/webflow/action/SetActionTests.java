package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

public class SetActionTests extends TestCase {
	public void testSetAction() throws Exception {
		StaticExpression name = new StaticExpression("");
		SetAction action = new SetAction(name, new StaticExpression("bar"), null, null);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		assertEquals("bar", name.getValue(null));
	}

	public void testSetActionWithTypeConversion() throws Exception {
		StaticExpression name = new StaticExpression("");
		SetAction action = new SetAction(name, new StaticExpression("3"), Integer.class, new DefaultConversionService());
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		assertEquals(new Integer(3), name.getValue(null));
	}

}
