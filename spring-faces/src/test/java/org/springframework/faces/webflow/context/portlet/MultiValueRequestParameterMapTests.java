package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class MultiValueRequestParameterMapTests extends TestCase {

	private MultiValueRequestParameterMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		this.request = new MockPortletRequest();
		this.requestMap = new MultiValueRequestParameterMap(this.request);
	}

	public void testMultiValueParameter() throws Exception {
		this.request.setParameter("key", "value");
		this.request.addParameter("key", "value2");
		Object actual = this.requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(2, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
		assertEquals("value2", ((String[]) actual)[1]);
	}

	public void testSingleValueParameterAsArray() throws Exception {
		this.request.setParameter("key", "value");
		Object actual = this.requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}

}
