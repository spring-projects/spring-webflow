package org.springframework.webflow.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.context.servlet.DefaultFlowUrlHandler;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

public class FlowHandlerAdapter extends WebApplicationObjectSupport implements HandlerAdapter {

	private static final Log logger = LogFactory.getLog(FlowHandlerAdapter.class);

	private FlowExecutor flowExecutor;

	private FlowUrlHandler urlHandler;

	public FlowHandlerAdapter(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
		this.urlHandler = new DefaultFlowUrlHandler();
	}

	public FlowUrlHandler getFlowUrlHandler() {
		return urlHandler;
	}

	public void setFlowUrlHandler(FlowUrlHandler urlHandler) {
		this.urlHandler = urlHandler;
	}

	public boolean supports(Object handler) {
		return handler instanceof FlowHandler;
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		String flowExecutionKey = urlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			try {
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
				return handleFlowExecutionResult(result, context, request, response, flowHandler);
			} catch (FlowException e) {
				return handleFlowException(e, request, response, flowHandler);
			}
		} else {
			try {
				MutableAttributeMap input = flowHandler.createExecutionInputMap(request);
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.launchExecution(flowHandler.getFlowId(), input, context);
				return handleFlowExecutionResult(result, context, request, response, flowHandler);
			} catch (FlowException e) {
				return handleFlowException(e, request, response, flowHandler);
			}
		}
	}

	// subclassing hooks

	protected ServletExternalContext createServletExternalContext(HttpServletRequest request,
			HttpServletResponse response) {
		return new ServletExternalContext(getServletContext(), request, response, urlHandler);
	}

	protected MutableAttributeMap defaultFlowExecutionInputMap(HttpServletRequest request) {
		return new LocalAttributeMap(request.getParameterMap());
	}

	protected ModelAndView defaultHandleFlowOutcome(String flowId, String outcome, AttributeMap endedOutput,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		// by default, just start the flow over passing the output as input
		if (logger.isDebugEnabled()) {
			logger.debug("Restarting a new execution of ended flow '" + flowId + "'");
		}
		response.sendRedirect(urlHandler.createFlowDefinitionUrl(flowId, endedOutput, request));
		return null;
	}

	protected ModelAndView defaultHandleFlowException(String flowId, FlowException e, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (e instanceof NoSuchFlowExecutionException && flowId != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Restarting a new execution of previously expired/ended flow '" + flowId + "'");
			}
			// by default, attempt to restart the flow
			response.sendRedirect(urlHandler.createFlowDefinitionUrl(flowId, null, request));
			return null;
		} else {
			throw e;
		}
	}

	// internal helpers

	private ModelAndView handleFlowExecutionResult(FlowExecutionResult result, ServletExternalContext context,
			HttpServletRequest request, HttpServletResponse response, FlowHandler handler) throws IOException {
		if (result.paused()) {
			if (context.flowExecutionRedirectRequested()) {
				String url = urlHandler.createFlowExecutionUrl(result.getFlowId(), result.getPausedKey(), request);
				if (logger.isDebugEnabled()) {
					logger.debug("Sending flow execution redirect to " + url);
				}
				response.sendRedirect(url);
				return null;
			} else if (context.externalRedirectRequested()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Sending external redirect to " + context.getExternalRedirectUrl());
				}
				response.sendRedirect(context.getExternalRedirectUrl());
				return null;
			} else {
				return null;
			}
		} else if (result.ended()) {
			if (context.flowDefinitionRedirectRequested()) {
				String flowId = context.getFlowRedirectFlowId();
				AttributeMap input = context.getFlowRedirectFlowInput();
				String url = urlHandler.createFlowDefinitionUrl(flowId, input, request);
				if (logger.isDebugEnabled()) {
					logger.debug("Sending flow definition to " + url);
				}
				response.sendRedirect(url);
				return null;
			} else if (context.externalRedirectRequested()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Sending external redirect to " + context.getExternalRedirectUrl());
				}
				response.sendRedirect(context.getExternalRedirectUrl());
				return null;
			} else {
				ModelAndView mv = handler.handleExecutionOutcome(result.getEndedOutcome(), result.getEndedOutput(),
						request, response);
				return mv != null ? mv : defaultHandleFlowOutcome(handler.getFlowId(), result.getEndedOutcome(), result
						.getEndedOutput(), request, response);
			}
		} else {
			throw new IllegalStateException("Execution result should have been one of [paused] or [ended]");
		}
	}

	private ModelAndView handleFlowException(FlowException e, HttpServletRequest request, HttpServletResponse response,
			FlowHandler handler) throws IOException {
		ModelAndView result = handler.handleException(e, request, response);
		return result != null ? result : defaultHandleFlowException(handler.getFlowId(), e, request, response);
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}

}
