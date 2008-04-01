package org.springframework.webflow.mvc.portlet;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.format.formatters.DateFormatter;
import org.springframework.binding.format.registry.DefaultFormatterRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.webflow.mvc.portlet.PortletMvcView;
import org.springframework.webflow.mvc.view.MvcView;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

public class PortletMvcViewTests extends TestCase {

	private boolean renderCalled;

	private Map model;

	private DefaultFormatterRegistry formatterRegistry = new DefaultFormatterRegistry();

	private ApplicationContext applicationContext;

	protected void setUp() {
		DateFormatter dateFormatter = new DateFormatter();
		dateFormatter.setLocale(Locale.ENGLISH);
		formatterRegistry.registerFormatter(Date.class, dateFormatter);
		applicationContext = (ApplicationContext) EasyMock.createMock(ApplicationContext.class);
	}

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
		MvcView view = new PortletMvcView(mvcView, context, applicationContext);
		view.setFormatterRegistry(formatterRegistry);
		view.render();
		assertNotNull(request.getAttribute(ViewRendererServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		assertNotNull(request.getAttribute(ViewRendererServlet.VIEW_ATTRIBUTE));
		assertNotNull(request.getAttribute(ViewRendererServlet.MODEL_ATTRIBUTE));
	}

}
