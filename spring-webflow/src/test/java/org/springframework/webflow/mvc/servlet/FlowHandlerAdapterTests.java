package org.springframework.webflow.mvc.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
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

public class FlowHandlerAdapterTests {
	private FlowHandlerAdapter flowHandlerAdapter;
	private FlowExecutor flowExecutor;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ServletExternalContext context;
	private FlowHandler flowHandler;
	private LocalAttributeMap<Object> flowInput = new LocalAttributeMap<>();
	private boolean handleException;
	private boolean handleExecutionOutcome;
	private MockFlashMapManager flashMapManager = new MockFlashMapManager();

	@BeforeEach
	public void setUp() throws Exception {
		flowExecutor = EasyMock.createMock(FlowExecutor.class);
		flowHandlerAdapter = new FlowHandlerAdapter() {
			protected ServletExternalContext createServletExternalContext(HttpServletRequest request,
					HttpServletResponse response) {
				return context;
			}
		};
		flowHandlerAdapter.setFlowExecutor(flowExecutor);
		MockServletContext servletContext = new MockServletContext();
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		applicationContext.setServletContext(servletContext);
		flowHandlerAdapter.setApplicationContext(applicationContext);
		flowHandlerAdapter.afterPropertiesSet();

		flowHandler = new FlowHandler() {
			public MutableAttributeMap<Object> createExecutionInputMap(HttpServletRequest request) {
				assertEquals(FlowHandlerAdapterTests.this.request, request);
				return flowInput;
			}

			public String getFlowId() {
				return "foo";
			}

			public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
					HttpServletResponse response) {
				if (handleExecutionOutcome) {
					return "/home";
				} else {
					return null;
				}
			}

			public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
				if (handleException) {
					return "error";
				} else {
					return null;
				}
			}
		};
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		context = new ServletExternalContext(servletContext, request, response, flowHandlerAdapter.getFlowUrlHandler());
		request.setAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE, flashMapManager);
	}

	@Test
	public void testLaunchFlowRequest() throws Exception {
		setupRequest("/springtravel", "/app", "/whatever", "GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowRequestEndsAfterProcessing() throws Exception {
		setupRequest("/springtravel", "/app", "/whatever", "GET");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowRequestEndsAfterProcessingAjaxRequest() throws Exception {
		setupRequest("/springtravel", "/app", "/whatever", "GET");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		context.setAjaxRequest(true);
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		request.addHeader("Accept", "text/html;type=ajax");
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo?bar=baz", response.getHeader("Spring-Redirect-URL"));
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testResumeFlowRequest() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "POST");
		request.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testResumeFlowRequestEndsAfterProcessing() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "POST");
		request.addParameter("execution", "12345");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		flowExecutor.resumeExecution("12345", context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testResumeFlowRequestEndsAfterProcessingFlowCommittedResponse() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "POST");
		request.addParameter("execution", "12345");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		flowExecutor.resumeExecution("12345", context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		context.recordResponseComplete();
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals(null, response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExecutionRedirect() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestFlowExecutionRedirect();
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/app/foo?execution=12345", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithDefinitionRedirect() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		LocalAttributeMap<Object> input = new LocalAttributeMap<>();
		input.put("baz", "boop");
		context.requestFlowDefinitionRedirect("bar", input);
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/app/bar?baz=boop", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalHttpRedirect() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("https://www.paypal.com");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("https://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalHttpsRedirect() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("https://www.paypal.com");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("https://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectServletRelative() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("servletRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/app/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectServletRelativeWithSlash() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("servletRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/app/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectContextRelative() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("contextRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectContextRelativeWithSlash() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("contextRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectServerRelative() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("serverRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectServerRelativeWithSlash() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("serverRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testLaunchFlowWithExternalRedirectNotHttp10Compatible() throws Exception {
		flowHandlerAdapter.setRedirectHttp10Compatible(false);
		setupRequest("/springtravel", "/app", "/foo", "GET");
		context.requestExternalRedirect("serverRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals(303, response.getStatus());
		assertEquals("/bar", response.getHeader("Location"));
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testSwf1385DefaultServletExternalRedirect() throws Exception {
		setupRequest("/springtravel", "/foo", null, "GET");
		context.requestExternalRedirect("/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testSwf1385DefaultServletExternalRedirectDeviation() throws Exception {
		// Deviation from the default case:
		// In some containers the default behavior can be switched so that the contents of the URI after
		// the context path is in the path info while the servlet path is empty.
		setupRequest("/springtravel", "", "/foo", "GET");
		context.requestExternalRedirect("/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testSwf1385DefaultServletExternalRedirectServletRelative() throws Exception {
		setupRequest("/springtravel", "/foo", null, "GET");
		context.requestExternalRedirect("/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testExternalRedirectServletRelativeWithDefaultServletMapping() throws Exception {
		setupRequest("/springtravel", "/foo", null, "GET");
		context.requestExternalRedirect("servletRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
		assertEquals("/springtravel/foo/bar", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testRemoteHost() throws Exception {
		assertFalse(flowHandlerAdapter.isRemoteHost("https://url.somewhere.com"));
		assertFalse(flowHandlerAdapter.isRemoteHost("/path"));
		assertFalse(flowHandlerAdapter.isRemoteHost("http://url.somewhereelse.com"));

		flowHandlerAdapter.setHosts(new String[] {"url.somewhere.com"});

		assertFalse(flowHandlerAdapter.isRemoteHost("https://url.somewhere.com"));
		assertFalse(flowHandlerAdapter.isRemoteHost("/path"));
		assertTrue(flowHandlerAdapter.isRemoteHost("http://url.somewhereelse.com"));

	}

	@Test
	public void testDefaultHandleFlowException() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		Map<String, String> parameters = new HashMap<>();
		request.setParameters(parameters);
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowException flowException = new FlowException("Error") {
		};
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(flowExecutor);
		try {
			flowHandlerAdapter.handle(request, response, flowHandler);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testDefaultHandleNoSuchFlowExecutionException() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		request.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", context);
		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo", response.getRedirectedUrl());
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testDefaultHandleNoSuchFlowExecutionExceptionAjaxRequest() throws Exception {
		setupRequest("/springtravel", "/app", "/foo", "GET");
		request.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", context);
		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(flowExecutor);
		context.setAjaxRequest(true);
		request.addHeader("Accept", "text/html;type=ajax");
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo", response.getHeader("Spring-Redirect-URL"));
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		doHandleFlowServletRedirectOutcome();
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testHandleFlowExceptionCustomFlowHandler() throws Exception {
		handleException = true;
		final FlowException flowException = new FlowException("Error") {
		};
		setupRequest("/springtravel", "/app", "/foo", "GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
	}

	@Test
	public void testHandleFlowServletRedirectOutcomeWithoutFlash() throws Exception {
		doHandleFlowServletRedirectOutcome();
		assertNull(flashMapManager.getFlashMap());
	}

	@Test
	public void testHandleFlowServletRedirectOutcomeWithFlash() throws Exception {
		flowHandlerAdapter.setSaveOutputToFlashScopeOnRedirect(true);
		doHandleFlowServletRedirectOutcome();
		assertEquals("baz", flashMapManager.getFlashMap().get("bar"));
		assertEquals("/springtravel/app/home", flashMapManager.getFlashMap().getTargetRequestPath());
	}

	private void doHandleFlowServletRedirectOutcome() throws Exception {
		handleExecutionOutcome = true;
		setupRequest("/springtravel", "/app", "/foo", "GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(flowExecutor);
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(flowExecutor);
	}

	private void setupRequest(String contextPath, String servletPath, String pathInfo, String method) {
		request.setContextPath(contextPath);
		request.setServletPath(servletPath);
		request.setPathInfo(pathInfo);
		request.setRequestURI(contextPath + servletPath + (pathInfo == null ? "" : pathInfo));
		request.setMethod(method);
	}

	private static class MockFlashMapManager implements FlashMapManager {

		private FlashMap flashMap;

		public FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response) {
			throw new UnsupportedOperationException();
		}

		public void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response) {
			this.flashMap = flashMap;
		}

		public FlashMap getFlashMap() {
			return flashMap;
		}
	}
}
