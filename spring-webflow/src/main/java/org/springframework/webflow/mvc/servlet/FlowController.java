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
package org.springframework.webflow.mvc.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.js.mvc.servlet.AjaxHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * The adapter between the Spring MVC Controller layer and the Spring Web Flow engine. This controller allows Spring Web
 * Flow to run embedded as a Controller within a DispatcherServlet, the key piece of the Spring Web MVC platform. It is
 * expected a DispatcherServlet HandlerMapping will care for mapping all requests for flows to this controller for
 * handling.
 * 
 * @author Keith Donald
 */
public class FlowController extends AbstractController {

	private FlowHandlerAdapter flowHandlerAdapter;

	private Map flowHandlers = new HashMap();

	/**
	 * Creates a new flow controller.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setFlowUrlHandler(FlowUrlHandler)
	 * @see #setAjaxHandler(AjaxHandler)
	 * @see #afterPropertiesSet()
	 */
	public FlowController() {
		// turn caching off for flows by default
		setCacheSeconds(0);
	}

	/**
	 * Returns the central service for executing flows. Required.
	 */
	public FlowExecutor getFlowExecutor() {
		return flowHandlerAdapter.getFlowExecutor();
	}

	/**
	 * Sets the central service for executing flows. Required.
	 * @param flowExecutor
	 */
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		flowHandlerAdapter.setFlowExecutor(flowExecutor);
	}

	/**
	 * Returns the configured flow url handler.
	 */
	public FlowUrlHandler getFlowUrlHandler() {
		return flowHandlerAdapter.getFlowUrlHandler();
	}

	/**
	 * Sets the configured flow url handler.
	 * @param urlHandler the flow url handler.
	 */
	public void setFlowUrlHandler(FlowUrlHandler urlHandler) {
		flowHandlerAdapter.setFlowUrlHandler(urlHandler);
	}

	/**
	 * Returns the configured Ajax handler.
	 */
	public AjaxHandler getAjaxHandler() {
		return flowHandlerAdapter.getAjaxHandler();
	}

	/**
	 * Sets the configured Ajax handler.
	 * @param ajaxHandler the ajax handler
	 */
	public void setAjaxHandler(AjaxHandler ajaxHandler) {
		flowHandlerAdapter.setAjaxHandler(ajaxHandler);
	}

	/**
	 * Sets the custom flow handles for managing the access to flows in a custom manner.
	 * @param flowHandlers the flow handler map
	 */
	public void setFlowHandlers(Map flowHandlers) {
		this.flowHandlers = flowHandlers;
	}

	/**
	 * Registers a flow handler this controller should delegate to to customize the control logic associated with
	 * managing the execution of a specific flow.
	 * @param flowHandler the handler
	 */
	public void registerFlowHandler(FlowHandler flowHandler) {
		flowHandlers.put(flowHandler.getFlowId(), flowHandler);
	}

	/**
	 * Returns the flow handler adapter which this Controller uses internally to carry out handler workflow.
	 */
	public FlowHandlerAdapter getFlowHandlerAdapter() {
		return flowHandlerAdapter;
	}

	/**
	 * Sets the flow handler adapter which this Controller uses internally to carry out handler workflow. Call this
	 * instead of the convenience accesors to completely customize flow controller workflow.
	 * @param flowHandlerAdapter the flow handler adapter
	 */
	public void setFlowHandlerAdapter(FlowHandlerAdapter flowHandlerAdapter) {
		this.flowHandlerAdapter = flowHandlerAdapter;
	}

	public void afterPropertiesSet() throws Exception {
		if (flowHandlerAdapter == null) {
			flowHandlerAdapter = new FlowHandlerAdapter();
			flowHandlerAdapter.setApplicationContext(getApplicationContext());
			flowHandlerAdapter.afterPropertiesSet();
		}
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FlowHandler handler = getFlowHandler(request);
		return flowHandlerAdapter.handle(request, response, handler);
	}

	// subclassing hooks

	// internal helpers

	private FlowHandler getFlowHandler(HttpServletRequest request) {
		FlowUrlHandler urlHandler = flowHandlerAdapter.getFlowUrlHandler();
		String flowId = urlHandler.getFlowId(request);
		return getFlowHandler(flowId);
	}

	private FlowHandler getFlowHandler(String flowId) {
		FlowHandler handler = (FlowHandler) flowHandlers.get(flowId);
		if (handler == null) {
			handler = new DefaultFlowHandler(flowId);
		}
		return handler;
	}

	private static class DefaultFlowHandler extends AbstractFlowHandler {
		private String flowId;

		public DefaultFlowHandler(String flowId) {
			this.flowId = flowId;
		}

		public String getFlowId() {
			return flowId;
		}
	}

}