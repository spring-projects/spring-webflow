package org.springframework.faces.webflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class JsfAjaxHandlerTests {

	private final JSFMockHelper jsfMock = new JSFMockHelper();
	private JsfAjaxHandler ajaxHandler;

	@Before
	public void setUp() throws Exception {
		this.jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(this.jsfMock.servletContext());
		this.ajaxHandler = new JsfAjaxHandler();
		this.ajaxHandler.setApplicationContext(webappContext);
	}

	@After
	public void tearDown() throws Exception {
		this.jsfMock.tearDown();
	}

	@Test
	public void testSendAjaxRedirect() throws Exception {
		this.ajaxHandler.sendAjaxRedirectInternal("/target", this.jsfMock.request(), this.jsfMock.response(), false);
		assertTrue(this.jsfMock.contentAsString().matches("<\\?xml version='1.0' encoding='utf-8'\\?>\n<partial-response.*><redirect url=\"/target\"/></partial-response>"));
		assertEquals("application/xml", this.jsfMock.response().getContentType());
	}

}
