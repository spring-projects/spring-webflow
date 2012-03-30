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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.webflow.context.servlet.DefaultFlowUrlHandler;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * A custom MVC HandlerAdapter that encapsulates the generic workflow associated with executing flows in a Servlet
 * environment. Delegates to mapped {@link FlowHandler flow handlers} to manage the interaction with executions of
 * specific flow definitions.
 * 
 * @author Keith Donald
 */
public class FlowHandlerAdapter extends WebContentGenerator implements HandlerAdapter, InitializingBean {

	private static final Log logger = LogFactory.getLog(FlowHandlerAdapter.class);

	private static final String REFERER_FLOW_EXECUTION_ATTRIBUTE = "refererExecution";

	private static final String SERVLET_RELATIVE_LOCATION_PREFIX = "servletRelative:";

	private static final String CONTEXT_RELATIVE_LOCATION_PREFIX = "contextRelative:";

	private static final String SERVER_RELATIVE_LOCATION_PREFIX = "serverRelative:";

	/**
	 * The entry point into Spring Web Flow.
	 */
	private FlowExecutor flowExecutor;

	/**
	 * A strategy for extracting flow arguments and generating flow urls.
	 */
	private FlowUrlHandler flowUrlHandler;

	/**
	 * The representation of an Ajax client service capable of interacting with web flow.
	 */
	private AjaxHandler ajaxHandler;

	private boolean redirectHttp10Compatible = true;

	/**
	 * Creates a new flow handler adapter.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setFlowUrlHandler(FlowUrlHandler)
	 * @see #setAjaxHandler(AjaxHandler)
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
	 * @param flowUrlHandler the flow url handler
	 */
	public void setFlowUrlHandler(FlowUrlHandler flowUrlHandler) {
		this.flowUrlHandler = flowUrlHandler;
	}

	/**
	 * Returns the configured Ajax handler.
	 */
	public AjaxHandler getAjaxHandler() {
		return ajaxHandler;
	}

	/**
	 * Sets the configured Ajax handler.
	 * @param ajaxHandler the ajax handler
	 */
	public void setAjaxHandler(AjaxHandler ajaxHandler) {
		this.ajaxHandler = ajaxHandler;
	}

	/**
	 * Whether redirect sent by this handler adapter should be compatible with HTTP 1.0 clients.
	 * @return true if so, false otherwise
	 */
	public boolean getRedirectHttp10Compatible() {
		return redirectHttp10Compatible;
	}

