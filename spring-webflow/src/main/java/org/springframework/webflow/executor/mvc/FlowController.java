/*
 * Copyright 2002-2006 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.FlowRequestHandler;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.RequestPathFlowExecutorArgumentHandler;

/**
 * Point of integration between Spring Web MVC and Spring Web Flow: a
 * {@link Controller} that routes incoming requests to one or more managed flow
 * executions.
 * <p>
 * Requests into the web flow system are handled by a {@link FlowExecutor},
 * which this class delegates to using a {@link FlowRequestHandler} helper.
 * Consult the JavaDoc of that class for more information on how requests are
 * processed.
 * <p>
 * Note: a single <code>FlowController</code> may execute all flows of your application.
 * <ul>
 * <li>By default, to have this controller launch a new flow execution
 * (conversation), have the client send a
 * {@link FlowExecutorArgumentHandler#getFlowIdArgumentName()} request
 * parameter indicating the flow definition to launch.
 * <li>To have this controller participate in an existing flow execution
 * (conversation), have the client send a
 * {@link FlowExecutorArgumentHandler#getFlowExecutionKeyArgumentName()}
 * request parameter identifying the conversation to participate in.
 * See the <code>flow-launcher</code> sample application for examples of the
 * various strategies for launching and resuming flow executions.
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 *     &lt;!--
 *         Exposes flows for execution at a single request URL.
 *         The id of a flow to launch should be passed in by clients using
 *         the &quot;_flowId&quot; request parameter:
 *         e.g. /app.htm?_flowId=flow1
 *     --&gt;
 *     &lt;bean name=&quot;/app.htm&quot; class=&quot;org.springframework.webflow.executor.mvc.FlowController&quot;&gt;
 *         &lt;property name=&quot;flowExecutor&quot; ref=&quot;flowExecutor&quot;/&gt;
 *     &lt;/bean&gt;
 * </pre>
 * <p>
 * It is also possible to customize the {@link FlowExecutorArgumentHandler}
 * strategy to allow for different types of controller parameterization, for
 * example perhaps in conjunction with a REST-style request mapper (see
 * {@link RequestPathFlowExecutorArgumentHandler}).
 * 
 * @see org.springframework.webflow.executor.FlowExecutor
 * @see org.springframework.webflow.executor.support.FlowRequestHandler
 * @see org.springframework.webflow.executor.support.FlowExecutorArgumentHandler
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class FlowController extends AbstractController implements InitializingBean {

	/**
	 * The facade for executing flows (launching new executions, and resuming
	 * existing executions).
	 */
	private FlowExecutor flowExecutor;

	/**
	 * The strategy for handling flow executor parameters.
	 */
	private FlowExecutorArgumentHandler argumentHandler = new RequestParameterFlowExecutorArgumentHandler();

	/**
	 * Create a new flow controller. Allows bean style usage.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setArgumentHandler(FlowExecutorArgumentHandler)
	 */
	public FlowController() {
		// set the cache seconds property to 0 so no pages are cached by default
		// for flows.
		setCacheSeconds(0);
	}

	/**
	 * Returns the flow executor used by this controller.
	 * @return the flow executor
	 */
	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	/**
	 * Sets the flow executor to use; setting this property is required.
	 * @param flowExecutor the fully configured flow executor to use
	 */
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	/**
	 * Returns the flow executor argument handler used by this controller.
	 * Defaults to {@link RequestParameterFlowExecutorArgumentHandler}.
	 * @return the argument handler
	 */
	public FlowExecutorArgumentHandler getArgumentHandler() {
		return argumentHandler;
	}

	/**
	 * Sets the flow executor argument handler to use. The default is
	 * {@link RequestParameterFlowExecutorArgumentHandler}.
	 * @param argumentHandler the fully configured argument handler
	 */
	public void setArgumentHandler(FlowExecutorArgumentHandler argumentHandler) {
		this.argumentHandler = argumentHandler;
	}

	/**
	 * Sets the identifier of the default flow to launch if no flowId argument
	 * can be extracted by the configured {@link FlowExecutorArgumentHandler}
	 * during request processing.
	 * <p>
	 * This is a convenience method that sets the default flow id of the
	 * controller's argument handler. Don't use this when using
	 * {@link #setArgumentHandler(FlowExecutorArgumentHandler)}.
	 */
	public void setDefaultFlowId(String defaultFlowId) {
		this.argumentHandler.setDefaultFlowId(defaultFlowId);
	}

	public void afterPropertiesSet() {
		Assert.notNull(flowExecutor, "The flow executor property is required");
		Assert.notNull(argumentHandler, "The argument handler property is required");
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ServletExternalContext context = new ServletExternalContext(getServletContext(), request, response);
		ResponseInstruction responseInstruction = createRequestHandler().handleFlowRequest(context);
		return toModelAndView(responseInstruction, context);
	}

	/**
	 * Factory method that creates a new helper for processing a request into
	 * this flow controller. The handler is a basic template encapsulating
	 * reusable flow execution request handling workflow.
	 * This implementation just creates a new {@link FlowRequestHandler}.
	 * @return the controller helper
	 */
	protected FlowRequestHandler createRequestHandler() {
		return new FlowRequestHandler(getFlowExecutor(), getArgumentHandler());
	}

	/**
	 * Create a ModelAndView object based on the information in the selected
	 * response instruction. Subclasses can override this to return a
	 * specialized ModelAndView or to do custom processing on it.
	 * @param response instruction the response instruction to convert
	 * @return a new ModelAndView object
	 */
	protected ModelAndView toModelAndView(ResponseInstruction response, ExternalContext context) {
		if (response.isApplicationView()) {
			// forward to a view as part of an active conversation
			ApplicationView view = (ApplicationView)response.getViewSelection();
			Map model = new HashMap(view.getModel());
			argumentHandler.exposeFlowExecutionContext(
					response.getFlowExecutionKey(), response.getFlowExecutionContext(), model);
			return new ModelAndView(view.getViewName(), model);
		}
		else if (response.isFlowDefinitionRedirect()) {
			// restart the flow by redirecting to flow launch URL
			String flowUrl = argumentHandler.createFlowDefinitionUrl((FlowDefinitionRedirect)response.getViewSelection(), context);
			return new ModelAndView(new RedirectView(flowUrl));
		}
		else if (response.isFlowExecutionRedirect()) {
			// redirect to active flow execution URL
			String flowExecutionUrl = argumentHandler.createFlowExecutionUrl(
					response.getFlowExecutionKey(), response.getFlowExecutionContext(), context);
			return new ModelAndView(new RedirectView(flowExecutionUrl));
		}
		else if (response.isExternalRedirect()) {
			// redirect to external URL
			ExternalRedirect redirect = (ExternalRedirect)response.getViewSelection();
			String externalUrl = argumentHandler.createExternalUrl(redirect, response.getFlowExecutionKey(), context);
			return new ModelAndView(new RedirectView(externalUrl));
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