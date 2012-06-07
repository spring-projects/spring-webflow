package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class MultiValueRequestPropertyMapTests extends TestCase {

	private MultiValueRequestPropertyMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		this.request = new MockPortletRequest();
		this.requestMap = new MultiValueRequestPropertyMap(this.request);
	}

	public void testMultiValueProperty() throws Exception {
		this.request.setProperty("key", "value");
		this.request.addProperty("key", "value2");
		Object actual = this.requestMap.getAttribute("key");
		assertEquals(2, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
		assertEquals("value2", ((String[]) actual)[1]);
	}

	public void testSingleValuePropertyAsArray() throws Exception {
		this.request.setProperty("key", "value");
		Object actual = this.requestMap.getAttribute("key");
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}
}
