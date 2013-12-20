package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.springframework.web.context.support.StaticWebApplicationContext;

public class JsfAjaxHandlerTests extends TestCase {

	private final JSFMockHelper jsfMock = new JSFMockHelper();
	private JsfAjaxHandler ajaxHandler;

	protected void setUp() throws Exception {
		this.jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(this.jsfMock.servletContext());
		this.ajaxHandler = new JsfAjaxHandler();
		this.ajaxHandler.setApplicationContext(webappContext);
	}

	protected void tearDown() throws Exception {
		this.jsfMock.tearDown();
	}

	public void testSendAjaxRedirect() throws Exception {
		this.ajaxHandler.sendAjaxRedirectInternal("/target", this.jsfMock.request(), this.jsfMock.response(), false);
		assertTrue(this.jsfMock.contentAsString().matches("<partial-response.*><redirect url=\"/target\"/></partial-response>"));
		assertEquals("application/xml", this.jsfMock.response().getContentType());
	}

}
