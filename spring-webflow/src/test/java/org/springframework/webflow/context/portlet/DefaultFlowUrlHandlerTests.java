package org.springframework.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;

public class DefaultFlowUrlHandlerTests extends TestCase {
	private DefaultFlowUrlHandler urlHandler = new DefaultFlowUrlHandler();
	private MockPortletRequest request = new MockPortletRequest();
	private MockActionRequest actionRequest = new MockActionRequest();
	private MockRenderRequest renderRequest = new MockRenderRequest();
	private MockActionResponse actionResponse = new MockActionResponse();
	private MockRenderResponse renderResponse = new MockRenderResponse();

	public void testGetFlowExecutionKey() {
		request.addParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(request));
	}

	public void testSetFlowExecutionRenderParameter() {
		urlHandler.setFlowExecutionRenderParameter("12345", actionResponse);
		assertEquals("12345", actionResponse.getRenderParameter("execution"));
	}

	public void testSetFlowExecutionInSession() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", renderRequest.getPortletSession().getAttribute("execution"));
	}

	public void testSessionFlowExecutionRemoval() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", urlHandler.getFlowExecutionKey(renderRequest));
		actionRequest.setParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(actionRequest));
		assertNull(actionRequest.getPortletSession().getAttribute("execution"));
	}

	public void testCreateFlowExecutionUrl() {
		String url = urlHandler.createFlowExecutionUrl("foo", "12345", renderResponse);
		assertEquals("http://localhost/mockportlet?urlType=action;param_execution=12345", url);
	}
}
