package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

public class FlowVariableTests extends TestCase {

	private boolean restoreCalled;

	public void testCreateVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}, true);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
	}

	public void testDestroyVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}, true);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
		var.destroy(context);
		assertFalse(context.getFlowScope().contains("foo"));
	}

	public void testCreateConversationVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}, false);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getConversationScope().get("foo"));
	}

	public void testDestroyConversationVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}, false);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getConversationScope().get("foo"));
		var.destroy(context);
		assertFalse(context.getConversationScope().contains("foo"));
	}

	public void testRestoreVariable() {
		FlowVariable var = new FlowVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
				restoreCalled = true;
				assertEquals("bar", value);
			}
		}, false);
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		var.restore(context);
		assertEquals("bar", context.getConversationScope().get("foo"));
		assertTrue(restoreCalled);
	}

}
