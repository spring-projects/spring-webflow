package org.springframework.webflow.context.servlet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class AbstractAjaxHandlerTests {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void testIsAjaxRequest() {
		TestAjaxHandler handler = new TestAjaxHandler(null, true);
		assertTrue(handler.isAjaxRequest(request, response));
	}

	@Test
	public void testIsNotAjaxRequest() {
		TestAjaxHandler handler = new TestAjaxHandler(null, false);
		assertFalse(handler.isAjaxRequest(request, response));
	}

	@Test
	public void testIsAjaxRequestViaDelegate() {
		TestAjaxHandler handler = new TestAjaxHandler(new TestAjaxHandler(null, true), false);
		assertTrue(handler.isAjaxRequest(request, response));
	}

	@Test
	public void testSendAjaxRedirect() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, true);
		handler.sendAjaxRedirect("", request, response, false);
		assertTrue(handler.wasAjaxRedirectInternalCalled);
	}

	@Test
	public void testAjaxRedirectNotSent() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, false);
		handler.sendAjaxRedirect("", request, response, false);
		assertFalse(handler.wasAjaxRedirectInternalCalled());
	}

	@Test
	public void testSendAjaxRedirectViaDelegate() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(new TestAjaxHandler(null, true), false);
		handler.sendAjaxRedirect("", request, response, false);
		assertTrue(handler.wasAjaxRedirectInternalCalled());
	}

	private final class TestAjaxHandler extends AbstractAjaxHandler {

		private boolean isAjaxRequest;
		private boolean wasAjaxRedirectInternalCalled;
		private TestAjaxHandler delegate;

		private TestAjaxHandler(TestAjaxHandler delegate, boolean isAjaxRequest) {
			super(delegate);
			this.delegate = delegate;
			this.isAjaxRequest = isAjaxRequest;
		}

		protected boolean isAjaxRequestInternal(HttpServletRequest request, HttpServletResponse response) {
			return isAjaxRequest;
		}

		protected void sendAjaxRedirectInternal(String targetUrl, HttpServletRequest request,
				HttpServletResponse response, boolean popup) {
			wasAjaxRedirectInternalCalled = true;
		}

		public TestAjaxHandler getDelegate() {
			return delegate;
		}

		public boolean wasAjaxRedirectInternalCalled() {
			return wasAjaxRedirectInternalCalled ||
					(getDelegate() != null) && delegate.wasAjaxRedirectInternalCalled();
		}
	}

}
