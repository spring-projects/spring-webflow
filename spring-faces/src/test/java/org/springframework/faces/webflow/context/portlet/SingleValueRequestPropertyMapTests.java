package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class SingleValueRequestPropertyMapTests extends TestCase {

	private SingleValueRequestPropertyMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		this.request = new MockPortletRequest();
		this.requestMap = new SingleValueRequestPropertyMap(this.request);
	}

	public void testSingleValueProperty() throws Exception {
		this.request.setProperty("key", "value");
		assertEquals("value", this.requestMap.getAttribute("key"));
	}

	public void testMultiValuePropertyAsString() throws Exception {
		this.request.setProperty("key", "value");
		this.request.addProperty("key", "value2");
		Object actual = this.requestMap.getAttribute("key");
		assertEquals("value", actual);
	}
}
