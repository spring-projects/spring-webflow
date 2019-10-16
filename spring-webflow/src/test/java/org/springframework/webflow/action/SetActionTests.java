package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

public class SetActionTests {

	@Test
	public void testSetAction() throws Exception {
		StaticExpression name = new StaticExpression("");
		SetAction action = new SetAction(name, new StaticExpression("bar"));
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		assertEquals("bar", name.getValue(null));
	}

}
