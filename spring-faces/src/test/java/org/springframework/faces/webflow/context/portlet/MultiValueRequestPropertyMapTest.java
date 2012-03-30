package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class MultiValueRequestPropertyMapTest extends TestCase {

	private MultiValueRequestPropertyMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new MultiValueRequestPropertyMap(request);
	}

	public void testMultiValueProperty() throws Exception {
		request.setProperty("key", "value");
		request.addProperty("key", "value2");
		Object actual = requestMap.getAttribute("key");
		assertEquals(2, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
		assertEquals("value2", ((String[]) actual)[1]);
	}

	public void testSingleValuePropertyAsArray() throws Exception {
		request.setProperty("key", "value");
		Object actual = requestMap.getAttribute("key");
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}
}
