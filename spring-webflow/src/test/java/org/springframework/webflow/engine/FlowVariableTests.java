package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

public class FlowVariableTests extends TestCase {

	private boolean restoreCalled;

	public void testCreateVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createVariableValue(RequestContext context) {
				return "bar";
			}

			public Object restoreReferences(Object value, RequestContext context) {
				return value;
			}

		}, true);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
	}

	public void testCreateConversationVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createVariableValue(RequestContext context) {
				return "bar";
			}

			public Object restoreReferences(Object value, RequestContext context) {
				return value;
			}
		}, false);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getConversationScope().get("foo"));
	}

	public void testCreateRestoreVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createVariableValue(RequestContext context) {
				return "bar";
			}

			public Object restoreReferences(Object value, RequestContext context) {
				restoreCalled = true;
				assertEquals("bar", value);
				return value;
			}
		}, false);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		var.restore(context);
		assertEquals("bar", context.getConversationScope().get("foo"));
		assertTrue(restoreCalled);
	}
}
