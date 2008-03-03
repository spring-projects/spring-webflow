package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

public class SetActionTests extends TestCase {
	public void testSetAction() throws Exception {
		StaticExpression name = new StaticExpression("");
		SetAction action = new SetAction(name, new StaticExpression("bar"));
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		assertEquals("bar", name.getValue(null));
	}
}
