package org.springframework.webflow.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerAdapter;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.context.PortletApplicationObjectSupport;
import org.springframework.webflow.context.portlet.FlowUrlHandler;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

public class FlowHandlerAdapter extends PortletApplicationObjectSupport implements HandlerAdapter {

	private static final String FLOW_EXECUTION_RESULT = "flowExecutionResult";

	private FlowExecutor flowExecutor;

	private FlowUrlHandler urlHandler;

	public FlowHandlerAdapter(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		String flowExecutionKey = urlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			PortletExternalContext context = createPortletExternalContext(request, response);
			flowExecutor.resumeExecution(flowExecutionKey, context);
			return null;
		} else {
			PortletSession session = request.getPortletSession(true);
			FlowExecutionResult result = (FlowExecutionResult) session.getAttribute(FLOW_EXECUTION_RESULT);
			if (result != null) {
				session.removeAttribute(FLOW_EXECUTION_RESULT);
				return flowHandler.handleFlowOutcome(result.getEndedOutcome(), result.getEndedOutput(), request,
						response);
			} else {
				MutableAttributeMap input = flowHandler.createExecutionInputMap(request);
				PortletExternalContext context = createPortletExternalContext(request, response);
				flowExecutor.launchExecution(flowHandler.getFlowId(), input, context);
				return null;
			}
		}
	}

	public void handleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {
		String flowExecutionKey = urlHandler.getFlowExecutionKey(request);
		PortletExternalContext context = createPortletExternalContext(request, response);
		FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
		if (result.paused()) {
			urlHandler.setFlowExecutionRenderParameter(flowExecutionKey, response);
		} else {
			setEndResult(result, request);
		}
	}

	private void setEndResult(FlowExecutionResult result, ActionRequest request) {
		request.getPortletSession().setAttribute(FLOW_EXECUTION_RESULT, result);
	}

	public boolean supports(Object handler) {
		return handler instanceof FlowHandler;
	}

	private PortletExternalContext createPortletExternalContext(PortletRequest request, PortletResponse response) {
		return new PortletExternalContext(getPortletContext(), request, response);
	}
}
