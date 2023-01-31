package org.springframework.faces.webflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.myfaces.test.mock.MockResponseWriter;
import org.easymock.EasyMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockParameterMap;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.html.HtmlForm;
import jakarta.faces.component.html.HtmlInputText;
import jakarta.faces.context.FacesContext;
import jakarta.faces.lifecycle.Lifecycle;

public class JsfViewTests {

	private static final String VIEW_ID = "testView.xhtml";

	private final MockExternalContext extContext = new MockExternalContext();

	private JsfView view;

	private final JSFMockHelper jsfMock = new JSFMockHelper();

	private final StringWriter output = new StringWriter();

	private final String event = "foo";

	private final RequestContext context = EasyMock.createMock(RequestContext.class);
	private final FlowExecutionContext flowExecutionContext = EasyMock.createMock(FlowExecutionContext.class);
	private final MutableAttributeMap<Object> flashScope = EasyMock.createMock(MutableAttributeMap.class);
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

	@BeforeEach
	public void setUp() throws Exception {

		this.jsfMock.setUp();
		this.jsfMock.facesContext().getApplication().setViewHandler(new MockViewHandler());
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

	@AfterEach
	public void tearDown() throws Exception {
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public final void testSaveState() {
		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);
		this.view.saveState();
	}

	@Test
	public final void testRender() throws IOException {

		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		this.view.render();
	}

	@Test
	public final void testRenderException() {

		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		this.jsfMock.application().setViewHandler(new ExceptionalViewHandler());

		try {
			this.view.render();
		} catch (Exception ignored) {
		}
	}

	/**
	 * View already exists in view scope and must be restored and the lifecycle executed, no event signaled
	 */
	@Test
	public final void testProcessUserEvent_Restored_NoEvent() {

		EasyMock.expect(this.flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				false);
		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		NoEventLifecycle lifecycle = new NoEventLifecycle(this.jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		JsfView restoredView = new JsfView(existingRoot, lifecycle, this.context);

		restoredView.processUserEvent();

		assertFalse(restoredView.hasFlowEvent(), "An unexpected event was signaled,");
		assertTrue(lifecycle.executed, "The lifecycle should have been invoked");
	}

	/**
	 * View already exists in view scope and must be restored and the lifecycle executed, an event is signaled
	 */
	@Test
	public final void testProcessUserEvent_Restored_EventSignaled() {

		EasyMock.expect(this.flashScope.getBoolean(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY))).andStubReturn(
				false);
		EasyMock.expect(this.flashScope.put(EasyMock.matches(FlowFacesContext.RENDER_RESPONSE_KEY), EasyMock.anyObject()))
				.andStubReturn(null);

		EventSignalingLifecycle lifecycle = new EventSignalingLifecycle(this.jsfMock.lifecycle());

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);

		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		JsfView restoredView = new JsfView(existingRoot, lifecycle, this.context);

		restoredView.processUserEvent();

		assertTrue(restoredView.hasFlowEvent(), "No event was signaled,");
		assertEquals(this.event, restoredView.getFlowEvent().getId(), "Event should be " + this.event);
		assertTrue(lifecycle.executed, "The lifecycle should have been invoked");
	}

	@Test
	public final void testUserEventQueued_GETRefresh() {

		MockParameterMap requestParameterMap = new MockParameterMap();
		requestParameterMap.put("execution", "e1s1");

		EasyMock.expect(this.context.getRequestParameters()).andStubReturn(requestParameterMap);
		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		JsfView createdView = new JsfView(new UIViewRoot(), this.jsfMock.lifecycle(), this.context);

		assertFalse(createdView.userEventQueued(), "No user event should be queued");
	}

	@Test
	public final void testUserEventQueued_FormSubmitted() {

		this.jsfMock.request().addParameter("execution", "e1s1");
		this.jsfMock.request().addParameter("jakarta.faces.ViewState", "e1s1");

		EasyMock.replay(this.context, this.flowExecutionContext, this.flowMap, this.flashScope);

		JsfView createdView = new JsfView(new UIViewRoot(), this.jsfMock.lifecycle(), this.context);

		assertTrue(createdView.userEventQueued(), "User event should be queued");
	}

	private static class ExceptionalViewHandler extends MockViewHandler {
		public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
			throw new IOException("Rendering blew up");
		}
	}

	private static class NoEventLifecycle extends FlowLifecycle {

		boolean executed = false;

		public NoEventLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse(this.executed, "Lifecycle executed more than once");
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
			assertFalse(this.executed, "Lifecycle executed more than once");
			super.execute(context);
			JsfViewTests.this.extContext.getRequestMap().put(JsfView.EVENT_KEY, JsfViewTests.this.event);
			this.executed = true;
		}
	}
}
