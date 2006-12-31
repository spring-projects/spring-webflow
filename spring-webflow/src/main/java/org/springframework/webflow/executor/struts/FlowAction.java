/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.executor.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.struts.ActionSupport;
import org.springframework.web.struts.DelegatingActionProxy;
import org.springframework.web.struts.SpringBindingActionForm;
import org.springframework.web.util.WebUtils;
import org.springframework.webflow.action.FormObjectAccessor;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.FlowRequestHandler;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;

/**
 * Point of integration between Struts and Spring Web Flow: a Struts Action that
 * acts a front controller entry point into the web flow system. A single
 * FlowAction may launch any new FlowExecution. In addition, a single Flow
 * Action may signal events in any existing/restored FlowExecutions.
 * <p>
 * Requests are managed by and delegated to a {@link FlowExecutor}, which this
 * class delegates to using a {@link FlowRequestHandler} (allowing reuse of
 * common front flow controller logic in other environments). Consult the
 * JavaDoc of those classes for more information on how requests are processed.
 * <p>
 * <li>By default, to have this controller launch a new flow execution
 * (conversation), have the client send a
 * {@link FlowExecutorArgumentHandler#getFlowIdArgumentName()} request
 * parameter indicating the flow definition to launch.
 * <li>To have this controller participate in an existing flow execution
 * (conversation), have the client send a
 * {@link FlowExecutorArgumentHandler#getFlowExecutionKeyArgumentName()}
 * request parameter identifying the conversation to participate in.
 * <p>
 * On each request received by this action, a {@link StrutsExternalContext}
 * object is created as input to the web flow system. This external source event
 * provides access to the action form, action mapping, and other Struts-specific
 * constructs.
 * <p>
 * This class also is aware of the {@link SpringBindingActionForm} adapter,
 * which adapts Spring's data binding infrastructure (based on POJO binding, a
 * standard Errors interface, and property editor type conversion) to the Struts
 * action form model. This option gives backend web-tier developers full support
 * for POJO-based binding with minimal hassel, while still providing consistency
 * to view developers who already have a lot of experience with Struts for
 * markup and request dispatching.
 * <p>
 * Below is an example <code>struts-config.xml</code> configuration for a
 * FlowAction:
 * 
 * <pre>
 *     &lt;action path=&quot;/userRegistration&quot;
 *         type=&quot;org.springframework.webflow.executor.struts.FlowAction&quot;
 *         name=&quot;springBindingActionForm&quot; scope=&quot;request&quot;&gt;
 *     &lt;/action&gt;
 * </pre>
 * 
 * This example maps the logical request URL <code>/userRegistration.do</code>
 * as a Flow controller (<code>FlowAction</code>). It is expected that flows
 * to launch be provided in a dynamic fashion by the views (allowing this single
 * <code>FlowAction</code> to manage any number of flow executions). A Spring
 * binding action form instance is set in request scope, acting as an adapter
 * enabling POJO-based binding and validation with Spring.
 * <p>
 * Other notes regarding Struts/Spring Web Flow integration:
 * <ul>
 * <li>Logical view names returned when <code>ViewStates</code> and
 * <code>EndStates</code> are entered are mapped to physical view templates
 * using standard Struts action forwards (typically global forwards).</li>
 * <li>Use of the <code>SpringBindingActionForm</code> requires no special
 * setup in <code>struts-config.xml</code>: simply declare a form bean in
 * request scope of the class
 * <code>org.springframework.web.struts.SpringBindingActionForm</code> and use
 * it with your FlowAction.</li>
 * <li>This class depends on a {@link FlowExecutor} instance to be configured.
 * If relying on Spring's {@link DelegatingActionProxy} (which is recommended),
 * a FlowExecutor reference can simply be injected using standard Spring
 * dependency injection techniques. If you are not using the proxy-based
 * approach, this class will attempt a root context lookup on initialization,
 * first querying for a bean of instance {@link FlowExecutor} named
 * {@link #FLOW_EXECUTOR_BEAN_NAME}.</li>
 * <li>The
 * {@link org.springframework.webflow.executor.support.FlowExecutorArgumentHandler}
 * used by the FlowAction can be configured in the root context using a bean of
 * name {@link #FLOW_EXECUTOR_ARGUMENT_HANDLER_BEAN_NAME}. If not explicitly
 * specified it will default to a normal
 * {@link org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler}
 * with standard configuration.</li>
 * </ul>
 * <p>
 * The benefits here are considerable: developers now have a powerful web flow
 * capability integrated with Struts, with a consistent-approach to POJO-based
 * binding and validation that addresses the proliferation of
 * <code>ActionForm</code> classes found in traditional Struts-based apps.
 * 
 * @see org.springframework.webflow.executor.FlowExecutor
 * @see org.springframework.webflow.executor.support.FlowRequestHandler
 * @see org.springframework.web.struts.SpringBindingActionForm
 * @see org.springframework.web.struts.DelegatingActionProxy
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class FlowAction extends ActionSupport {

	/**
	 * The flow executor will be retreived from the application context using
	 * this bean name if no executor is explicitly set. ("flowExecutor")
	 */
	protected static final String FLOW_EXECUTOR_BEAN_NAME = "flowExecutor";

	/**
	 * The flow executor argument handler will be retreived from the
	 * application context using this bean name if no argument handler is
	 * explicitly set. ("argumentHandler")
	 */
	protected static final String FLOW_EXECUTOR_ARGUMENT_HANDLER_BEAN_NAME = "argumentHandler";

	/**
	 * The service responsible for launching and signaling Struts-originating
	 * events in flow executions.
	 */
	private FlowExecutor flowExecutor;

	/**
	 * Delegate to handle flow executor arguments.
	 */
	private FlowExecutorArgumentHandler argumentHandler;

	/**
	 * Returns the flow executor used by this controller.
	 * @return the flow executor
	 */
	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	/**
	 * Configures the flow executor implementation to use. Required.
	 * @param flowExecutor the fully configured flow executor
	 */
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	/**
	 * Returns the flow executor argument handler used by this controller.
	 * @return the argument handler
	 */
	public FlowExecutorArgumentHandler getArgumentHandler() {
		return argumentHandler;
	}

	/**
	 * Sets the flow executor argument handler to use.
	 * @param argumentHandler the fully configured argument handler
	 */
	public void setArgumentHandler(FlowExecutorArgumentHandler argumentHandler) {
		this.argumentHandler = argumentHandler;
	}

	protected void onInit() {
		WebApplicationContext context = getWebApplicationContext();
		if (getFlowExecutor() == null) {
			if (context.containsBean(FLOW_EXECUTOR_BEAN_NAME)) {
				setFlowExecutor((FlowExecutor)context.getBean(FLOW_EXECUTOR_BEAN_NAME, FlowExecutor.class));
			}
			else {
				throw new IllegalStateException("No '" + FLOW_EXECUTOR_BEAN_NAME
						+ "' bean definition could be found; to use Spring Web Flow with Struts you must "
						+ "configure this FlowAction with a FlowExecutor");
			}
		}
		if (getArgumentHandler() == null) {
			if (context.containsBean(FLOW_EXECUTOR_ARGUMENT_HANDLER_BEAN_NAME)) {
				setArgumentHandler((FlowExecutorArgumentHandler)context.getBean(
						FLOW_EXECUTOR_ARGUMENT_HANDLER_BEAN_NAME, FlowExecutorArgumentHandler.class));
			}
			else {
				// default
				argumentHandler = new RequestParameterFlowExecutorArgumentHandler();
			}
		}
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ExternalContext context = new StrutsExternalContext(mapping, form, getServletContext(), request, response);
		ResponseInstruction responseInstruction = createRequestHandler().handleFlowRequest(context);
		return toActionForward(responseInstruction, mapping, form, request, response, context);
	}

	/**
	 * Factory method that creates a new helper for processing a request into
	 * this flow controller.
	 * @return the controller helper
	 */
	protected FlowRequestHandler createRequestHandler() {
		return new FlowRequestHandler(getFlowExecutor(), getArgumentHandler());
	}

	/**
	 * Return a Struts ActionForward given a ResponseInstruction. Adds all
	 * attributes from the ResponseInstruction as request attributes.
	 */
	protected ActionForward toActionForward(ResponseInstruction response, ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpResponse, ExternalContext context) throws Exception {
		if (response.isApplicationView()) {
			// forward to a view as part of an active conversation
			ApplicationView forward = (ApplicationView)response.getViewSelection();
			Map model = new HashMap(forward.getModel());
			argumentHandler.exposeFlowExecutionContext(
					response.getFlowExecutionKey(), response.getFlowExecutionContext(), model);
			WebUtils.exposeRequestAttributes(request, model);
			if (form instanceof SpringBindingActionForm) {
				SpringBindingActionForm bindingForm = (SpringBindingActionForm)form;
				// expose the form object and associated errors as the
				// "current form object" in the request
				Errors currentErrors = (Errors)model.get(FormObjectAccessor.getCurrentFormErrorsName());
				bindingForm.expose(currentErrors, request);
			}
			return findForward(forward, mapping);

		}
		else if (response.isFlowExecutionRedirect()) {
			// redirect to active flow execution URL
			String flowExecutionUrl = argumentHandler.createFlowExecutionUrl(
					response.getFlowExecutionKey(), response.getFlowExecutionContext(), context);
			return createRedirectForward(flowExecutionUrl, httpResponse);
		}
		else if (response.isFlowDefinitionRedirect()) {
			// restart the flow by redirecting to flow launch URL
			String flowUrl = argumentHandler.createFlowDefinitionUrl(
					(FlowDefinitionRedirect)response.getViewSelection(), context);
			return createRedirectForward(flowUrl, httpResponse);
		}
		else if (response.isExternalRedirect()) {
			// redirect to external URL
			String externalUrl = argumentHandler.createExternalUrl(
					(ExternalRedirect)response.getViewSelection(), response.getFlowExecutionKey(), context);
			return createRedirectForward(externalUrl, httpResponse);
		}
		else if (response.isNull()) {
			// no response to issue
			return null;
		}
		else {
			throw new IllegalArgumentException("Don't know how to handle response instruction " + response);
		}
	}

	/**
	 * Handles a redirect. This implementation simply calls sendRedirect on the
	 * response object.
	 * @param url the url to redirect to
	 * @param response the http response
	 * @return the redirect forward, this implementation returns null
	 * @throws Exception an excpetion occured processing the redirect
	 * @see HttpServletResponse#sendRedirect(java.lang.String)
	 */
	protected ActionForward createRedirectForward(String url, HttpServletResponse response) throws Exception {
		response.sendRedirect(url);
		return null;
	}

	/**
	 * Find an action forward for given application view. If no suitable forward
	 * is found in the action mapping using the view name as a key, this method
	 * will create a new action forward using the view name.
	 * @param forward the application view to find a forward for
	 * @param mapping the action mapping to use
	 * @return the action forward, never null
	 */
	protected ActionForward findForward(ApplicationView forward, ActionMapping mapping) {
		// note that this method is always creating a new ActionForward to make
		// sure that the redirect flag is false -- redirect is controlled by SWF
		// itself, not Struts
		ActionForward actionForward = mapping.findForward(forward.getViewName());
		if (actionForward != null) {
			// the 1.2.1 copy constructor would ideally be better to
			// use, but it is not Struts 1.1 compatible
			actionForward = new ActionForward(actionForward.getName(), actionForward.getPath(), false);
		}
		else {
			actionForward = new ActionForward(forward.getViewName(), false);
		}
		return actionForward;
	}
}