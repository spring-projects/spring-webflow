package org.springframework.webflow.context.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;

public class DefaultFlowUrlHandlerTests {
	private DefaultFlowUrlHandler urlHandler = new DefaultFlowUrlHandler();

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void testGetFlowId() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		assertEquals("foo", urlHandler.getFlowId(request));
	}

	@Test
	public void testGetFlowIdNoPathInfo() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app/foo.htm");
		request.setPathInfo(null);
		request.setRequestURI("/springtravel/app/foo.htm");
		assertEquals("app/foo", urlHandler.getFlowId(request));
	}

	@Test
	public void testGetFlowIdOnlyContextPath() {
		request.setContextPath("/springtravel");
		request.setRequestURI("/springtravel");
		assertEquals("springtravel", urlHandler.getFlowId(request));
	}

	@Test
	public void testGetFlowExecutionKey() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.addParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(request));
	}

	@Test
	public void testCreateFlowDefinitionUrl() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", null, request);
		assertEquals("/springtravel/app/bookHotel", url);
	}

	@Test
	public void testCreateFlowDefinitionUrlNoPathInfo() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app/foo.htm");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowDefinitionUrl("app/foo", null, request);
		assertEquals("/springtravel/app/foo.htm", url);
	}

	@Test
	public void testCreateFlowDefinitionUrlContextPathOnly() {
		request.setContextPath("/springtravel");
		request.setRequestURI("/springtravel");
		String url = urlHandler.createFlowDefinitionUrl("springtravel", null, request);
		assertEquals("/springtravel", url);
	}

	@Test
	public void testCreateFlowDefinitionUrlEmptyInput() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", CollectionUtils.EMPTY_ATTRIBUTE_MAP, request);
		assertEquals("/springtravel/app/bookHotel", url);
	}

	@Test
	public void testCreateFlowDefinitionUrlWithFlowInput() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		LocalAttributeMap<Object> input = new LocalAttributeMap<>(new LinkedHashMap<>());
		input.put("foo", "bar");
		input.put("bar", "needs encoding");
		input.put("baz", 1);
		input.put("boop", null);
		String url = urlHandler.createFlowDefinitionUrl("bookHotel", input, request);
		assertEquals("/springtravel/app/bookHotel?foo=bar&bar=needs+encoding&baz=1&boop=", url);
	}

	@Test
	public void testCreateFlowExecutionUrl() {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		String url = urlHandler.createFlowExecutionUrl("foo", "12345", request);
		assertEquals("/springtravel/app/foo?execution=12345", url);
	}
}
