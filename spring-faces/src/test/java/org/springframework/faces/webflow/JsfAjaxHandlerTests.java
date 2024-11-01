package org.springframework.faces.webflow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.context.support.StaticWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		assertEquals(this.jsfMock.contentAsString(), """
				<?xml version='1.0' encoding='UTF-8'?>
				<partial-response><redirect url="/target"/></partial-response>""");

		assertEquals("application/xml", this.jsfMock.response().getContentType());
	}

}
