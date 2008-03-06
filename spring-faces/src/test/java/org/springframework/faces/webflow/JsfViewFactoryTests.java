package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.faces.ui.AjaxViewRoot;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.test.MockExternalContext;

public class JsfViewFactoryTests extends TestCase {

	private static final String VIEW_ID = "/testView.xhtml";

	private ViewFactory factory;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private RequestContext context = (RequestContext) EasyMock.createMock(RequestContext.class);

	private AttributeMap flashMap = new LocalAttributeMap();

	private ViewHandler viewHandler = new MockViewHandler();

	private Lifecycle lifecycle;

	private PhaseListener trackingListener;

	private ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	private MockExternalContext extContext = new MockExternalContext();

	private String event = "foo";

	protected void setUp() throws Exception {
		configureJsf();
		RequestContextHolder.setRequestContext(context);
		EasyMock.expect(context.getFlashScope()).andStubReturn(flashMap);
		EasyMock.expect(context.getExternalContext()).andStubReturn(extContext);
		EasyMock.expect(context.getRequestParameters()).andStubReturn(new LocalParameterMap(new HashMap()));
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
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, new ParserContextImpl().template().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setCreateView(newRoot);

		EasyMock.replay(new Object[] { context });

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
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, new ParserContextImpl().template().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setRestoreView(existingRoot);

		EasyMock.replay(new Object[] { context });

		View restoredView = factory.getView(context);

		assertNotNull("A View was not restored", restoredView);
		assertTrue("A JsfView was expected", restoredView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId());
		assertFalse("An unexpected event was signaled,", restoredView.eventSignaled());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * Ajax Request - View already exists in flash scope and must be restored and the lifecycle executed, no event
	 * signaled
	 */
	public final void testGetView_Restore_Ajax_NoEvent() {

		lifecycle = new NoEventLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, new ParserContextImpl().template().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setRestoreView(existingRoot);

		extContext.setAjaxRequest(true);

		EasyMock.expect(context.getCurrentState()).andReturn(new ModalViewState());

		EasyMock.replay(new Object[] { context });

		View restoredView = factory.getView(context);

		assertNotNull("A View was not restored", restoredView);
		assertTrue("A JsfView was expected", restoredView instanceof JsfView);
		assertTrue("An AjaxViewRoot was not set", ((JsfView) restoredView).getViewRoot() instanceof AjaxViewRoot);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId());
		assertFalse("An unexpected event was signaled,", restoredView.eventSignaled());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
	}

	/**
	 * View already exists in flowscope and must be restored and the lifecycle executed, an event is signaled
	 */
	public final void testGetView_Restore_EventSignaled() {

		lifecycle = new EventSignalingLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, new ParserContextImpl().template().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);

		UIViewRoot existingRoot = new UIViewRoot();
		existingRoot.setViewId(VIEW_ID);
		((MockViewHandler) viewHandler).setRestoreView(existingRoot);

		EasyMock.replay(new Object[] { context });

		View restoredView = factory.getView(context);

		assertNotNull("A View was not restored", restoredView);
		assertTrue("A JsfView was expected", restoredView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) restoredView).getViewRoot().getViewId());
		assertTrue("No event was signaled,", restoredView.eventSignaled());
		assertEquals("Event should be " + event, event, restoredView.getEvent().getId());
		assertTrue("The lifecycle should have been invoked", ((EventSignalingLifecycle) lifecycle).executed);
	}

	/**
	 * Third party sets the view root before RESTORE_VIEW
	 */
	public final void testGetView_ExternalViewRoot() {

		lifecycle = new NoEventLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression(VIEW_ID, new ParserContextImpl().template().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		jsfMock.facesContext().setViewRoot(newRoot);
		jsfMock.facesContext().renderResponse();

		EasyMock.replay(new Object[] { context });

		View newView = factory.getView(context);

		assertNotNull("A View was not created", newView);
		assertTrue("A JsfView was expected", newView instanceof JsfView);
		assertEquals("View name did not match", VIEW_ID, ((JsfView) newView).getViewRoot().getViewId());
		assertSame("View root was not the third party instance", newRoot, ((JsfView) newView).getViewRoot());
		assertFalse("An unexpected event was signaled,", newView.eventSignaled());
		assertTrue("The lifecycle should have been invoked", ((NoEventLifecycle) lifecycle).executed);
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

	private class ModalViewState implements StateDefinition {

		AttributeMap attrs = new LocalAttributeMap();

		public ModalViewState() {
			attrs.asMap().put("modal", Boolean.TRUE);
		}

		public String getId() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public FlowDefinition getOwner() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public AttributeMap getAttributes() {
			return attrs;
		}

		public String getCaption() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getDescription() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

	private class NormalViewState implements StateDefinition {

		public String getId() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public FlowDefinition getOwner() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public AttributeMap getAttributes() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getCaption() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public String getDescription() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

}
