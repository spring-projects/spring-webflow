package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;

public class JsfFinalResponseActionTests extends TestCase {

	private static final String VIEW_ID = "/testView.xhtml";

	private JsfViewFactory factory;

	private JsfFinalResponseAction finalResponseAction;

	private JSFMockHelper jsfMock = new JSFMockHelper();

	private RequestContext context = (RequestContext) EasyMock.createMock(RequestContext.class);

	private ViewHandler viewHandler = new NoRenderViewHandler();

	private TestLifecycle lifecycle;

	private PhaseListener trackingListener;

	private StringWriter output = new StringWriter();

	ExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

	protected void setUp() throws Exception {
		configureJsf();
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
		lifecycle = new TestLifecycle(jsfMock.lifecycle());
		factory = new JsfViewFactory(parser.parseExpression("#{'" + VIEW_ID + "'}", new ParserContextImpl().eval(
				RequestContext.class).expect(String.class)), null, lifecycle);
		finalResponseAction = new JsfFinalResponseAction(factory);
		RequestContextHolder.setRequestContext(context);
		MockExternalContext ext = new MockExternalContext();
		ext.setNativeContext(new MockServletContext());
		ext.setNativeRequest(new MockHttpServletRequest());
		ext.setNativeResponse(new MockHttpServletResponse());
		EasyMock.expect(context.getExternalContext()).andStubReturn(ext);
		AttributeMap flash = new LocalAttributeMap();
		EasyMock.expect(context.getFlashScope()).andStubReturn(flash);
		EasyMock.expect(context.getRequestParameters()).andStubReturn(new LocalParameterMap(new HashMap()));
	}

	public void testRender() throws Exception {

		UIViewRoot newRoot = new UIViewRoot();
		newRoot.setViewId(VIEW_ID);
		newRoot.setRenderKitId("HTML_BASIC");
		((MockViewHandler) viewHandler).setCreateView(newRoot);

		EasyMock.replay(new Object[] { context });

		finalResponseAction.execute(context);

		assertTrue(newRoot.isTransient());
		assertTrue(((NoRenderViewHandler) viewHandler).rendered);
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

	private class NoRenderViewHandler extends MockViewHandler {
		boolean rendered = false;

		public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
			rendered = true;
		}

	}

}
