package org.springframework.webflow.mvc.portlet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.mvc.view.AbstractMvcView;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

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

}
