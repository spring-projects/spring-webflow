package org.springframework.webflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

public class ViewVariableTests {

	private boolean restoreCalled;

	@Test
	public void testCreateVariable() {
		ViewVariable var = new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		});
		Flow flow = new Flow("flow");
		ViewState view = new ViewState(flow, "view", new StubViewFactory());
		MockRequestControlContext context = new MockRequestControlContext(flow);
		view.enter(context);
		var.create(context);
		assertEquals("bar", context.getViewScope().get("foo"));
	}

	@Test
	public void testDestroyVariable() {
		ViewVariable var = new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		});
		Flow flow = new Flow("flow");
		ViewState view = new ViewState(flow, "view", new StubViewFactory());
		MockRequestControlContext context = new MockRequestControlContext(flow);
		view.enter(context);
		var.create(context);
		assertEquals("bar", context.getViewScope().get("foo"));
		var.destroy(context);
		assertFalse(context.getViewScope().contains("foo"));
	}

	@Test
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
		Flow flow = new Flow("flow");
		ViewState view = new ViewState(flow, "view", new StubViewFactory());
		MockRequestControlContext context = new MockRequestControlContext(flow);
		view.enter(context);
		var.create(context);
		var.restore(context);
		assertEquals("bar", context.getViewScope().get("foo"));
		assertTrue(restoreCalled);
	}

}
