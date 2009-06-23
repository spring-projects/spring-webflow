package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.test.MockRequestContext;

public class RenderActionTests extends TestCase {
	public void testRenderAction() throws Exception {
		StaticExpression name = new StaticExpression("frag1");
		StaticExpression name2 = new StaticExpression("frag2");
		RenderAction action = new RenderAction(new Expression[] { name, name2 });
		MockRequestContext context = new MockRequestContext();
		Event result = action.execute(context);
		assertEquals("success", result.getId());
		String[] fragments = (String[]) context.getFlashScope().getArray(View.RENDER_FRAGMENTS_ATTRIBUTE,
				String[].class);
		assertEquals("frag1", fragments[0]);
		assertEquals("frag2", fragments[1]);
	}

	public void testIllegalNullArg() {
		try {
			new RenderAction(null);
			fail("iae");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testIllegalEmptyArg() {
		try {
			new RenderAction(new Expression[0]);
			fail("iae");
		} catch (IllegalArgumentException e) {

		}
	}

}
