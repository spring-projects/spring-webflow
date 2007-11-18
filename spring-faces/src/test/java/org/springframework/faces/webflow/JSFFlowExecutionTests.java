package org.springframework.faces.webflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.CompositeELResolver;
import javax.faces.FacesException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.method.MethodSignature;
import org.springframework.binding.method.Parameter;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.webflow.action.AbstractBeanInvokingAction;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.action.SetAction;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.expression.el.RequestContextELResolver;
import org.springframework.webflow.core.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

public class JSFFlowExecutionTests extends TestCase {

	JSFMockHelper jsf;
	JSFManagedBean jsfBean;
	JSFModel jsfModel;
	MockViewHandler viewHandler;
	MockService service;
	GenericWebApplicationContext ctx;
	TrackingPhaseListener trackingListener;

	Flow flow;
	FlowExecution execution;

	ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	/**
	 * TODO - The management of the JSF mocks has gotten rather convoluted now that we are tearing down and rebuilding
	 * the FacesContext multiple times per request. Consider enhancing JSFMockHelper to manage things more appropriately
	 * for SWF usage.
	 */
	protected void setUp() throws Exception {
		service = EasyMock.createMock(MockService.class);

		trackingListener = new TrackingPhaseListener();
		jsfRequestSetup();

		flow = Flow.create("jsf-flow", null);

		ViewState view1 = new ViewState(flow, "viewState1", new JsfViewFactory(parser.parseExpression("/view1",
				RequestContext.class, String.class, null), null));
		view1.getTransitionSet().add(new Transition(on("event1"), to("doSomething")));
		view1.getTransitionSet().add(new Transition(on("event2"), to("evalSomething")));

		ActionState doSomething = new ActionState(flow, "doSomething");
		doSomething.getActionList().add(
				new StubBeanAction(new MethodSignature("doSomething", new Parameter(String.class, parser
						.parseExpression("#{JsfBean.prop1}", RequestContext.class, String.class, null)))));
		doSomething.getTransitionSet().add(new Transition(on("success"), to("viewState2")));

		ActionState evalSomething = new ActionState(flow, "evalSomething");
		evalSomething.getEntryActionList().add(
				new SetAction(parser.parseExpression("#{requestContext.flowScope.jsfModel}", RequestContext.class,
						String.class, null), ScopeType.FLOW, parser.parseExpression("#{'foo'}", RequestContext.class,
						String.class, null)));
		evalSomething.getActionList().add(
				new EvaluateAction(parser.parseExpression("#{JsfBean.addValue(jsfModel)}", RequestContext.class,
						String.class, null)));
		evalSomething.getTransitionSet().add(new Transition(on("success"), to("viewState2")));

		ViewState viewState2 = new ViewState(flow, "viewState2", new JsfViewFactory(parser.parseExpression("/view2",
				RequestContext.class, String.class, null), null));
		viewState2.getEntryActionList().add(new ViewState2SetupAction());
		viewState2.getTransitionSet().add(new Transition(on("event1"), to("endState1")));

		new EndState(flow, "endState1");

		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionKeyFactory(new SimpleFlowExecutionKeyFactory());
		execution = factory.createFlowExecution(flow);
	}

