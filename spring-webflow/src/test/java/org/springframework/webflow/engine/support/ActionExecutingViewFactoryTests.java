package org.springframework.webflow.engine.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.test.MockRequestContext;

public class ActionExecutingViewFactoryTests {

	@Test
	public void testGetView() throws Exception {
		TestAction action = new TestAction();
		ActionExecutingViewFactory factory = new ActionExecutingViewFactory(action);
		MockRequestContext context = new MockRequestContext();
		View view = factory.getView(context);
		assertFalse(action.isExecuted());
		view.render();
		assertTrue(action.isExecuted());
	}

	@Test
	public void testProcessUserEvent() throws IOException {
		TestAction action = new TestAction();
		ActionExecutingViewFactory factory = new ActionExecutingViewFactory(action);
		MockRequestContext context = new MockRequestContext();
		View view = factory.getView(context);
		assertFalse(action.isExecuted());
		view.render();
		assertTrue(action.isExecuted());
		context.putRequestParameter("_eventId", "foo");
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("foo", view.getFlowEvent().getId());
	}

	@Test
	public void testProcessUserEventButton() throws IOException {
		TestAction action = new TestAction();
		ActionExecutingViewFactory factory = new ActionExecutingViewFactory(action);
		MockRequestContext context = new MockRequestContext();
		View view = factory.getView(context);
		assertFalse(action.isExecuted());
		view.render();
		assertTrue(action.isExecuted());
		context.putRequestParameter("_eventId_foo", "doesn't matter");
		view.processUserEvent();
		assertTrue(view.hasFlowEvent());
		assertEquals("foo", view.getFlowEvent().getId());
	}

	@Test
	public void testProcessUserEventNoEvent() throws IOException {
		TestAction action = new TestAction();
		ActionExecutingViewFactory factory = new ActionExecutingViewFactory(action);
		MockRequestContext context = new MockRequestContext();
		View view = factory.getView(context);
		assertFalse(action.isExecuted());
		view.render();
		assertTrue(action.isExecuted());
		view.processUserEvent();
		assertFalse(view.hasFlowEvent());
		assertNull(view.getFlowEvent());
	}
}
