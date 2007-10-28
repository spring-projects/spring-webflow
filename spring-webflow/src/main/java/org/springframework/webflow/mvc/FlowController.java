package org.springframework.webflow.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.FlowExecutor;

public class FlowController extends AbstractController {

	private FlowExecutor flowExecutor;

	public FlowController() {
	}

	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ServletExternalContext context = new ServletExternalContext(getServletContext(), request, response);
		context.executeFlowRequest(flowExecutor);
		return null;
	}
}
