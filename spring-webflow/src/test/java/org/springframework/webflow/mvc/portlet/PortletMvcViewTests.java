package org.springframework.webflow.mvc.portlet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.mvc.view.AbstractMvcView;
import org.springframework.webflow.mvc.view.MvcViewTests.BindBean;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.validation.WebFlowMessageCodesResolver;

public class PortletMvcViewTests extends TestCase {

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

	public void testResumeEvent() throws Exception {
		MockRequestContext context = new MockRequestContext();
		context.putRequestParameter("_eventId", "submit");
		context.putRequestParameter("booleanProperty", "bogus");
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
		org.springframework.web.servlet.View mvcView = (org.springframework.web.servlet.View) EasyMock
				.createMock(org.springframework.web.servlet.View.class);
		AbstractMvcView view = new PortletMvcView(mvcView, context);
		view.setExpressionParser(new WebFlowSpringELExpressionParser(new SpelExpressionParser()));
		view.setMessageCodesResolver(new WebFlowMessageCodesResolver());
		view.processUserEvent();
		assertEquals(true, bindBean.getBooleanProperty());
	}

}
