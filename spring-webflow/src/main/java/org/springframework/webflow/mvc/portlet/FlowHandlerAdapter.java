/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.mvc.portlet;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.portlet.HandlerAdapter;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.handler.PortletContentGenerator;
import org.springframework.webflow.context.portlet.DefaultFlowUrlHandler;
import org.springframework.webflow.context.portlet.FlowUrlHandler;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * A custom MVC HandlerAdapter that encapsulates the generic workflow associated with executing flows in a Portlet
 * environment. Delegates to mapped {@link FlowHandler flow handlers} to manage the interaction with executions of
 * specific flow definitions.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 * @author Rossen Stoyanchev
 */
public class FlowHandlerAdapter extends PortletContentGenerator implements HandlerAdapter, InitializingBean {

	private static final String ACTION_REQUEST_FLOW_EXCEPTION_ATTRIBUTE = "actionRequestFlowException";

	private FlowExecutor flowExecutor;

	private FlowUrlHandler flowUrlHandler;

	/**
	 * Creates a new flow handler adapter.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setFlowUrlHandler(FlowUrlHandler)
	 * @see #afterPropertiesSet()
	 */
	public FlowHandlerAdapter() {
		// prevent caching of flow pages by default
		setCacheSeconds(0);
	}

	/**
	 * Returns the central service for executing flows. Required.
	 */
	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	/**
	 * Sets the central service for executing flows. Required.
	 * @param flowExecutor
	 */
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	/**
	 * Returns the flow url handler.
	 */
	public FlowUrlHandler getFlowUrlHandler() {
		return flowUrlHandler;
	}

