package org.springframework.webflow.action;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.test.MockRequestContext;

public class FlowDefinitionRedirectActionTests extends TestCase {
	private FlowDefinitionRedirectAction action;

	public void testExecute() throws Exception {
		Expression flowId = new StaticExpression("user");
		Map input = new HashMap();
		input.put(new StaticExpression("foo"), new StaticExpression("bar"));
		action = new FlowDefinitionRedirectAction(flowId, input);
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("user", context.getMockExternalContext().getFlowRedirectFlowId());
		assertEquals("bar", context.getMockExternalContext().getFlowRedirectFlowInput().get("foo"));
	}

	public void testExecuteWithNullRequestFields() throws Exception {
		Expression flowId = new StaticExpression("user");
		action = new FlowDefinitionRedirectAction(flowId, null);
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("user", context.getMockExternalContext().getFlowRedirectFlowId());
	}

	public void testExecuteWithNullFlowId() throws Exception {
		try {
			action = new FlowDefinitionRedirectAction(null, null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {

		}
	}
}
