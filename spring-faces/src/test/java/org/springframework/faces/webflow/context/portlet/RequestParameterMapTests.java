package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class RequestParameterMapTests extends TestCase {

	private RequestParameterMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new RequestParameterMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		requestMap = null;
	}

	public void testSingleValueParameter() throws Exception {
		request.setParameter("key", "value");
		assertEquals("value", requestMap.getAttribute("key"));
	}

	public void testMultiValueParameter() throws Exception {
		request.setParameter("key", "value");
		request.addParameter("key", "value2");
		Object actual = requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(2, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
		assertEquals("value2", ((String[]) actual)[1]);
	}

	public void testSingleValueParameterAsArray() throws Exception {
		request.setParameter("key", "value");
		requestMap.setUseArrayForMultiValueAttributes(Boolean.TRUE);
		Object actual = requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}

	public void testMultiValueParameterAsString() throws Exception {
		request.setParameter("key", "value");
		request.addParameter("key", "value2");
		requestMap.setUseArrayForMultiValueAttributes(Boolean.FALSE);
		Object actual = requestMap.getAttribute("key");
		assertEquals("value", actual);
	}

}
