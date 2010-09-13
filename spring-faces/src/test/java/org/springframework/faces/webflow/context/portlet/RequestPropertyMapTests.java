package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class RequestPropertyMapTests extends TestCase {

	private RequestPropertyMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new RequestPropertyMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		requestMap = null;
	}

	public void testSingleValueProperty() throws Exception {
		request.setProperty("key", "value");
		assertEquals("value", requestMap.getAttribute("key"));
	}

	public void testMultiValueProperty() throws Exception {
		request.setProperty("key", "value");
		request.addProperty("key", "value2");
		Object actual = requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(2, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
		assertEquals("value2", ((String[]) actual)[1]);
	}

	public void testSingleValuePropertyAsArray() throws Exception {
		request.setProperty("key", "value");
		requestMap.setUseArrayForMultiValueAttributes(Boolean.TRUE);
		Object actual = requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}

	public void testMultiValuePropertyAsString() throws Exception {
		request.setProperty("key", "value");
		request.addProperty("key", "value2");
		requestMap.setUseArrayForMultiValueAttributes(Boolean.FALSE);
		Object actual = requestMap.getAttribute("key");
		assertEquals("value", actual);
	}
}
