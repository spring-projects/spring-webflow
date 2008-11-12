package org.springframework.webflow.mvc.portlet;

import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.mvc.view.AbstractMvcView;
import org.springframework.webflow.mvc.view.MvcViewTests.BindBean;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

public class PortletMvcViewTests extends TestCase {

	private boolean renderCalled;

	private Map model;

	public void testRender() throws Exception {
		RenderRequest request = new MockRenderRequest();
		RenderResponse response = new MockRenderResponse();
		MockRequestContext context = new MockRequestContext();
		context.getMockExternalContext().setNativeContext(new MockPortletContext());
		context.getMockExternalContext().setNativeRequest(request);
		context.getMockExternalContext().setNativeResponse(response);
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = (org.springframework.web.servlet.View) EasyMock
				.createMock(org.springframework.web.servlet.View.class);
		AbstractMvcView view = new PortletMvcView(mvcView, context);
		view.render();
		assertNotNull(request.getAttribute(ViewRendererServlet.VIEW_ATTRIBUTE));
		assertNotNull(request.getAttribute(ViewRendererServlet.MODEL_ATTRIBUTE));
	}

	public void testResumeEventModelBindingFieldMarkerFieldPresent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("booleanProperty", "true");
		context.putRequestParameter("_booleanProperty", "whatever");
		BindBean bindBean = new BindBean();
		StaticExpression modelObject = new StaticExpression(bindBean);
		modelObject.setExpressionString("bindBean");
		context.getCurrentState().getAttributes().put("model", modelObject);
		context.getFlowScope().put("bindBean", bindBean);
		context.getMockExternalContext().setNativeContext(new MockServletContext());
		context.getMockExternalContext().setNativeRequest(new MockHttpServletRequest());
		context.getMockExternalContext().setNativeResponse(new MockHttpServletResponse());
		context.getMockFlowExecutionContext().setKey(new MockFlowExecutionKey("c1v1"));
		org.springframework.web.servlet.View mvcView = new MockView();
		AbstractMvcView view = new MockPortletMvcView(mvcView, context);
		view.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		view.processUserEvent();
		assertEquals(true, bindBean.getBooleanProperty());
		MappingResultsHolder holder = (MappingResultsHolder) context.getFlashScope().get(
				MappingResultsHolder.MAPPING_RESULTS_HOLDER_KEY);
		assertEquals("submit", holder.getEventId());
		assertNotNull(holder.getMappingResults());
		assertFalse(holder.getViewErrors());
	}

	private class MockPortletMvcView extends PortletMvcView {

		public MockPortletMvcView(View view, RequestContext context) {
			super(view, context);
		}

		protected void doRender(Map model) throws Exception {
			getView().render(model, (HttpServletRequest) getRequestContext().getExternalContext().getNativeRequest(),
					(HttpServletResponse) getRequestContext().getExternalContext().getNativeResponse());
		}

	}

	private class MockView implements View {

		public String getContentType() {
			return "text/html";
		}

		public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			renderCalled = true;
			model = model;
		}

	}

}
