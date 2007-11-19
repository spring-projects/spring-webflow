package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.test.MockExternalContext;

public class JsfViewFactoryTests extends TestCase {

	private static final String VIEW_ID = "/testView.xhtml";

	private ViewFactory factory;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private RequestContext context = EasyMock.createMock(RequestContext.class);

	private AttributeMap flashMap = new LocalAttributeMap();

	private ViewHandler viewHandler = new MockViewHandler();

	private Lifecycle lifecycle;

	private PhaseListener trackingListener;

	private ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	private ExternalContext extContext = new MockExternalContext();

	private String event = "foo";

	protected void setUp() throws Exception {
		configureJsf();
		RequestContextHolder.setRequestContext(context);
		EasyMock.expect(context.getFlashScope()).andStubReturn(flashMap);
		EasyMock.expect(context.getExternalContext()).andStubReturn(extContext);
		EasyMock.replay(new Object[] { context });
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	private void configureJsf() throws Exception {
		jsfMock.setUp();
		trackingListener = new TrackingPhaseListener();
		jsfMock.lifecycle().addPhaseListener(trackingListener);
		jsfMock.facesContext().setViewRoot(null);
		jsfMock.facesContext().getApplication().setViewHandler(viewHandler);
	}

	/**
	 * View has not yet been created
	 */
	public final void testGetView_Create() {

		lifecycle = new NoEventLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, RequestContext.class, String.class, null), null,
				lifecycle);

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setCreateView(newRoot);

		View newView = factory.getView(context);

		assertNotNull("A View was not created", newView);
		assertTrue("A JsfView was expected", newView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) newView).getViewRoot().getViewId());
		assertFalse("An unexpected event was signaled,", newView.eventSignaled());
		assertFalse("The lifecycle should not have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * View already exists in flash scope and must be restored and the lifecycle executed, no event signaled
	 */
	public final void testGetView_Restore_NoEvent() {

		lifecycle = new NoEventLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, RequestContext.class, String.class, null), null,
				lifecycle);

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setRestoreView(existingRoot);

		View restoredView = factory.getView(context);

		assertNotNull("A View was not restored", restoredView);
		assertTrue("A JsfView was expected", restoredView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId());
		assertFalse("An unexpected event was signaled,", restoredView.eventSignaled());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * View already exists in flowscope and must be restored and the lifecycle executed, an event is signaled
	 */
	public final void testGetView_Restore_EventSignaled() {

		lifecycle = new EventSignalingLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, RequestContext.class, String.class, null), null,
				lifecycle);

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setRestoreView(existingRoot);

		View restoredView = factory.getView(context);

		assertNotNull("A View was not restored", restoredView);
		assertTrue("A JsfView was expected", restoredView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId());
		assertTrue("No event was signaled,", restoredView.eventSignaled());
		assertEquals("Event should be " + event, event, restoredView.getEvent().getId());
		assertTrue("The lifecycle should have been invoked", ((EventSignalingLifecycle) lifecycle).executed);
	}

	/**
	 * View is restored, and then the same view-state is re-entered at the end of the request
	 * @throws Exception
	 */
	/*
	 * public final void testGetView_RestoreTwice() throws Exception {
	 * 
	 * lifecycle = new EventSignalingLifecycle(jsfMock.lifecycle()); factory = new JsfViewFactory(lifecycle,
	 * parser.parseExpression(VIEW_ID));
	 * 
	 * UIViewRoot existingRoot = new UIViewRoot(); existingRoot.setViewId(VIEW_ID); ((MockViewHandler)
	 * viewHandler).setRestoreView(existingRoot);
	 * 
	 * View restoredView = factory.getView(context);
	 * 
	 * assertNull("FacesContext was not released", FacesContext.getCurrentInstance());
	 * 
	 * configureJsf();
	 * 
	 * View recursiveView = factory.getView(context);
	 * 
	 * assertNotNull("A View was not restored", restoredView); assertTrue("A JsfView was expected", restoredView
	 * instanceof JsfView); assertEquals("View name did not match", VIEW_ID, ((JsfView)
	 * restoredView).getViewRoot().getViewId()); assertSame("Re-entered view should be the same instance", ((JsfView)
	 * restoredView).getViewRoot(), ((JsfView) recursiveView).getViewRoot()); assertTrue("No event was signaled,",
	 * restoredView.eventSignaled()); assertEquals("Event should be " + event, event, restoredView.getEvent().getId());
	 * assertTrue("The lifecycle should have been invoked", lifecycle.executed); }
	 */

	/**
	 * Third party sets the view root before RESTORE_VIEW
	 */
	public final void testGetView_ExternalViewRoot() {

		lifecycle = new NoEventLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, RequestContext.class, String.class, null), null,
				lifecycle);

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		jsfMock.facesContext().setViewRoot(newRoot);
		jsfMock.facesContext().renderResponse();

		View newView = factory.getView(context);

		assertNotNull("A View was not created", newView);
		assertTrue("A JsfView was expected", newView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) newView).getViewRoot().getViewId());
		assertSame("View root was not the third party instance", newRoot, ((JsfView) newView).getViewRoot());
		assertFalse("An unexpected event was signaled,", newView.eventSignaled());
		assertFalse("The lifecycle should not have been invoked", ((NoEventLifecycle) lifecycle).executed);
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

		@SuppressWarnings("unchecked")
		public void execute(FacesContext context) throws FacesException {
			assertFalse("Lifecycle executed more than once", executed);
			super.execute(context);
			extContext.getRequestMap().put(JsfView.EVENT_KEY, event);
			executed = true;
		}
	}

	private class TrackingPhaseListener implements PhaseListener {

		private List phaseCallbacks = new ArrayList();

		public void afterPhase(PhaseEvent event) {
			String phaseCallback = "AFTER_" + event.getPhaseId();
			assertFalse("Phase callback " + phaseCallback + " already executed.", phaseCallbacks
					.contains(phaseCallback));
			phaseCallbacks.add(phaseCallback);
		}

		public void beforePhase(PhaseEvent event) {
			String phaseCallback = "BEFORE_" + event.getPhaseId();
			assertFalse("Phase callback " + phaseCallback + " already executed.", phaseCallbacks
					.contains(phaseCallback));
			phaseCallbacks.add(phaseCallback);
		}

		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}

		public List getPhaseCallbacks() {
			return phaseCallbacks;
		}

	}

}