	/**
	 * Set whether redirects sent by this handler adapter should be compatible with HTTP 1.0 clients.
	 * <p>
	 * By default, this will enforce a redirect HTTP status code of 302 by delegating to
	 * <code>HttpServletResponse.sendRedirect</code>. Setting this to false will send HTTP status code 303, which is the
	 * correct code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>
	 * Many HTTP 1.1 clients treat 302 just like 303, not making any difference. However, some clients depend on 303
	 * when redirecting after a POST request; turn this flag off in such a scenario.
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect
	 */
	public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
		this.redirectHttp10Compatible = redirectHttp10Compatible;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowExecutor, "The FlowExecutor to execute flows is required");
		if (flowUrlHandler == null) {
			flowUrlHandler = new DefaultFlowUrlHandler();
		}
		if (ajaxHandler == null) {
			ajaxHandler = new SpringJavascriptAjaxHandler();
		}
	}

	public boolean supports(Object handler) {
		return handler instanceof FlowHandler;
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		FlowHandler flowHandler = (FlowHandler) handler;
		checkAndPrepare(request, response, false);
		String flowExecutionKey = flowUrlHandler.getFlowExecutionKey(request);
		if (flowExecutionKey != null) {
			try {
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
				handleFlowExecutionResult(result, context, request, response, flowHandler);
			} catch (FlowException e) {
				handleFlowException(e, request, response, flowHandler);
			}
		} else {
			try {
				String flowId = getFlowId(flowHandler, request);
				MutableAttributeMap<Object> input = getInputMap(flowHandler, request);
				ServletExternalContext context = createServletExternalContext(request, response);
				FlowExecutionResult result = flowExecutor.launchExecution(flowId, input, context);
				handleFlowExecutionResult(result, context, request, response, flowHandler);
			} catch (FlowException e) {
				handleFlowException(e, request, response, flowHandler);
			}
		}
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}

	// subclassing hooks

	/**
	 * Creates the servlet external context for the current HTTP servlet request.
	 * @param request the current request
	 * @param response the current response
	 */
	protected ServletExternalContext createServletExternalContext(HttpServletRequest request,
			HttpServletResponse response) {
		ServletExternalContext context = new MvcExternalContext(getServletContext(), request, response, flowUrlHandler);
		context.setAjaxRequest(ajaxHandler.isAjaxRequest(request, response));
		return context;
	}

	/**
	 * The default algorithm to determine the id of the flow to launch from the current request. Only called if
	 * {@link FlowHandler#getFlowId()} returns null. This implementation delegates to the configured
	 * {@link FlowUrlHandler#getFlowId(HttpServletRequest)}. Subclasses may override.
	 * @param request the current request
	 */
	protected String defaultGetFlowId(HttpServletRequest request) {
		return flowUrlHandler.getFlowId(request);
	}

	/**
	 * The default algorithm to create the flow execution input map. Only called if
	 * {@link FlowHandler#createExecutionInputMap(HttpServletRequest)} returns null. This implementation exposes all
	 * current request parameters as flow execution input attributes. Subclasses may override.
	 * @param request the current request
	 */
	protected MutableAttributeMap<Object> defaultCreateFlowExecutionInputMap(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap.size() == 0) {
			return null;
		}
		LocalAttributeMap<Object> inputMap = new LocalAttributeMap<Object>(parameterMap.size(), 1);
		Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String[]> entry = it.next();
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

	/**
	 * The default algorithm for handling a flow execution outcome. Only called if
	 * {@link FlowHandler#handleExecutionOutcome(FlowExecutionOutcome, HttpServletRequest, HttpServletResponse)} returns
	 * null. This implementation attempts to start a new execution of the ended flow. Any flow execution output is
	 * passed as input to the new execution. Subclasses may override.
	 * @param flowId the id of the ended flow
	 * @param outcome the flow execution outcome
	 * @param context ServletExternalContext the completed ServletExternalContext
	 * @param request the current request
	 * @param response the current response
	 */
	protected void defaultHandleExecutionOutcome(String flowId, FlowExecutionOutcome outcome,
			ServletExternalContext context, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!context.isResponseComplete()) {
			// by default, just start the flow over passing the output as input
			if (logger.isDebugEnabled()) {
				logger.debug("Ended flow '" + flowId + "' did not commit a response; "
						+ "attempting to start a new flow execution as a default outcome handler");
			}
			String flowUrl = flowUrlHandler.createFlowDefinitionUrl(flowId, outcome.getOutput(), request);
			sendRedirect(flowUrl, request, response);
		}
	}

	/**
	 * The default algorithm for handling a {@link FlowException} now handled by the Web Flow system. Only called if
	 * {@link FlowHandler#handleException(FlowException, HttpServletRequest, HttpServletResponse)} returns null. This
	 * implementation rethrows the exception unless it is a {@link NoSuchFlowExecutionException}. If the exception is a
	 * NoSuchFlowExecutionException, this implementation attempts to start a new execution of the ended or expired flow.
	 * Subclasses may override.
	 * @param flowId the id of the ended flow
	 * @param e the flow exception
	 * @param request the current request
	 * @param response the current response
	 */
	protected void defaultHandleException(String flowId, FlowException e, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (e instanceof NoSuchFlowExecutionException && flowId != null) {
			if (!response.isCommitted()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Restarting a new execution of previously ended flow '" + flowId + "'");
				}
				// by default, attempt to restart the flow
				String flowUrl = flowUrlHandler.createFlowDefinitionUrl(flowId, null, request);
				sendRedirect(flowUrl, request, response);
			}
		} else {
			throw e;
		}
	}

	/**
	 * Sends a redirect to the requested url using {@link HttpServletResponse#sendRedirect(String)}.Called to actually
	 * perform flow execution redirects, flow definition redirects, and external redirects. Subclasses may override to
	 * customize general Web Flow system redirect behavior.
	 * @param url the url to redirect to
	 * @param request the current request
	 * @param response the current response
	 * @throws IOException an exception occurred
	 */
	protected void sendRedirect(String url, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (ajaxHandler.isAjaxRequest(request, response)) {
			ajaxHandler.sendAjaxRedirect(url, request, response, false);
		} else {
			if (redirectHttp10Compatible) {
				// Always send status code 302.
				response.sendRedirect(response.encodeRedirectURL(url));
			} else {
				// Correct HTTP status code is 303, in particular for POST requests.
				response.setStatus(303);
				response.setHeader("Location", response.encodeRedirectURL(url));
			}
		}
	}

	// internal helpers

	private void handleFlowExecutionResult(FlowExecutionResult result, ServletExternalContext context,
			HttpServletRequest request, HttpServletResponse response, FlowHandler handler) throws IOException {
		if (result.isPaused()) {
			if (context.getFlowExecutionRedirectRequested()) {
				sendFlowExecutionRedirect(result, context, request, response);
			} else if (context.getFlowDefinitionRedirectRequested()) {
				sendFlowDefinitionRedirect(result, context, request, response);
			} else if (context.getExternalRedirectRequested()) {
				sendExternalRedirect(context.getExternalRedirectUrl(), request, response);
			}
		} else if (result.isEnded()) {
			if (context.getFlowDefinitionRedirectRequested()) {
				sendFlowDefinitionRedirect(result, context, request, response);
			} else if (context.getExternalRedirectRequested()) {
				sendExternalRedirect(context.getExternalRedirectUrl(), request, response);
			} else {
				String location = handler.handleExecutionOutcome(result.getOutcome(), request, response);
				if (location != null) {
					sendExternalRedirect(location, request, response);
				} else {
					defaultHandleExecutionOutcome(result.getFlowId(), result.getOutcome(), context, request, response);
				}
			}
		} else {
			throw new IllegalStateException("Execution result should have been one of [paused] or [ended]");
		}
	}

	private void sendFlowExecutionRedirect(FlowExecutionResult result, ServletExternalContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = flowUrlHandler.createFlowExecutionUrl(result.getFlowId(), result.getPausedKey(), request);
		if (logger.isDebugEnabled()) {
			logger.debug("Sending flow execution redirect to '" + url + "'");
		}
		if (context.isAjaxRequest()) {
			ajaxHandler.sendAjaxRedirect(url, request, response, context.getRedirectInPopup());
		} else {
			sendRedirect(url, request, response);
		}
	}

	private void sendFlowDefinitionRedirect(FlowExecutionResult result, ServletExternalContext context,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		String flowId = context.getFlowRedirectFlowId();
		MutableAttributeMap<Object> input = context.getFlowRedirectFlowInput();
		if (result.isPaused()) {
			input.put(REFERER_FLOW_EXECUTION_ATTRIBUTE, result.getPausedKey());
		}
		String url = flowUrlHandler.createFlowDefinitionUrl(flowId, input, request);
		if (logger.isDebugEnabled()) {
			logger.debug("Sending flow definition redirect to '" + url + "'");
		}
		sendRedirect(url, request, response);
	}

	private void sendExternalRedirect(String location, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Sending external redirect to '" + location + "'");
		}
		if (location.startsWith(SERVLET_RELATIVE_LOCATION_PREFIX)) {
			sendServletRelativeRedirect(location.substring(SERVLET_RELATIVE_LOCATION_PREFIX.length()), request,
					response);
		} else if (location.startsWith(CONTEXT_RELATIVE_LOCATION_PREFIX)) {
			sendContextRelativeRedirect(location.substring(CONTEXT_RELATIVE_LOCATION_PREFIX.length()), request,
					response);
		} else if (location.startsWith(SERVER_RELATIVE_LOCATION_PREFIX)) {
			String url = location.substring(SERVER_RELATIVE_LOCATION_PREFIX.length());
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
			sendRedirect(url, request, response);
		} else if (location.startsWith("http://") || location.startsWith("https://")) {
			sendRedirect(location, request, response);
		} else {
			if (isRedirectServletRelative(request)) {
				sendServletRelativeRedirect(location, request, response);
			} else {
				sendContextRelativeRedirect(location, request, response);
			}
		}
	}

	/**
	 * Returns true if the servlet path should automatically be prepended to an external redirect URL for which a prefix
	 * such as "contextRelative: was not specified. This answer depends on how the MVC Dispatcher Servlet is mapped: (1)
	 * default servlet, (2) prefix, (3) extension, (4) exact match. In (1), (3), and (4) it doesn't make sense to
	 * prepend the servlet path, which contains the entire URL after the context path.
	 * 
	 * Because there is no simple way to get the servlet mapping, this method is implemented to return True if path info
	 * is not null. Also see SWF-1385.
	 */
	private boolean isRedirectServletRelative(HttpServletRequest request) {
		return (request.getPathInfo() != null);
	}

	private void sendContextRelativeRedirect(String location, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		StringBuffer url = new StringBuffer(request.getContextPath());
		if (!location.startsWith("/")) {
			url.append('/');
		}
		url.append(location);
		sendRedirect(url.toString(), request, response);
	}

	private void sendServletRelativeRedirect(String location, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		StringBuffer url = new StringBuffer(request.getContextPath());
		url.append(request.getServletPath());
		if (!location.startsWith("/")) {
			url.append('/');
		}
		url.append(location);
		sendRedirect(url.toString(), request, response);
	}

	private void handleFlowException(FlowException e, HttpServletRequest request, HttpServletResponse response,
			FlowHandler handler) throws IOException {
		String location = handler.handleException(e, request, response);
		if (location != null) {
			sendExternalRedirect(location, request, response);
		} else {
			defaultHandleException(getFlowId(handler, request), e, request, response);
		}
	}

	private String getFlowId(FlowHandler handler, HttpServletRequest request) {
		String flowId = handler.getFlowId();
		if (flowId != null) {
			return flowId;
		} else {
			return defaultGetFlowId(request);
		}
	}

	private MutableAttributeMap<Object> getInputMap(FlowHandler handler, HttpServletRequest request) {
		MutableAttributeMap<Object> input = handler.createExecutionInputMap(request);
		if (input != null) {
			return input;
		} else {
			return defaultCreateFlowExecutionInputMap(request);
		}
	}
}
