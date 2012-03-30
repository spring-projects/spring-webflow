package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class SingleValueRequestParameterMapTests extends TestCase {

	private SingleValueRequestParameterMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new SingleValueRequestParameterMap(request);
	}

	public void testSingleValueParameter() throws Exception {
		request.setParameter("key", "value");
		assertEquals("value", requestMap.getAttribute("key"));
	}

	public void testMultiValueParameterAsString() throws Exception {
		request.setParameter("key", "value");
		request.addParameter("key", "value2");
		Object actual = requestMap.getAttribute("key");
		assertEquals("value", actual);
	}
}
