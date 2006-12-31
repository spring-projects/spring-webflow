/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.executor.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.mvc.Controller;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;

/**
 * Point of integration between Spring Portlet MVC and Spring Web Flow: a
 * {@link Controller} that routes incoming portlet requests to one or more
 * managed flow executions.
 * <p>
 * Requests into the web flow system are handled by a {@link FlowExecutor},
 * which this class delegates to. Consult the JavaDoc of that class for more
 * information on how requests are processed.
 * <p>
 * Note: a single <code>PortletFlowController</code> may execute all flows
 * within your application. See the <code>phonebook-portlet</code> sample
 * application for examples of the various strategies for launching and resuming
 * flow executions in a Portlet environment.
 * <p>
 * It is also possible to customize the {@link FlowExecutorArgumentHandler}
 * strategy to allow for different types of controller parameterization, for
 * example perhaps in conjunction with a REST-style request mapper.
 * 
 * @see org.springframework.webflow.executor.FlowExecutor
 * @see org.springframework.webflow.executor.support.FlowExecutorArgumentHandler
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author J.Enrique Ruiz
 * @author César Ordiñana
 */
public class PortletFlowController extends AbstractController implements InitializingBean {

	/**
	 * Name of the attribute under which the response instruction will be stored
	 * in the session.
	 */
	private static final String RESPONSE_INSTRUCTION_SESSION_ATTRIBUTE = "actionRequest.responseInstruction";

	/**
	 * Delegate for executing flow executions (launching new executions, and
	 * resuming existing executions).
	 */
	private FlowExecutor flowExecutor;

	/**
	 * Delegate for handler flow executor arguments.
	 */
	private FlowExecutorArgumentHandler argumentHandler = new RequestParameterFlowExecutorArgumentHandler();

	/**
	 * Create a new portlet flow controller. Allows for bean style usage.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setArgumentHandler(FlowExecutorArgumentHandler)
	 */
	public PortletFlowController() {
		// set the cache seconds property to 0 so no pages are cached by default
		// for flows
		setCacheSeconds(0);
		// this controller stores ResponseInstruction objects in the session, so
		// we need to ensure we do this in an orderly manner
		// see exposeToRenderPhase() and extractActionResponseInstruction()
		setSynchronizeOnSession(true);
	}

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

	/**
	 * Sets the identifier of the default flow to launch if no flowId argument
	 * can be extracted by the configured {@link FlowExecutorArgumentHandler}
	 * during render request processing.
	 * <p>
	 * This is a convenience method that sets the default flow id of the
	 * controller's argument handler. Don't use this when using
	 * {@link #setArgumentHandler(FlowExecutorArgumentHandler)}.
	 */
	public void setDefaultFlowId(String defaultFlowId) {
		argumentHandler.setDefaultFlowId(defaultFlowId);
	}

