package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class SingleValueRequestPropertyMapTests extends TestCase {

	private SingleValueRequestPropertyMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new SingleValueRequestPropertyMap(request);
	}

	public void testSingleValueProperty() throws Exception {
		request.setProperty("key", "value");
		assertEquals("value", requestMap.getAttribute("key"));
	}

	public void testMultiValuePropertyAsString() throws Exception {
		request.setProperty("key", "value");
		request.addProperty("key", "value2");
		Object actual = requestMap.getAttribute("key");
		assertEquals("value", actual);
	}
}
