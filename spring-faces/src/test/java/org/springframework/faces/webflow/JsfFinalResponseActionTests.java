package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.faces.lifecycle.Lifecycle;
import org.apache.el.ExpressionFactoryImpl;
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
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.test.MockExternalContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsfFinalResponseActionTests {

	private static final String VIEW_ID = "/testView.xhtml";

	private JsfViewFactory factory;

	private final JSFMockHelper jsfMock = new JSFMockHelper();

	private final RequestContext context = EasyMock.createMock(RequestContext.class);

	private final ViewHandler viewHandler = new NoRenderViewHandler();

	ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	@BeforeEach
	public void setUp() throws Exception {
		configureJsf();
	}

	@AfterEach
	public void tearDown() throws Exception {
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	private void configureJsf() throws Exception {

		this.jsfMock.setUp();

		PhaseListener trackingListener = new TrackingPhaseListener();
		this.jsfMock.lifecycle().addPhaseListener(trackingListener);
		this.jsfMock.facesContext().setViewRoot(null);
		this.jsfMock.facesContext().getApplication().setViewHandler(this.viewHandler);
		TestLifecycle lifecycle = new TestLifecycle(this.jsfMock.lifecycle());
		this.factory = new JsfViewFactory(this.parser.parseExpression("#{'" + VIEW_ID + "'}", new FluentParserContext()
				.template().evaluate(RequestContext.class).expectResult(String.class)), lifecycle);
		RequestContextHolder.setRequestContext(this.context);
		MockExternalContext ext = new MockExternalContext();
		ext.setNativeContext(new MockServletContext());
		ext.setNativeRequest(new MockHttpServletRequest());
		ext.setNativeResponse(new MockHttpServletResponse());
		EasyMock.expect(this.context.getExternalContext()).andStubReturn(ext);
		LocalAttributeMap<Object> requestMap = new LocalAttributeMap<>();
		EasyMock.expect(this.context.getFlashScope()).andStubReturn(requestMap);
		EasyMock.expect(this.context.getRequestParameters()).andStubReturn(
				new LocalParameterMap(new HashMap<>()));
	}

	@Test
	public void testRender() throws Exception {

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		newRoot.setRenderKitId("HTML_BASIC");
		((MockViewHandler) this.viewHandler).setCreateView(newRoot);
		this.context.inViewState();
		EasyMock.expectLastCall().andReturn(false);

		EasyMock.replay(this.context);

		View view = this.factory.getView(this.context);
		((JsfView) view).getViewRoot().setTransient(true);
		view.render();

		assertTrue(newRoot.isTransient());
		assertTrue(((NoRenderViewHandler) this.viewHandler).rendered);
	}

	private static class TestLifecycle extends FlowLifecycle {

		boolean executed = false;

		public TestLifecycle(Lifecycle delegate) {
			super(delegate);
		}

		public void execute(FacesContext context) throws FacesException {
			assertFalse(this.executed, "Lifecycle executed more than once");
			super.execute(context);
			this.executed = true;
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

	private static class NoRenderViewHandler extends MockViewHandler {
		boolean rendered = false;

		public void renderView(FacesContext context, UIViewRoot viewToRender) throws FacesException {
			this.rendered = true;
		}

	}

}
