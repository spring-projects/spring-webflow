package org.springframework.webflow.mvc.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
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

public class FlowHandlerAdapterTests extends TestCase {
	private FlowHandlerAdapter flowHandlerAdapter;
	private FlowExecutor flowExecutor;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ServletExternalContext context;
	private FlowHandler flowHandler;
	private LocalAttributeMap flowInput = new LocalAttributeMap();
	private boolean handleException;
	private boolean handleExecutionOutcome;

	protected void setUp() throws Exception {
		flowExecutor = (FlowExecutor) EasyMock.createMock(FlowExecutor.class);
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
			public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
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
	}

	public void testLaunchFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/whatever");
		request.setRequestURI("/springtravel/app/whatever");
		request.setMethod("GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowRequestEndsAfterProcessing() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/whatever");
		request.setRequestURI("/springtravel/app/whatever");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRequestEndsAfterProcessing() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		flowExecutor.resumeExecution("12345", context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRequestEndsAfterProcessingFlowCommittedResponse() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		flowExecutor.resumeExecution("12345", context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		context.recordResponseComplete();
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals(null, response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExecutionRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestFlowExecutionRedirect();
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/app/foo?execution=12345", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithDefinitionRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("baz", "boop");
		context.requestFlowDefinitionRedirect("bar", input);
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		ModelAndView mv = flowHandlerAdapter.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/app/bar?baz=boop", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalHttpRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("http://www.paypal.com");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("http://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalHttpsRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("https://www.paypal.com");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("https://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectServletRelative() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("servletRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/app/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectServletRelativeWithSlash() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("servletRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/app/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectContextRelative() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("contextRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectContextRelativeWithSlash() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("contextRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/springtravel/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectServerRelative() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("serverRelative:bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectServerRelativeWithSlash() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("serverRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals("/bar", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowWithExternalRedirectNotHttp10Compatible() throws Exception {
		flowHandlerAdapter.setRedirectHttp10Compatible(false);
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		context.requestExternalRedirect("serverRelative:/bar");
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
		assertEquals(303, response.getStatus());
		assertEquals("/bar", response.getHeader("Location"));
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testDefaultHandleFlowException() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		flowExecutor.launchExecution("foo", flowInput, context);
		FlowException flowException = new FlowException("Error") {
		};
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		try {
			flowHandlerAdapter.handle(request, response, flowHandler);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testDefaultHandleNoSuchFlowExecutionException() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		request.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", context);
		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		assertEquals("/springtravel/app/foo", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		handleExecutionOutcome = true;
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testHandleFlowExceptionCustomFlowHandler() throws Exception {
		handleException = true;
		final FlowException flowException = new FlowException("Error") {
		};
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		flowExecutor.launchExecution("foo", flowInput, context);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		flowHandlerAdapter.handle(request, response, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}
}
