package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.FacesException;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

import junit.framework.TestCase;

import org.apache.shale.test.mock.MockResponseWriter;
import org.apache.shale.test.mock.MockStateManager;
import org.easymock.EasyMock;
import org.springframework.faces.ui.AjaxViewRoot;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;

public class JsfViewTests extends TestCase {

	private static final String VIEW_ID = "testView.xhtml";

	private MockExternalContext extContext = new MockExternalContext();

	private JsfView view;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private StringWriter output = new StringWriter();

	private String event = "foo";

	private RequestContext context = (RequestContext) EasyMock.createMock(RequestContext.class);
	private FlowExecutionContext flowExecutionContext = (FlowExecutionContext) EasyMock
			.createMock(FlowExecutionContext.class);
	private MutableAttributeMap flashScope = (MutableAttributeMap) EasyMock.createMock(MutableAttributeMap.class);
	private MutableAttributeMap flowMap = (MutableAttributeMap) EasyMock.createMock(MutableAttributeMap.class);

	private FlowExecutionKey key = new FlowExecutionKey() {

		public String toString() {
			return "MOCK_KEY";
		}

		public boolean equals(Object o) {
			return true;
		}

		public int hashCode() {
			return 0;
		}
	};

	protected void setUp() throws Exception {

		jsfMock.setUp();
		jsfMock.facesContext().getApplication().setViewHandler(new MockViewHandler());
		jsfMock.application().setStateManager(new TestStateManager());
		jsfMock.facesContext().setResponseWriter(new MockResponseWriter(output, null, null));

		UIViewRoot viewToRender = new UIViewRoot();
		viewToRender.setRenderKitId("HTML_BASIC");
		viewToRender.setViewId(VIEW_ID);
		jsfMock.facesContext().setViewRoot(viewToRender);

		UIForm form = new HtmlForm();
		form.setId("myForm");

		UIInput input = new HtmlInputText();
		input.setId("foo");

		form.getChildren().add(input);
		viewToRender.getChildren().add(form);

		RequestContextHolder.setRequestContext(context);
		EasyMock.expect(context.getExternalContext()).andStubReturn(extContext);
		EasyMock.expect(context.getFlashScope()).andStubReturn(flashScope);
		EasyMock.expect(context.getFlowScope()).andStubReturn(flowMap);
		EasyMock.expect(context.getFlowExecutionContext()).andStubReturn(flowExecutionContext);
		EasyMock.expect(flowExecutionContext.getKey()).andStubReturn(key);

		view = new JsfView(viewToRender, jsfMock.lifecycle(), context);
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public final void testRender() throws IOException {

		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);
		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RESPONSE_COMPLETE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(new Object[] { context, flowExecutionContext, flowMap, flashScope });

		view.render();

		assertNull("The FacesContext was not released", FacesContext.getCurrentInstance());
	}

	public final void testRenderException() throws IOException {

		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);
		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RESPONSE_COMPLETE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(new Object[] { context, flowExecutionContext, flowMap, flashScope });

		jsfMock.application().setViewHandler(new ExceptionalViewHandler());

		try {
			view.render();
		} catch (FacesException ex) {
			assertNull("The FacesContext was not released", FacesContext.getCurrentInstance());
		}
	}

	/**
	 * View already exists in flash scope and must be restored and the lifecycle executed, no event signaled
	 */
	public final void testResume_Restored_NoEvent() {

		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RESPONSE_COMPLETE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		Lifecycle lifecycle = new NoEventLifecycle(jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(new Object[] { context, flowExecutionContext, flowMap, flashScope });

		JsfView restoredView = new JsfView(existingRoot, lifecycle, context);
		restoredView.setRestored(true);

		restoredView.processUserEvent();

		assertFalse("An unexpected event was signaled,", restoredView.hasFlowEvent());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * Ajax Request - View already exists in flash scope and must be restored and the lifecycle executed, no event
	 * signaled
	 */
	public final void testGetView_Restore_Ajax_NoEvent() {

		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RESPONSE_COMPLETE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		Lifecycle lifecycle = new NoEventLifecycle(jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		AjaxViewRoot ajaxRoot = new AjaxViewRoot(existingRoot);

		EasyMock.replay(new Object[] { context, flowExecutionContext, flowMap, flashScope });

		JsfView restoredView = new JsfView(ajaxRoot, lifecycle, context);
		restoredView.setRestored(true);

		restoredView.processUserEvent();

		assertFalse("An unexpected event was signaled,", restoredView.hasFlowEvent());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * View already exists in flowscope and must be restored and the lifecycle executed, an event is signaled
	 */
	public final void testGetView_Restore_EventSignaled() {

		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RESPONSE_COMPLETE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				Boolean.FALSE);
		EasyMock.expect(flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		Lifecycle lifecycle = new EventSignalingLifecycle(jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(new Object[] { context, flowExecutionContext, flowMap, flashScope });

		JsfView restoredView = new JsfView(existingRoot, lifecycle, context);
		restoredView.setRestored(true);

		restoredView.processUserEvent();

		assertTrue("No event was signaled,", restoredView.hasFlowEvent());
		assertEquals("Event should be " + event, event, restoredView.getFlowEvent().getId());
		assertTrue("The lifecycle should have been invoked", ((EventSignalingLifecycle) lifecycle).executed);
	}

	private class ExceptionalViewHandler extends MockViewHandler {
		public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
			throw new IOException("Rendering blew up");
		}
	}

	private class TestStateManager extends MockStateManager {
		public SerializedView saveSerializedView(FacesContext context) {
			SerializedView state = new SerializedView(new Object[] { "tree_state" }, new Object[] { "component_state" });
			return state;
		}
	}

	private class NoEventLifecycle extends FlowLifecycle {

		boolean executed = false;

		public NoEventLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse("Lifecycle executed more than once", executed);
			super.execute(context);
			executed = true;
		}

	}

	private class EventSignalingLifecycle extends FlowLifecycle {
		boolean executed = false;

		public EventSignalingLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse("Lifecycle executed more than once", executed);
			super.execute(context);
			extContext.getRequestMap().put(JsfView.EVENT_KEY, event);
			executed = true;
		}
	}
}
