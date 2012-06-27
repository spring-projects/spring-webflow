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

import org.apache.myfaces.test.mock.MockResponseWriter;
import org.apache.myfaces.test.mock.MockStateManager;
import org.easymock.EasyMock;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockParameterMap;

public class JsfViewTests extends TestCase {

	private static final String VIEW_ID = "testView.xhtml";

	private final MockExternalContext extContext = new MockExternalContext();

	private JsfView view;

	private final JSFMockHelper jsfMock = new JSFMockHelper();

	private final StringWriter output = new StringWriter();

	private final String event = "foo";

	private final RequestContext context = EasyMock.createMock(RequestContext.class);
	private final FlowExecutionContext flowExecutionContext = EasyMock.createMock(FlowExecutionContext.class);
	@SuppressWarnings("unchecked")
	private final MutableAttributeMap<Object> flashScope = EasyMock.createMock(MutableAttributeMap.class);
	@SuppressWarnings("unchecked")
	private final MutableAttributeMap<Object> flowMap = EasyMock.createMock(MutableAttributeMap.class);

	private final FlowExecutionKey key = new FlowExecutionKey() {

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

		this.jsfMock.setUp();
		this.jsfMock.facesContext().getApplication().setViewHandler(new MockViewHandler());
		this.jsfMock.facesContext().getApplication().setStateManager(new TestStateManager());
		this.jsfMock.facesContext().setResponseWriter(new MockResponseWriter(this.output, null, null));

		UIViewRoot viewToRender = new UIViewRoot();
		viewToRender.setRenderKitId("HTML_BASIC");
		viewToRender.setViewId(VIEW_ID);
		this.jsfMock.facesContext().setViewRoot(viewToRender);

		UIForm form = new HtmlForm();
		form.setId("myForm");

		UIInput input = new HtmlInputText();
		input.setId("foo");

		form.getChildren().add(input);
		viewToRender.getChildren().add(form);

		RequestContextHolder.setRequestContext(this.context);
		EasyMock.expect(this.context.getExternalContext()).andStubReturn(this.extContext);
		EasyMock.expect(this.context.getFlashScope()).andStubReturn(this.flashScope);
		EasyMock.expect(this.context.getFlowScope()).andStubReturn(this.flowMap);
		EasyMock.expect(this.context.getFlowExecutionContext()).andStubReturn(this.flowExecutionContext);
		EasyMock.expect(this.flowExecutionContext.getKey()).andStubReturn(this.key);

		this.view = new JsfView(viewToRender, this.jsfMock.lifecycle(), this.context);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public final void testSaveState() {
		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });
		this.view.saveState();
	}

	public final void testRender() throws IOException {

		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		this.view.render();
	}

	public final void testRenderException() throws IOException {

		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		this.jsfMock.application().setViewHandler(new ExceptionalViewHandler());

		try {
			this.view.render();
		} catch (Exception ex) {
		}
	}

	/**
	 * View already exists in view scope and must be restored and the lifecycle executed, no event signaled
	 */
	public final void testProcessUserEvent_Restored_NoEvent() {

		EasyMock.expect(this.flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				false);
		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		Lifecycle lifecycle = new NoEventLifecycle(this.jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		JsfView restoredView = new JsfView(existingRoot, lifecycle, this.context);

		restoredView.processUserEvent();

		assertFalse("An unexpected event was signaled,", restoredView.hasFlowEvent());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * View already exists in view scope and must be restored and the lifecycle executed, an event is signaled
	 */
	public final void testProcessUserEvent_Restored_EventSignaled() {

		EasyMock.expect(this.flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				false);
		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		Lifecycle lifecycle = new EventSignalingLifecycle(this.jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		JsfView restoredView = new JsfView(existingRoot, lifecycle, this.context);

		restoredView.processUserEvent();

		assertTrue("No event was signaled,", restoredView.hasFlowEvent());
		assertEquals("Event should be " + this.event, this.event, restoredView.getFlowEvent().getId());
		assertTrue("The lifecycle should have been invoked", ((EventSignalingLifecycle) lifecycle).executed);
	}

	public final void testUserEventQueued_GETRefresh() {

		MockParameterMap requestParameterMap = new MockParameterMap();
		requestParameterMap.put("execution", "e1s1");

		EasyMock.expect(this.context.getRequestParameters()).andStubReturn(requestParameterMap);
		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		JsfView createdView = new JsfView(new UIViewRoot(), this.jsfMock.lifecycle(), this.context);

		assertFalse("No user event should be queued", createdView.userEventQueued());
	}

	public final void testUserEventQueued_FormSubmitted() {

		MockParameterMap requestParameterMap = new MockParameterMap();
		requestParameterMap.put("execution", "e1s1");
		requestParameterMap.put("javax.faces.ViewState", "e1s1");

		EasyMock.expect(this.context.getRequestParameters()).andStubReturn(requestParameterMap);
		EasyMock.replay(new Object[] { this.context, this.flowExecutionContext, this.flowMap, this.flashScope });

		JsfView createdView = new JsfView(new UIViewRoot(), this.jsfMock.lifecycle(), this.context);

		assertTrue("User event should be queued", createdView.userEventQueued());
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
			assertFalse("Lifecycle executed more than once", this.executed);
			super.execute(context);
			this.executed = true;
		}

	}

	private class EventSignalingLifecycle extends FlowLifecycle {
		boolean executed = false;

		public EventSignalingLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse("Lifecycle executed more than once", this.executed);
			super.execute(context);
			JsfViewTests.this.extContext.getRequestMap().put(JsfView.EVENT_KEY, JsfViewTests.this.event);
			this.executed = true;
		}
	}
}
