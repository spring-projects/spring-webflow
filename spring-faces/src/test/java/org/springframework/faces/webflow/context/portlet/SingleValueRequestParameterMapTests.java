package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class SingleValueRequestParameterMapTests extends TestCase {

	private SingleValueRequestParameterMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		this.request = new MockPortletRequest();
		this.requestMap = new SingleValueRequestParameterMap(this.request);
	}

	public void testSingleValueParameter() throws Exception {
		this.request.setParameter("key", "value");
		assertEquals("value", this.requestMap.getAttribute("key"));
	}

	public void testMultiValueParameterAsString() throws Exception {
		this.request.setParameter("key", "value");
		this.request.addParameter("key", "value2");
		Object actual = this.requestMap.getAttribute("key");
		assertEquals("value", actual);
	}
}
