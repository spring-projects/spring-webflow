package org.springframework.webflow.context.servlet;

import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;

public class DefaultFlowUrlHandlerTests extends TestCase {
	private DefaultFlowUrlHandler urlHandler = new DefaultFlowUrlHandler();
	private MockHttpServletRequest request = new MockHttpServletRequest();

	public void testGetFlowId() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	public void testGetFlowExecutionKey() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.addParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(request));
	}

	public void testCreateFlowDefinitionUrl() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", null, request);
		assertEquals("/springtravel/app/bookHotel", url);
	}

	public void testCreateFlowDefinitionUrlEmptyInput() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", CollectionUtils.EMPTY_ATTRIBUTE_MAP, request);
		assertEquals("/springtravel/app/bookHotel", url);
	}

	public void testCreateFlowDefinitionUrlWithFlowInput() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		LocalAttributeMap input = new LocalAttributeMap(new LinkedHashMap());
		input.put("foo", "bar");
		input.put("bar", "needs encoding");
		input.put("baz", new Integer(1));
		input.put("boop", null);
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", input, request);
		assertEquals("/springtravel/app/bookHotel?foo=bar&bar=needs+encoding&baz=1&boop=", url);
	}

	public void testCreateFlowExecutionUrl() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowExecutionUrl("foo", "12345", request);
		assertEquals("/springtravel/app/foo?execution=12345", url);
	}
}