	/**
	 * Sets the flow url handler
	 * @param urlHandler the flow url handler
	 */
	public void setFlowUrlHandler(FlowUrlHandler urlHandler) {
		this.flowUrlHandler = urlHandler;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowExecutor, "The FlowExecutor to execute flows is required");
		if (flowUrlHandler == null) {
			flowUrlHandler = new DefaultFlowUrlHandler();
		}
	}

	public boolean supports(Object handler) {
		return handler instanceof FlowHandler;
	}

	public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		checkAndPrepare(request, response);
		populateConveniencePortletProperties(request);
		FlowException e = clearActionRequestFlowException(request, response, flowHandler);
		if (e != null) {
			return handleException(e, flowHandler, request, response);
		}
		String flowExecutionKey = flowUrlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			return resumeFlowRender(request, response, flowHandler, flowExecutionKey);
		} else {
			MutableAttributeMap<Object> input = flowHandler.createExecutionInputMap(request);
			if (input == null) {
				input = defaultCreateFlowExecutionInputMap(request);
			}
			return startFlowRender(flowHandler, input, request, response);
		}
	}

	public ModelAndView handleResource(ResourceRequest request, ResourceResponse response, Object handler)
			throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		checkAndPrepare(request, response);
		populateConveniencePortletProperties(request);
		String flowExecutionKey = flowUrlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			return resumeFlowResource(request, response, flowHandler, flowExecutionKey);
		} else {
			MutableAttributeMap<Object> input = flowHandler.createResourceExecutionInputMap(request);
			if (input == null) {
				input = defaultCreateFlowExecutionInputMap(request);
			}
			return startFlowResource(flowHandler, request, response);
		}
	}

	public void handleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		populateConveniencePortletProperties(request);
		String flowExecutionKey = flowUrlHandler.getFlowExecutionKey(request);
		PortletExternalContext context = createPortletExternalContext(request, response);
		try {
			FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
			if (result.isPaused()) {
				flowUrlHandler.setFlowExecutionRenderParameter(result.getPausedKey(), response);
			} else if (result.isEnded()) {
				handleFlowExecutionOutcome(result.getOutcome(), flowHandler, request, response);
			} else {
				throw new IllegalStateException("Execution result should have been one of [paused] or [ended]");
			}
		} catch (FlowException e) {
			request.getPortletSession().setAttribute(ACTION_REQUEST_FLOW_EXCEPTION_ATTRIBUTE, e);
		}
	}

	public void handleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {
		// keep render params
		response.setRenderParameters(request);
	}

	// subclassing hooks

	protected void populateConveniencePortletProperties(PortletRequest request) {
		request.setAttribute("portletMode", request.getPortletMode().toString());
		request.setAttribute("portletWindowState", request.getWindowState().toString());
	}

	protected PortletExternalContext createPortletExternalContext(PortletRequest request, PortletResponse response) {
		return new PortletExternalContext(getPortletContext(), request, response);
	}

	protected MutableAttributeMap<Object> defaultCreateFlowExecutionInputMap(PortletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap.size() == 0) {
			return null;
		}
		LocalAttributeMap<Object> inputMap = new LocalAttributeMap<Object>();
		Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String[]> entry = it.next();
			String name = entry.getKey();
			String[] values = entry.getValue();
			if (values.length == 1) {
				inputMap.put(name, values[0]);
			} else {
				inputMap.put(name, values);
			}
		}
		return inputMap;
	}

	protected void defaultHandleExecutionOutcome(FlowExecutionOutcome outcome, FlowHandler flowHandler,
			ActionRequest request, ActionResponse response) throws PortletModeException {
	}

	protected ModelAndView defaultHandleException(FlowHandler flowHandler, FlowException e, RenderRequest request,
			RenderResponse response) {
		if (e instanceof NoSuchFlowExecutionException) {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting a new execution of previously ended flow '" + flowHandler.getFlowId() + "'");
			}
			// by default, attempt to restart the flow
			try {
				startFlowRender(flowHandler, null, request, response);
				return null;
			} catch (FlowException flowException) {
				return handleException(flowException, flowHandler, request, response);
			}
		} else {
			throw e;
		}
	}

	protected ModelAndView defaultHandleResourceException(FlowHandler flowHandler, FlowException e,
			ResourceRequest request, ResourceResponse response) {
		if (e instanceof NoSuchFlowExecutionException) {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting a new execution of previously ended flow '" + flowHandler.getFlowId() + "'");
			}
			// by default, attempt to restart the flow
			return startFlowResource(flowHandler, request, response);
		} else {
			throw e;
		}
	}

	// helpers

	private ModelAndView handleException(FlowException e, FlowHandler flowHandler, RenderRequest request,
			RenderResponse response) {
		String view = flowHandler.handleException(e, request, response);
		if (view != null) {
			return new ModelAndView(view);
		} else {
			return defaultHandleException(flowHandler, e, request, response);
		}
	}

	private ModelAndView handleResourceException(FlowException e, FlowHandler flowHandler, ResourceRequest request,
			ResourceResponse response) {
		String view = flowHandler.handleResourceException(e, request, response);
		if (view != null) {
			return new ModelAndView(view);
		} else {
			return defaultHandleResourceException(flowHandler, e, request, response);
		}
	}

	private void handleFlowExecutionOutcome(FlowExecutionOutcome outcome, FlowHandler flowHandler,
			ActionRequest request, ActionResponse response) throws PortletModeException {
		boolean handled = flowHandler.handleExecutionOutcome(outcome, request, response);
		if (!handled) {
			defaultHandleExecutionOutcome(outcome, flowHandler, request, response);
		}
	}

	private ModelAndView startFlowRender(FlowHandler flowHandler, MutableAttributeMap<Object> input,
			RenderRequest request, RenderResponse response) {
		PortletExternalContext context = createPortletExternalContext(request, response);
		try {
			FlowExecutionResult result = flowExecutor.launchExecution(flowHandler.getFlowId(), input, context);
			if (result.isPaused()) {
				flowUrlHandler.setFlowExecutionInSession(result.getPausedKey(), request);
			}
			return null;
		} catch (FlowException flowEx) {
			return handleException(flowEx, flowHandler, request, response);
		}
	}

	private ModelAndView startFlowResource(FlowHandler flowHandler, ResourceRequest request, ResourceResponse response) {
		PortletExternalContext context = createPortletExternalContext(request, response);
		try {
			FlowExecutionResult result = flowExecutor.launchExecution(flowHandler.getFlowId(), null, context);
			if (result.isPaused()) {
				flowUrlHandler.setFlowExecutionInSession(result.getPausedKey(), request);
			}
			return null;
		} catch (FlowException flowEx) {
			return handleResourceException(flowEx, flowHandler, request, response);
		}
	}

	private ModelAndView resumeFlowRender(RenderRequest request, RenderResponse response, FlowHandler flowHandler,
			String flowExecutionKey) {
		PortletExternalContext context = createPortletExternalContext(request, response);
		try {
			flowExecutor.resumeExecution(flowExecutionKey, context);
			return null;
		} catch (FlowException e) {
			return handleException(e, flowHandler, request, response);
		}
	}

	private ModelAndView resumeFlowResource(ResourceRequest request, ResourceResponse response,
			FlowHandler flowHandler, String flowExecutionKey) {
		PortletExternalContext context = createPortletExternalContext(request, response);
		try {
			flowExecutor.resumeExecution(flowExecutionKey, context);
			return null;
		} catch (FlowException e) {
			return handleResourceException(e, flowHandler, request, response);
		}
	}

	private FlowException clearActionRequestFlowException(RenderRequest request, RenderResponse response,
			FlowHandler flowHandler) {
		PortletSession session = request.getPortletSession(false);
		if (session != null) {
			FlowException e = (FlowException) session.getAttribute(ACTION_REQUEST_FLOW_EXCEPTION_ATTRIBUTE);
			if (e != null) {
				session.removeAttribute(ACTION_REQUEST_FLOW_EXCEPTION_ATTRIBUTE);
				return e;
			}
		}
		return null;
	}

}