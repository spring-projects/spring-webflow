package org.springframework.webflow.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * Adapter between the Spring MVC Controller layer and the Spring Web Flow engine. This controller allows Spring Web
 * Flow to run in "embedded" mode as a Controller within a DispatcherServlet.
 */
public class FlowController extends AbstractController {

	private FlowExecutor flowExecutor;

	/**
	 * Creates a new flow controller.
	 */
	public FlowController() {
	}

	/**
	 * Sets the flow executor to delegate to. The FlowExecutor is the entry point into Spring Web Flow.
	 * @param flowExecutor the flow executor
	 */
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