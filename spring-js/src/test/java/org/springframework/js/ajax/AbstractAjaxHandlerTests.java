package org.springframework.js.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class AbstractAjaxHandlerTests extends TestCase {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	protected void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testIsAjaxRequest() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, true);
		assertTrue(handler.isAjaxRequest(request, response));
	}

	public void testIsNotAjaxRequest() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, false);
		assertFalse(handler.isAjaxRequest(request, response));
	}

	public void testIsAjaxRequestViaDelegate() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(new TestAjaxHandler(null, true), false);
		assertTrue(handler.isAjaxRequest(request, response));
	}

	public void testSendAjaxRedirect() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, true);
		handler.sendAjaxRedirect("", request, response, false);
		assertTrue(handler.wasAjaxRedirectInternalCalled);
	}

	public void testAjaxRedirectNotSent() throws Exception {
		TestAjaxHandler handler = new TestAjaxHandler(null, false);
		handler.sendAjaxRedirect("", request, response, false);
		assertFalse(handler.wasAjaxRedirectInternalCalled());
	}

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
				HttpServletResponse response, boolean popup) throws IOException {
			wasAjaxRedirectInternalCalled = true;
		}

		public TestAjaxHandler getDelegate() {
			return delegate;
		}

		public boolean wasAjaxRedirectInternalCalled() {
			if (wasAjaxRedirectInternalCalled) {
				return true;
			} else {
				return (getDelegate() != null) ? delegate.wasAjaxRedirectInternalCalled() : false;
			}
		}
	}

}
