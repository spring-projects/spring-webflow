package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.test.MockRequestContext;

public class FlowDefinitionRedirectActionTests {
	private FlowDefinitionRedirectAction action;

	@Test
	public void testExecute() throws Exception {
		Expression flowId = new StaticExpression("user?foo=bar");
		action = new FlowDefinitionRedirectAction(flowId);
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("user", context.getMockExternalContext().getFlowRedirectFlowId());
		assertEquals("bar", context.getMockExternalContext().getFlowRedirectFlowInput().get("foo"));
	}

	@Test
	public void testExecuteWithNullRequestFields() throws Exception {
		Expression flowId = new StaticExpression("user");
		action = new FlowDefinitionRedirectAction(flowId);
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("user", context.getMockExternalContext().getFlowRedirectFlowId());
	}

	@Test
	public void testExecuteWithNullFlowId() throws Exception {
		try {
			action = new FlowDefinitionRedirectAction(null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {

		}
	}
}
