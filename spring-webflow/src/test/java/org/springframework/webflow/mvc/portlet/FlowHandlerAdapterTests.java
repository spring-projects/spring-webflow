package org.springframework.webflow.mvc.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowHandlerAdapterTests extends TestCase {

	private FlowHandlerAdapter controller;
	private FlowExecutor flowExecutor;
	private MockPortletContext portletContext;
	private MockActionRequest actionRequest;
	private MockActionResponse actionResponse;
	private MockRenderRequest renderRequest;
	private MockRenderResponse renderResponse;
	private PortletExternalContext actionContext;
	private PortletExternalContext renderContext;
	private FlowHandler flowHandler;
	private LocalAttributeMap flowInput = null;
	private boolean handleException;
	private boolean handleExecutionOutcome;
	private boolean handleExecutionOutcomeCalled;

	protected void setUp() throws Exception {
		flowExecutor = (FlowExecutor) EasyMock.createMock(FlowExecutor.class);
		controller = new FlowHandlerAdapter() {
			protected PortletExternalContext createPortletExternalContext(PortletRequest request,
					PortletResponse response) {
				if (request instanceof ActionRequest) {
					return actionContext;
				} else {
					return renderContext;
				}
			}
		};
		controller.setFlowExecutor(flowExecutor);
		controller.setApplicationContext(new StaticWebApplicationContext());
		portletContext = new MockPortletContext();
		controller.setPortletContext(portletContext);
		controller.afterPropertiesSet();

		actionRequest = new MockActionRequest();
		actionResponse = new MockActionResponse();
		renderRequest = new MockRenderRequest();
		renderResponse = new MockRenderResponse();
		actionContext = new PortletExternalContext(portletContext, actionRequest, actionResponse, controller
				.getFlowUrlHandler());
		renderContext = new PortletExternalContext(portletContext, renderRequest, renderResponse, controller
				.getFlowUrlHandler());

		flowHandler = new FlowHandler() {
			public String getFlowId() {
				return "foo";
			}

			public MutableAttributeMap createExecutionInputMap(RenderRequest request) {
				return null;
			}

			public boolean handleExecutionOutcome(FlowExecutionOutcome outcome, ActionRequest request,
					ActionResponse response) {
				handleExecutionOutcomeCalled = true;
				if (handleExecutionOutcome) {
					return true;
				} else {
					return false;
				}
			}

			public String handleException(FlowException e, RenderRequest request, RenderResponse response) {
				if (handleException) {
					return "error";
				} else {
					return null;
				}
			}

		};
	}

	public void testLaunchFlowRequest() throws Exception {
		renderRequest.setContextPath("/springtravel");
		flowExecutor.launchExecution("foo", flowInput, renderContext);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowActionRequest() throws Exception {
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", actionContext);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRenderRequest() throws Exception {
		renderRequest.setContextPath("/springtravel");
		renderRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", renderContext);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleRender(renderRequest, renderResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRenderRequestFromSession() throws Exception {
		renderRequest.setContextPath("/springtravel");
		PortletSession session = renderRequest.getPortletSession();
		session.setAttribute("execution", "12345");
		flowExecutor.resumeExecution("12345", renderContext);
		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleRender(renderRequest, renderResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testDefaultHandleFlowException() throws Exception {
		PortletSession session = renderRequest.getPortletSession();
		final FlowException flowException = new FlowException("Error") {
		};
		session.setAttribute("actionRequestFlowException", flowException);
		try {
			controller.handleRender(renderRequest, renderResponse, flowHandler);
			fail("Should have thrown exception");
		} catch (FlowException e) {
			assertEquals(flowException, e);
		}
	}

	public void testDefaultHandleNoSuchFlowExecutionException() throws Exception {
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", actionContext);
		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		assertNotNull(actionRequest.getPortletSession().getAttribute("actionRequestFlowException"));
		EasyMock.verify(new Object[] { flowExecutor });
		Exception e = (Exception) actionRequest.getPortletSession().getAttribute("actionRequestFlowException");
		assertTrue(e instanceof NoSuchFlowExecutionException);
	}

	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		handleExecutionOutcome = true;
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");
		LocalAttributeMap output = new LocalAttributeMap();
		output.put("bar", "baz");
		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		flowExecutor.resumeExecution("12345", actionContext);
		EasyMock.expectLastCall().andReturn(FlowExecutionResult.createEndedResult("bar", outcome));
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		assertTrue(handleExecutionOutcomeCalled);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testHandleFlowExceptionCustomFlowHandler() throws Exception {
		handleException = true;
		final FlowException flowException = new FlowException("Error") {
		};
		renderRequest.setContextPath("/springtravel");
		flowExecutor.launchExecution("foo", flowInput, renderContext);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertNotNull(mv);
		assertEquals("error", mv.getViewName());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testHandleFlowExceptionFromSession() throws Exception {
		handleException = true;
		PortletSession session = renderRequest.getPortletSession();
		final FlowException flowException = new FlowException("Error") {
		};
		session.setAttribute("actionRequestFlowException", flowException);
		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertEquals("error", mv.getViewName());
	}

	public void testPopulateConveniencePortletProperties() {
		controller.populateConveniencePortletProperties(renderRequest);
		assertEquals(renderRequest.getPortletMode().toString(), renderRequest.getAttribute("portletMode"));
		assertEquals(renderRequest.getWindowState().toString(), renderRequest.getAttribute("portletWindowState"));
	}

}
