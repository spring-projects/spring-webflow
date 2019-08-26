package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.test.MockRequestContext;

public class RenderActionTests {
	@Test
	public void testRenderAction() throws Exception {
		StaticExpression name = new StaticExpression("frag1");
		StaticExpression name2 = new StaticExpression("frag2");
		RenderAction action = new RenderAction(name, name2);
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		String[] fragments = context.getFlashScope().getArray(View.RENDER_FRAGMENTS_ATTRIBUTE, String[].class);
		assertEquals("frag1", fragments[0]);
		assertEquals("frag2", fragments[1]);
	}

	@Test
	public void testIllegalNullArg() {
		try {
			new RenderAction((Expression[]) null);
			fail("iae");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testIllegalEmptyArg() {
		try {
			new RenderAction();
			fail("iae");
		} catch (IllegalArgumentException e) {

		}
	}

}