	private void jsfRequestSetup() throws Exception {
		jsf = new JSFMockHelper();
		jsf.tearDown();
		jsf.setUp();
		FacesContext flowContext = new FlowFacesContext(new MockRequestContext(), jsf.facesContext());
		org.apache.shale.test.mock.MockFacesContext.setCurrentInstance(flowContext);

		viewHandler = new NoRenderViewHandler();
		jsf.application().setViewHandler(viewHandler);
		trackingListener.reset();
		jsf.lifecycle().addPhaseListener(trackingListener);

		CompositeELResolver baseResolver = (CompositeELResolver) jsf.facesContext().getELContext().getELResolver();
		baseResolver.add(new RequestContextELResolver());
		baseResolver.add(new ScopeSearchingELResolver());

		jsf.externalContext().getRequestMap().put("JsfBean", new JSFManagedBean());
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public void testManagedBeanExpression() {
		ValueBinding vb = jsf.application().createValueBinding("#{JsfBean}");
		jsfBean = (JSFManagedBean) vb.getValue(jsf.facesContext());
		assertNotNull(jsfBean);
	}

	/*
	 * public void testBeanAction() throws Exception { startFlow();
	 * 
	 * jsfRequestSetup();
	 * 
	 * testManagedBeanExpression(); jsfBean.setProp1("arg"); service.doSomething(jsfBean.getProp1());
	 * EasyMock.replay(new Object[] { service });
	 * 
	 * jsf.externalContext().getRequestMap().put(JsfView.EVENT_KEY, "event1");
	 * 
	 * UIViewRoot existingRoot = new UIViewRoot(); existingRoot.setViewId("view1");
	 * viewHandler.setRestoreView(existingRoot);
	 * 
	 * execution.resume(getExternalContext());
	 * 
	 * EasyMock.verify(new Object[] { service });
	 * 
	 * ViewState currentState = (ViewState) execution.getActiveSession().getState(); assertEquals("viewState2",
	 * currentState.getId()); }
	 * 
	 * public void testEvalAction() throws Exception { startFlow();
	 * 
	 * jsfRequestSetup();
	 * 
	 * testManagedBeanExpression();
	 * 
	 * jsf.externalContext().getRequestMap().put(JsfView.EVENT_KEY, "event2");
	 * 
	 * UIViewRoot existingRoot = new UIViewRoot(); existingRoot.setViewId("view1");
	 * viewHandler.setRestoreView(existingRoot);
	 * 
	 * execution.resume(getExternalContext());
	 * 
	 * assertFalse(jsfBean.getValues().isEmpty()); String addedValue = jsfBean.getValues().get(0).toString();
	 * assertEquals(addedValue, "foo");
	 * 
	 * ViewState currentState = (ViewState) execution.getActiveSession().getState(); assertEquals("viewState2",
	 * currentState.getId()); }
	 */
	private static TransitionCriteria on(String event) {
		return new EventIdTransitionCriteria(event);
	}

	private static TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}

	private void startFlow() {
		UIViewRoot view = new UIViewRoot();
		view.setViewId("view1");
		viewHandler.setCreateView(view);
		execution.start(getExternalContext());
	}

	private ExternalContext getExternalContext() {
		jsf.request().setPathElements("myApp", "", "/flow", null);
		ExternalContext ext = new ServletExternalContext(jsf.servletContext(), jsf.request(), jsf.response());
		ExternalContextHolder.setExternalContext(ext);
		return ext;
	}

	private class TestLifecycle extends FlowLifecycle {

		boolean executed = false;

		public TestLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse("Lifecycle executed more than once", executed);
			super.execute(context);
			executed = true;
		}

		public void reset() {
			executed = false;
		}

	}

	private class StubBeanAction extends AbstractBeanInvokingAction {

		protected StubBeanAction(MethodSignature methodSignature) {
			super(methodSignature);
		}

		protected Object getBean(RequestContext context) throws Exception {
			return service;
		}

	}

	private static class SimpleFlowExecutionKeyFactory implements FlowExecutionKeyFactory {
		public FlowExecutionKey getKey(FlowExecution execution) {
			return new FlowExecutionKey() {
				public String toString() {
					return "key";
				}

				public boolean equals(Object o) {
					return true;
				}

				public int hashCode() {
					return 0;
				}
			};
		}
	}

	private class NoRenderViewHandler extends MockViewHandler {

		public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
			// do nothing
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

		public void reset() {
			phaseCallbacks.clear();
		}

	}

	private class ViewState2SetupAction implements Action {

		public Event execute(RequestContext context) throws Exception {
			jsfRequestSetup();
			UIViewRoot newRoot = new UIViewRoot();
			newRoot.setViewId("view2");
			viewHandler.setCreateView(newRoot);
			return new Event(this, "success");
		}
	}
}
