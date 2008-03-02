package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

public class ViewVariableTests extends TestCase {

	private boolean restoreCalled;

	public void testCreateVariable() {
		ViewVariable var = new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		});
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
	}

	public void testDestroyVariable() {
		ViewVariable var = new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		});
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
		var.destroy(context);
		assertFalse(context.getFlowScope().contains("foo"));
	}

	public void testRestoreVariable() {
		ViewVariable var = new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
				restoreCalled = true;
				assertEquals("bar", value);
			}
		});
		MockRequestContext context = new MockRequestContext();
		var.create(context);
		var.restore(context);
		assertEquals("bar", context.getFlowScope().get("foo"));
		assertTrue(restoreCalled);
	}

}
