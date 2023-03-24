package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIOutput;
import jakarta.faces.component.UIPanel;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.faces.event.PostRestoreStateEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.lifecycle.Lifecycle;
import org.apache.el.ExpressionFactoryImpl;
import org.apache.myfaces.test.mock.MockApplication20;
import org.easymock.EasyMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.test.MockExternalContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JsfViewFactoryTests {

	private static final String VIEW_ID = "/testView.xhtml";

	private ViewFactory factory;

	private final JSFMockHelper jsfMock = new JSFMockHelper();

	private final RequestContext context = EasyMock.createMock(RequestContext.class);

	private final LocalAttributeMap<Object> flashMap = new LocalAttributeMap<>();

	private final ViewHandler viewHandler = new MockViewHandler();

	private Lifecycle lifecycle;

	private final ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	private final MockExternalContext extContext = new MockExternalContext();

	private final MockServletContext servletContext = new MockServletContext();

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	@BeforeEach
	public void setUp() throws Exception {
		configureJsf();
		this.extContext.setNativeContext(this.servletContext);
		this.extContext.setNativeRequest(this.request);
		this.extContext.setNativeResponse(this.response);
		RequestContextHolder.setRequestContext(this.context);
		EasyMock.expect(this.context.getFlashScope()).andStubReturn(this.flashMap);
		EasyMock.expect(this.context.getExternalContext()).andStubReturn(this.extContext);
		EasyMock.expect(this.context.getRequestParameters()).andStubReturn(
				new LocalParameterMap(new HashMap<>()));
	}

	@AfterEach
	public void tearDown() throws Exception {
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	private void configureJsf() throws Exception {
		this.jsfMock.setUp();
		ExceptionEventAwareMockApplication application = new ExceptionEventAwareMockApplication();
		((MockBaseFacesContext) FlowFacesContext.getCurrentInstance()).setApplication(application);
		PhaseListener trackingListener = new TrackingPhaseListener();
		this.jsfMock.lifecycle().addPhaseListener(trackingListener);
		this.jsfMock.facesContext().setViewRoot(null);
		this.jsfMock.facesContext().getApplication().setViewHandler(this.viewHandler);
	}

	/**
	 * View has not yet been created
	 */
	@Test
	public final void testGetView_Create() {

		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot newRoot = new MockUIViewRoot();
		newRoot.setViewId(VIEW_ID);
		((MockViewHandler) this.viewHandler).setCreateView(newRoot);
		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.replay(this.context);

		View newView = this.factory.getView(this.context);

		assertNotNull(newView, "A View was not created");
		assertTrue(newView instanceof JsfView, "A JsfView was expected");
		assertEquals(VIEW_ID, ((JsfView) newView).getViewRoot().getViewId(), "View name did not match");
		assertFalse(newView.hasFlowEvent(), "An unexpected event was signaled,");
	}

	/**
	 * View already exists in view/flash scope and must be restored and the lifecycle executed, no flow event signaled
	 */
	@Test
	public final void testGetView_Restore() {

		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot existingRoot = new MockUIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		UIInput input = new UIInput();
		input.setId("invalidInput");
		input.setValid(false);
		existingRoot.getChildren().add(input);
		((MockViewHandler) this.viewHandler).setRestoreView(existingRoot);

		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.replay(this.context);

		View restoredView = this.factory.getView(this.context);

		assertNotNull(restoredView, "A View was not restored");
		assertTrue(restoredView instanceof JsfView, "A JsfView was expected");
		assertEquals(VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId(), "View name did not match");
		assertFalse(restoredView.hasFlowEvent(), "An unexpected event was signaled,");
		assertTrue(input.isValid(), "The input component's valid flag was not reset");
		assertTrue(existingRoot.isPostRestoreStateEventSeen(), "The PostRestoreViewEvent was not seen");
	}

	/**
	 * View already exists in view/flash scope and must be restored and the lifecycle executed, no flow event signaled
	 */
	@Test
	@SuppressWarnings({ "deprecation", "unchecked" })
	public final void testGetView_RestoreWithBindings() {

		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot existingRoot = new MockUIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		UIPanel panel = new UIPanel();
		panel.setId("panel1");
		UIOutput output = new UIOutput();
		FacesContext facesContext = this.jsfMock.facesContext();
		output.setValueExpression("binding",
				facesContext.getApplication().getExpressionFactory().createValueExpression(
						facesContext.getELContext(), "#{myBean.output}", UIOutput.class));
		output.setId("output1");
		UIInput input = new UIInput();
		input.setValueExpression("binding",
				facesContext.getApplication().getExpressionFactory().createValueExpression(
						facesContext.getELContext(), "#{myBean.input}", UIInput.class));
		input.setId("input1");

		existingRoot.getChildren().add(panel);
		panel.getFacets().put("label", output);
		panel.getChildren().add(input);

		TestBean testBean = new TestBean();
		//noinspection unchecked
		this.jsfMock.externalContext().getRequestMap().put("myBean", testBean);

		((MockViewHandler) this.viewHandler).setRestoreView(existingRoot);

		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.replay(this.context);

		View restoredView = this.factory.getView(this.context);

		assertNotNull(restoredView, "A View was not restored");
		assertTrue(restoredView instanceof JsfView, "A JsfView was expected");
		assertEquals(VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId(), "View name did not match");
		assertFalse(restoredView.hasFlowEvent(), "An unexpected event was signaled,");
		assertSame(input, testBean.getInput(), "The UIInput binding was not restored properly");
		assertSame(output, testBean.getOutput(), "The faceted UIOutput binding was not restored properly");
		assertTrue(existingRoot.isPostRestoreStateEventSeen(), "The PostRestoreViewEvent was not seen");
	}

	/**
	 * Ajax Request - View already exists in view/flash scope and must be restored and the lifecycle executed, no flow
	 * event signaled
	 */
	@Test
	public final void testGetView_Restore_Ajax() {

		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot existingRoot = new MockUIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) this.viewHandler).setRestoreView(existingRoot);

		this.request.addHeader("Accept", "text/html;type=ajax");

		EasyMock.expect(this.context.getCurrentState()).andReturn(new NormalViewState());

		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.replay(this.context);

		View restoredView = this.factory.getView(this.context);

		assertNotNull(restoredView, "A View was not restored");
		assertTrue(restoredView instanceof JsfView, "A JsfView was expected");
		assertNotNull(((JsfView) restoredView).getViewRoot(), "An ViewRoot was not set");
		assertEquals(VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId(), "View name did not match");
		assertFalse(restoredView.hasFlowEvent(), "An unexpected event was signaled,");
		assertTrue(existingRoot.isPostRestoreStateEventSeen(), "The PostRestoreViewEvent was not seen");
	}

	/**
	 * Third party sets the view root before RESTORE_VIEW
	 */
	@Test
	public final void testGetView_ExternalViewRoot() {
		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot newRoot = new MockUIViewRoot();
		newRoot.setViewId(VIEW_ID);
		this.jsfMock.facesContext().setViewRoot(newRoot);
		this.jsfMock.facesContext().renderResponse();

		EasyMock.replay(this.context);

		View newView = this.factory.getView(this.context);

		assertNotNull(newView, "A View was not created");
		assertTrue(newView instanceof JsfView, "A JsfView was expected");
		assertEquals(VIEW_ID, ((JsfView) newView).getViewRoot().getViewId(), "View name did not match");
		assertSame(newRoot, ((JsfView) newView).getViewRoot(), "View root was not the third party instance");
		assertFalse(newView.hasFlowEvent(), "An unexpected event was signaled,");
		assertTrue(newRoot.isPostRestoreStateEventSeen(), "The PostRestoreViewEvent was not seen");
	}

	@Test
	public void testGetView_ExceptionsOnPostRestoreStateEvent() {
		this.lifecycle = new NoExecutionLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression(VIEW_ID,
				new FluentParserContext().template().evaluate(RequestContext.class).expectResult(String.class)),
				this.lifecycle);

		MockUIViewRoot existingRoot = new MockUIViewRoot();
		existingRoot.setThrowOnPostRestoreStateEvent(true);
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) this.viewHandler).setRestoreView(existingRoot);

		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.replay(this.context);
		this.factory.getView(this.context);
		ExceptionEventAwareMockApplication application = (ExceptionEventAwareMockApplication) FlowFacesContext
				.getCurrentInstance().getApplication();
		assertNotNull(application.getExceptionQueuedEventContext(), "Expected exception event");
		assertSame(existingRoot.getAbortProcessingException(),
				application.getExceptionQueuedEventContext().getException(), "Expected same exception");
	}

	private static class NoExecutionLifecycle extends FlowLifecycle {

		public NoExecutionLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			fail("The lifecycle should not be invoked from the ViewFactory");
		}
	}

	private static class TrackingPhaseListener implements PhaseListener {

		private final List<String> phaseCallbacks = new ArrayList<>();

		public void afterPhase(PhaseEvent event) {
			String phaseCallback = "AFTER_" + event.getPhaseId();
			assertFalse(this.phaseCallbacks.contains(phaseCallback),
					"Phase callback " + phaseCallback + " already executed.");
			this.phaseCallbacks.add(phaseCallback);
		}

		public void beforePhase(PhaseEvent event) {
			String phaseCallback = "BEFORE_" + event.getPhaseId();
			assertFalse(this.phaseCallbacks.contains(phaseCallback),
					"Phase callback " + phaseCallback + " already executed.");
			this.phaseCallbacks.add(phaseCallback);
		}

		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}
	}

	private static class NormalViewState implements StateDefinition {

		public boolean isViewState() {
			return true;
		}

		public String getId() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public FlowDefinition getOwner() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public MutableAttributeMap<Object> getAttributes() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getCaption() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getDescription() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}
	}

	public static class TestBean {

		UIOutput output;
		UIInput input;

		public UIOutput getOutput() {
			return this.output;
		}

		public void setOutput(UIOutput output) {
			this.output = output;
		}

		public UIInput getInput() {
			return this.input;
		}

		public void setInput(UIInput input) {
			this.input = input;
		}
	}

	private static class MockUIViewRoot extends UIViewRoot {

		private boolean postRestoreStateEventSeen;
		private boolean throwOnPostRestoreStateEvent;
		private AbortProcessingException abortProcessingException;

		public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
			if (event instanceof PostRestoreStateEvent) {
				assertSame(this, event.getComponent(), "Component did not match");
				this.postRestoreStateEventSeen = true;
				if (this.throwOnPostRestoreStateEvent) {
					this.abortProcessingException = new AbortProcessingException();
					throw this.abortProcessingException;
				}
			}
		}

		public void setThrowOnPostRestoreStateEvent(boolean throwOnPostRestoreStateEvent) {
			this.throwOnPostRestoreStateEvent = throwOnPostRestoreStateEvent;
		}

		public boolean isPostRestoreStateEventSeen() {
			return this.postRestoreStateEventSeen;
		}

		public AbortProcessingException getAbortProcessingException() {
			return this.abortProcessingException;
		}
	}

	private static class ExceptionEventAwareMockApplication extends MockApplication20 {

		private ExceptionQueuedEventContext exceptionQueuedEventContext;

		public void publishEvent(FacesContext facesContext, Class<? extends SystemEvent> systemEventClass, Object source) {
			if (ExceptionQueuedEvent.class.equals(systemEventClass)) {
				this.exceptionQueuedEventContext = (ExceptionQueuedEventContext) source;
			} else {
				super.publishEvent(facesContext, systemEventClass, source);
			}
		}

		public ExceptionQueuedEventContext getExceptionQueuedEventContext() {
			return this.exceptionQueuedEventContext;
		}
	}
}
