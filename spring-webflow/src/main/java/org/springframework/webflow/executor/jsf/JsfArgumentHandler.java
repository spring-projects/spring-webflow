package org.springframework.webflow.executor.jsf;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;

/**
 * JSF specific argument handler that ensures the JSF 1.2 "javax.faces.ViewState" parameter is maintained accross flow
 * execution redirects. Note that this argument handler does not rely on JSF 1.2 specific APIs, so it can be used safely
 * in a JSF 1.1 environment.
 * <p>
 * Maintaining the "javax.faces.ViewState" parameter is important because JSF uses it to tell whether or not a request
 * is a postback. If the request is a postback, the JSF {@link javax.faces.application.StateManager} will be used to
 * restore the JSF component states in the view, otherwise state is lost. The actual value of the parameter is not
 * defined by the JSF specification.
 * <p>
 * Note that the "javax.faces.ViewState" parameter will only be added to
 * {@link #createFlowExecutionUrl(String, FlowExecutionContext, ExternalContext) flow execution URLs}. It doesn't make
 * sense to add it to
 * {@link #createFlowDefinitionUrl(org.springframework.webflow.execution.support.FlowDefinitionRedirect, ExternalContext) flow definition URLs}
 * or
 * {@link #createExternalUrl(org.springframework.webflow.execution.support.ExternalRedirect, String, ExternalContext) external URLs},
 * since we're clearly not in a postback situation in those cases.
 * <p>
 * Also note that the resulting URL could potentially be too long when using JSF client-side state saving mode. In
 * general there are issues in trying to use alwaysRedirectOnPause with client-side state saving.
 * 
 * @since 1.0.6
 * 
 * @author Erwin Vervaet
 * @author Mike Seghers
 */
public class JsfArgumentHandler extends RequestParameterFlowExecutorArgumentHandler {

	// the ViewState parameter as defined by ResponseStateManager.VIEW_STATE_PARAM in JSF 1.2
	private static final String VIEW_STATE_PARAM = "javax.faces.ViewState";

	public String createFlowExecutionUrl(String flowExecutionKey, FlowExecutionContext flowExecution,
			ExternalContext context) {
		StringBuffer url = new StringBuffer(super.createFlowExecutionUrl(flowExecutionKey, flowExecution, context));
		String viewStateParam = context.getRequestParameterMap().get(VIEW_STATE_PARAM);
		if (viewStateParam != null) {
			url.append('&'); // the flow execution key is already there
			appendQueryParameter(url, VIEW_STATE_PARAM, viewStateParam);
		}
		return url.toString();
	}
}
