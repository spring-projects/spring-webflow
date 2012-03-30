package org.springframework.faces.webflow.context.portlet;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletRequest;

public class MultiValueRequestParameterMapTests extends TestCase {

	private MultiValueRequestParameterMap requestMap;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		requestMap = new MultiValueRequestParameterMap(request);
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
		Object actual = requestMap.getAttribute("key");
		assertTrue(actual.getClass().isArray());
		assertEquals(1, ((String[]) actual).length);
		assertEquals("value", ((String[]) actual)[0]);
	}

}
