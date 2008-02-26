package org.springframework.webflow.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
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

/**
 * Adapter between the Spring MVC Controller layer and the Spring Web Flow engine. This controller allows Spring Web
 * Flow to run in "embedded" mode as a Controller within a DispatcherServlet.
 */
public class FlowController extends AbstractController {

	private FlowExecutor flowExecutor;

	private FlowUrlHandler urlHandler;

	private Map flowHandlers = new HashMap();

	/**
	 * @param flowExecutor the web flow executor service
	 */
	public FlowController(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
		this.urlHandler = new DefaultFlowUrlHandler();
		// set the cache seconds property to 0 so no pages are cached by default for flows
		setCacheSeconds(0);
	}

	public FlowUrlHandler getFlowUrlHandler() {
		return urlHandler;
	}

	public void setFlowRequestUrlHandler(FlowUrlHandler urlHandler) {
		this.urlHandler = urlHandler;
	}

	public void registerFlowHandler(FlowHandler handler) {
		flowHandlers.put(handler.getFlowId(), handler);
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String flowExecutionKey = urlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			try {
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
				return handleFlowExecutionResult(result, context, request, response);
			} catch (FlowException e) {
				return handleFlowException(e, request, response);
			}
		} else {
			try {
				String flowId = urlHandler.getFlowId(request);
				MutableAttributeMap input = getFlowInput(flowId, request);
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.launchExecution(flowId, input, context);
				return handleFlowExecutionResult(result, context, request, response);
			} catch (FlowException e) {
				return handleFlowException(e, request, response);
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
		response.sendRedirect(urlHandler.createFlowDefinitionUrl(flowId, endedOutput, request));
		return null;
	}

	protected ModelAndView defaultHandleFlowException(String flowId, FlowException e, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (e instanceof NoSuchFlowExecutionException && flowId != null) {
			// by default, attempt to restart the flow
			response.sendRedirect(urlHandler.createFlowDefinitionUrl(flowId, null, request));
			return null;
		} else {
			throw e;
		}
	}

	// internal helpers

	private ModelAndView handleFlowExecutionResult(FlowExecutionResult result, ServletExternalContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (result.paused()) {
			if (context.flowExecutionRedirectRequested()) {
				String url = urlHandler.createFlowExecutionUrl(result.getFlowId(), result.getPausedKey(), request);
				response.sendRedirect(url);
				return null;
			} else if (context.externalRedirectRequested()) {
				response.sendRedirect(context.getExternalRedirectUrl());
				return null;
			} else {
				// nothing to do: flow has handled the response
				return null;
			}
		} else if (result.ended()) {
			if (context.flowDefinitionRedirectRequested()) {
				String flowId = context.getFlowRedirectFlowId();
				AttributeMap input = context.getFlowRedirectFlowInput();
				response.sendRedirect(urlHandler.createFlowDefinitionUrl(flowId, input, request));
				return null;
			} else if (context.externalRedirectRequested()) {
				response.sendRedirect(context.getExternalRedirectUrl());
				return null;
			} else {
				return handleFlowOutcome(result.getFlowId(), result.getEndedOutcome(), result.getEndedOutput(),
						request, response);
			}
		} else {
			throw new IllegalStateException("Execution result should have been one of [paused] or [ended]");
		}
	}

	private MutableAttributeMap getFlowInput(String flowId, HttpServletRequest request) {
		FlowHandler handler = getFlowHandler(flowId);
		if (handler != null) {
			return handler.createExecutionInputMap(request);
		} else {
			return defaultFlowExecutionInputMap(request);
		}
	}

	private ModelAndView handleFlowOutcome(String flowId, String outcome, AttributeMap endedOutput,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		FlowHandler handler = getFlowHandler(flowId);
		if (handler != null) {
			ModelAndView result = handler.handleExecutionOutcome(outcome, endedOutput, request, response);
			return result != null ? result : defaultHandleFlowOutcome(flowId, outcome, endedOutput, request, response);
		} else {
			return defaultHandleFlowOutcome(flowId, outcome, endedOutput, request, response);
		}
	}

	private ModelAndView handleFlowException(FlowException e, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String flowId = urlHandler.getFlowId(request);
		if (flowId != null) {
			FlowHandler handler = getFlowHandler(flowId);
			if (handler != null) {
				ModelAndView result = handler.handleException(e, request, response);
				return result != null ? result : defaultHandleFlowException(flowId, e, request, response);
			} else {
				return defaultHandleFlowException(flowId, e, request, response);
			}
		} else {
			return defaultHandleFlowException(null, e, request, response);
		}
	}

	private FlowHandler getFlowHandler(String flowId) {
		return (FlowHandler) flowHandlers.get(flowId);
	}
}