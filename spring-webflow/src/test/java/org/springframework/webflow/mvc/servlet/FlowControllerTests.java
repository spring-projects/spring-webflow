package org.springframework.webflow.mvc.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.context.servlet.DefaultAjaxHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowControllerTests {
	private FlowController controller;
	private FlowExecutor executor;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ServletExternalContext context;

	@BeforeEach
	public void setUp() throws Exception {
		executor = EasyMock.createMock(FlowExecutor.class);
		controller = new FlowController();
		FlowHandlerAdapter handlerAdapter = new FlowHandlerAdapter() {
			protected ServletExternalContext createServletExternalContext(HttpServletRequest request,
					HttpServletResponse response) {
				return context;
			}
		};
		handlerAdapter.setFlowExecutor(executor);
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		MockServletContext servletContext = new MockServletContext();
		applicationContext.setServletContext(servletContext);
		handlerAdapter.setApplicationContext(applicationContext);
		handlerAdapter.afterPropertiesSet();

		controller.setFlowHandlerAdapter(handlerAdapter);
		controller.setApplicationContext(new StaticWebApplicationContext());
		controller.afterPropertiesSet();

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		context = new ServletExternalContext(servletContext, request, response, controller.getFlowUrlHandler());
	}

	@Test
	public void testLaunchFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowRequestEndsAfterProcessing() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", null, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testResumeFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		executor.resumeExecution("12345", context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		EasyMock.verify(executor);
	}

	@Test
	public void testResumeFlowRequestEndsAfterProcessing() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		executor.resumeExecution("12345", context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithExecutionRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestFlowExecutionRedirect();
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?execution=12345", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithExecutionRedirectAjaxHeaderOpenInPopup() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		request.addHeader("Accept", "text/html;type=ajax");
		context.setAjaxRequest(true);
		context.requestFlowExecutionRedirect();
		context.requestRedirectInPopup();
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals(null, response.getRedirectedUrl());
		assertEquals("true", response.getHeader(DefaultAjaxHandler.POPUP_VIEW_HEADER));
		assertEquals("/springtravel/app/foo?execution=12345",
				response.getHeader(DefaultAjaxHandler.REDIRECT_URL_HEADER));
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithExecutionRedirectAjaxParameter() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		request.addParameter("ajaxSource", "this");
		context.setAjaxRequest(true);
		context.requestFlowExecutionRedirect();
		LocalAttributeMap<Object> inputMap = new LocalAttributeMap<>();
		inputMap.put("ajaxSource", "this");
		executor.launchExecution("foo", inputMap, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals(null, response.getRedirectedUrl());
		assertEquals(null, response.getHeader(DefaultAjaxHandler.POPUP_VIEW_HEADER));
		assertEquals("/springtravel/app/foo?execution=12345",
				response.getHeader(DefaultAjaxHandler.REDIRECT_URL_HEADER));
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithDefinitionRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		LocalAttributeMap<Object> input = new LocalAttributeMap<>();
		input.put("baz", "boop");
		context.requestFlowDefinitionRedirect("bar", input);
		executor.launchExecution("foo", null, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		EasyMock.verify(executor);
		assertEquals("/springtravel/app/bar?baz=boop", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("https://www.paypal.com");
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		EasyMock.verify(executor);
		assertEquals("https://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testDefaultHandleFlowException() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", null, context);
		FlowException flowException = new FlowException("Error") {
		};
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(executor);
		try {
			controller.handleRequest(request, response);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
		EasyMock.verify(executor);
	}

	@Test
	public void testDefaultHandleNoSuchFlowExecutionException() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		request.addParameter("execution", "12345");
		executor.resumeExecution("12345", context);
		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals("/springtravel/app/foo", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testLaunchFlowWithCustomFlowHandler() throws Exception {
		final LocalAttributeMap<Object> input = new LocalAttributeMap<>();
		input.put("bar", "boop");
		controller.registerFlowHandler(new FlowHandler() {
			public String getFlowId() {
				return "foo";
			}

			public MutableAttributeMap<Object> createExecutionInputMap(HttpServletRequest request) {
				return input;
			}

			public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
					HttpServletResponse response) {
				return null;
			}

			public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
				return null;
			}
		});
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", input, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		EasyMock.verify(executor);
	}

	@Test
	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		final LocalAttributeMap<Object> input = new LocalAttributeMap<>();
		input.put("bar", "boop");
		controller.registerFlowHandler(new FlowHandler() {
			public String getFlowId() {
				return "foo";
			}

			public MutableAttributeMap<Object> createExecutionInputMap(HttpServletRequest request) {
				return input;
			}

			public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
					HttpServletResponse response) {
				assertEquals("finish", outcome.getId());
				assertEquals("baz", outcome.getOutput().get("bar"));
				assertEquals(FlowControllerTests.this.request, request);
				assertEquals(FlowControllerTests.this.response, response);
				return null;
			}

			public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
				return null;
			}
		});
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", input, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(executor);
		ModelAndView mv = controller.handleRequest(request, response);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(executor);
	}

	@Test
	public void testHandleFlowExceptionCustomFlowHandler() throws Exception {
		final FlowException flowException = new FlowException("Error") {
		};

		controller.registerFlowHandler(new FlowHandler() {
			public String getFlowId() {
				return "foo";
			}

			public MutableAttributeMap<Object> createExecutionInputMap(HttpServletRequest request) {
				return null;
			}

			public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
					HttpServletResponse response) {
				return null;
			}

			public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
				assertEquals(flowException, e);
				return null;
			}
		});
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", null, context);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(executor);
		try {
			controller.handleRequest(request, response);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
		EasyMock.verify(executor);
	}
}