	public void afterPropertiesSet() {
		Assert.notNull(flowExecutor, "The flow executor property is required");
		Assert.notNull(argumentHandler, "The argument handler property is required");
	}

	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		PortletExternalContext context = new PortletExternalContext(getPortletContext(), request, response);
		if (argumentHandler.isFlowExecutionKeyPresent(context)) {
			// flowExecutionKey render param present: this is a request to
			// render an active flow execution -- extract its key
			String flowExecutionKey = argumentHandler.extractFlowExecutionKey(context);
			// look for a cached response instruction in the session put there
			// by the action request phase as part of an "active view" forward
			ResponseInstruction responseInstruction = extractActionResponseInstruction(request);
			if (responseInstruction == null) {
				// no response instruction found, simply refresh the current
				// view state of the flow execution
				return toModelAndView(flowExecutor.refresh(flowExecutionKey, context));
			}
			else {
				// found: convert it to model and view for rendering
				return toModelAndView(responseInstruction);
			}
		}
		else {
			// this is either a "launch" flow request or a "confirmation view"
			// render request -- look for the cached "confirmation view"
			// response instruction
			ResponseInstruction responseInstruction = extractActionResponseInstruction(request);
			if (responseInstruction == null) {
				// no response instruction found in session - launch a new flow
				// execution
				String flowId = argumentHandler.extractFlowId(context);
				return toModelAndView(flowExecutor.launch(flowId, context));
			}
			else {
				// found: convert it to model and view for rendering
				return toModelAndView(responseInstruction);
			}
		}
	}

	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		PortletExternalContext context = new PortletExternalContext(getPortletContext(), request, response);
		String flowExecutionKey = argumentHandler.extractFlowExecutionKey(context);
		String eventId = argumentHandler.extractEventId(context);
		// signal the event against the flow execution, returning the next
		// response instruction
		ResponseInstruction responseInstruction = flowExecutor.resume(flowExecutionKey, eventId, context);
		if (responseInstruction.isApplicationView()) {
			// response instruction is a forward to an "application view"
			if (responseInstruction.isActiveView()) {
				// is an "active" forward from a view-state (not end-state) --
				// set the flow execution key render parameter to support
				// browser refresh
				response.setRenderParameter(
						argumentHandler.getFlowExecutionKeyArgumentName(),
						responseInstruction.getFlowExecutionKey());
			}
			// cache response instruction for access during render phase of this
			// portlet
			exposeToRenderPhase(responseInstruction, request);
		}
		else if (responseInstruction.isFlowExecutionRedirect()) {
			// is a flow execution redirect: simply expose key parameter to
			// support refresh during render phase
			response.setRenderParameter(
					argumentHandler.getFlowExecutionKeyArgumentName(),
					responseInstruction.getFlowExecutionKey());
		}
		else if (responseInstruction.isFlowDefinitionRedirect()) {
			// set flow id render parameter to request that a new flow be
			// launched within this portlet
			FlowDefinitionRedirect redirect = (FlowDefinitionRedirect)responseInstruction.getViewSelection();
			response.setRenderParameters(redirect.getExecutionInput());
			response.setRenderParameter(argumentHandler.getFlowIdArgumentName(), redirect.getFlowDefinitionId());
		}
		else if (responseInstruction.isExternalRedirect()) {
			// issue the redirect to the external URL
			ExternalRedirect redirect = (ExternalRedirect)responseInstruction.getViewSelection();
			String url = argumentHandler.createExternalUrl(redirect, flowExecutionKey, context);
			response.sendRedirect(url);
		}
		else {
			throw new IllegalArgumentException("Don't know how to handle response instruction " + responseInstruction);
		}
	}

	// helpers

	/**
	 * Expose given response instruction to the render phase by putting it in
	 * the session.
	 */
	private void exposeToRenderPhase(ResponseInstruction responseInstruction, ActionRequest request) {
		PortletSession session = request.getPortletSession(false);
		Assert.notNull(session, "A PortletSession is required");
		session.setAttribute(RESPONSE_INSTRUCTION_SESSION_ATTRIBUTE, responseInstruction);
	}

	/**
	 * Extract a response instruction stored in the session during the action
	 * phase by {@link #exposeToRenderPhase(ResponseInstruction, ActionRequest)}.
	 * If a response instruction is found, it will be removed from the session.
	 * @param request the portlet request
	 * @return the response instructions found in the session or null if not
	 * found
	 */
	private ResponseInstruction extractActionResponseInstruction(PortletRequest request) {
		PortletSession session = request.getPortletSession(false);
		ResponseInstruction response = null;
		if (session != null) {
			response = (ResponseInstruction)session.getAttribute(RESPONSE_INSTRUCTION_SESSION_ATTRIBUTE);
			if (response != null) {
				// remove it
				session.removeAttribute(RESPONSE_INSTRUCTION_SESSION_ATTRIBUTE);
			}
		}
		return response;
	}

	/**
	 * Convert given response instruction into a Spring Portlet MVC model and
	 * view.
	 */
	protected ModelAndView toModelAndView(ResponseInstruction response) {
		if (response.isApplicationView()) {
			// forward to a view as part of an active conversation
			ApplicationView forward = (ApplicationView)response.getViewSelection();
			Map model = new HashMap(forward.getModel());
			argumentHandler.exposeFlowExecutionContext(
					response.getFlowExecutionKey(), response.getFlowExecutionContext(), model);
			return new ModelAndView(forward.getViewName(), model);
		}
		else if (response.isNull()) {
			// no response to issue
			return null;
		}
		else {
			throw new IllegalArgumentException("Don't know how to handle response instruction " + response);
		}
	}
}