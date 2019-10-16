package org.springframework.faces.webflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class JsfAjaxHandlerTests {

	private final JSFMockHelper jsfMock = new JSFMockHelper();
	private JsfAjaxHandler ajaxHandler;

	@BeforeEach
	public void setUp() throws Exception {
		this.jsfMock.setUp();
		StaticWebApplicationContext webappContext = new StaticWebApplicationContext();
		webappContext.setServletContext(this.jsfMock.servletContext());
		this.ajaxHandler = new JsfAjaxHandler();
		this.ajaxHandler.setApplicationContext(webappContext);
	}

	@AfterEach
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
