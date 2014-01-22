package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.springframework.web.context.support.StaticWebApplicationContext;

public class JsfAjaxHandlerTests extends TestCase {

	private JSFMockHelper jsfMock = new JSFMockHelper();
	private JsfAjaxHandler ajaxHandler;

	protected void setUp() throws Exception {
		jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(jsfMock.servletContext());
		ajaxHandler = new JsfAjaxHandler();
		ajaxHandler.setApplicationContext(webappContext);
	}

	protected void tearDown() throws Exception {
		jsfMock.tearDown();
	}

	public void testSendAjaxRedirect() throws Exception {
		ajaxHandler.sendAjaxRedirectInternal("/target", jsfMock.request(), jsfMock.response(), false);
		assertTrue(this.jsfMock.contentAsString().matches("<partial-response.*><redirect url=\"/target\"/></partial-response>"));
		assertEquals("application/xml", jsfMock.response().getContentType());
	}

}
