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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
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
public class FlowController implements Controller, ApplicationContextAware, InitializingBean {

	private FlowHandlerAdapter flowHandlerAdapter = new FlowHandlerAdapter();

	private Map flowHandlers = new HashMap();

	private boolean customFlowHandlerAdapterSet;

	/**
	 * Creates a new flow controller.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setFlowUrlHandler(FlowUrlHandler)
	 * @see #setAjaxHandler(AjaxHandler)
	 * @see #setFlowHandlerAdapter(FlowHandlerAdapter)
	 * @see #afterPropertiesSet()
	 */
	public FlowController() {

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
	 * Set whether redirects sent by this controller should be compatible with HTTP 1.0 clients.
	 * <p>
	 * By default, this will enforce a redirect HTTP status code of 302 by delegating to
	 * <code>HttpServletResponse.sendRedirect</code>. Setting this to false will send HTTP status code 303, which is
	 * the correct code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>
	 * Many HTTP 1.1 clients treat 302 just like 303, not making any difference. However, some clients depend on 303
	 * when redirecting after a POST request; turn this flag off in such a scenario.
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect
	 */
	public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
		flowHandlerAdapter.setRedirectHttp10Compatible(redirectHttp10Compatible);
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
		customFlowHandlerAdapterSet = true;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (!customFlowHandlerAdapterSet) {
			flowHandlerAdapter.setApplicationContext(applicationContext);
		}
	}

	public void afterPropertiesSet() throws Exception {
		if (!customFlowHandlerAdapterSet) {
			flowHandlerAdapter.afterPropertiesSet();
		}
	}

	// subclassing hooks

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FlowHandler handler = getFlowHandler(request);
		return flowHandlerAdapter.handle(request, response, handler);
	}

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