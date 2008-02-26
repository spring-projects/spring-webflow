package org.springframework.webflow.mvc;

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
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowHandlerAdapterTests extends TestCase {
	private FlowHandlerAdapter controller;
	private FlowExecutor executor;
	private MockServletContext servletContext;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ServletExternalContext context;
	private FlowHandler flowHandler;
	private LocalAttributeMap flowInput;
	private boolean handleException;
	private boolean handleExecutionOutcome;

	protected void setUp() {
		executor = (FlowExecutor) EasyMock.createMock(FlowExecutor.class);
		controller = new FlowHandlerAdapter(executor) {
			protected ServletExternalContext createServletExternalContext(HttpServletRequest request,
					HttpServletResponse response) {
				return context;
			}
		};
		servletContext = new MockServletContext();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		context = new ServletExternalContext(servletContext, request, response, controller.getFlowUrlHandler());
		controller.setApplicationContext(new StaticWebApplicationContext());
		controller.setServletContext(servletContext);
		flowHandler = new FlowHandler() {
			public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
				assertEquals(FlowHandlerAdapterTests.this.request, request);
				return flowInput;
			}

			public String getFlowId() {
				return "foo";
			}

			public ModelAndView handleException(FlowException e, HttpServletRequest request,
					HttpServletResponse response) {
				if (handleException) {
					return new ModelAndView("error");
				} else {
					return null;
				}
			}

			public ModelAndView handleExecutionOutcome(String outcome, AttributeMap output, HttpServletRequest request,
					HttpServletResponse response) {
				if (handleExecutionOutcome) {
					return new ModelAndView("redirect:/home");
				} else {
					return null;
				}
			}

		};
	}

	public void testLaunchFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/whatever");
		request.setRequestURI("/springtravel/app/whatever");
		request.setMethod("GET");
		executor.launchExecution("foo", flowInput, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { executor });
	}

	public void testLaunchFlowRequestEndsAfterProcessing() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/whatever");
		request.setRequestURI("/springtravel/app/whatever");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		executor.launchExecution("foo", null, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		Event outcome = new Event(this, "finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
	}

	public void testResumeFlowRequest() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("POST");
		request.addParameter("execution", "12345");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		executor.resumeExecution("12345", context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { executor });
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
		executor.resumeExecution("12345", context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		Event outcome = new Event(this, "finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals("/springtravel/app/foo?bar=baz", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
	}

	public void testLaunchFlowWithExecutionRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		context.requestFlowExecutionRedirect();
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { executor });
		assertEquals("/springtravel/app/foo?execution=12345", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
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
		executor.launchExecution("foo", null, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		Event outcome = new Event(this, "finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { executor });
		assertEquals("/springtravel/app/bar?baz=boop", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
	}

	public void testLaunchFlowWithExternalRedirect() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		context.requestExternalRedirect("http://www.paypal.com");
		executor.launchExecution("foo", null, context);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { executor });
		assertEquals("http://www.paypal.com", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
	}

	public void testDefaultHandleFlowException() throws Exception {
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		Map parameters = new HashMap();
		request.setParameters(parameters);
		executor.launchExecution("foo", null, context);
		FlowException flowException = new FlowException("Error") {
		};
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { executor });
		try {
			controller.handle(request, response, flowHandler);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
		EasyMock.verify(new Object[] { executor });
	}

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
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNull(mv);
		assertEquals("/springtravel/app/foo", response.getRedirectedUrl());
		EasyMock.verify(new Object[] { executor });
	}

	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		handleExecutionOutcome = true;
		request.setContextPath("/springtravel");
		request.setServletPath("/app");
		request.setPathInfo("/foo");
		request.setRequestURI("/springtravel/app/foo");
		request.setMethod("GET");
		executor.launchExecution("foo", flowInput, context);
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		Event outcome = new Event(this, "finish", output);
		FlowExecutionResult result = FlowExecutionResult.createEndedResult("foo", outcome);
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNotNull(mv);
		EasyMock.verify(new Object[] { executor });
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
		executor.launchExecution("foo", null, context);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { executor });
		ModelAndView mv = controller.handle(request, response, flowHandler);
		assertNotNull(mv);
		EasyMock.verify(new Object[] { executor });
	}
}
