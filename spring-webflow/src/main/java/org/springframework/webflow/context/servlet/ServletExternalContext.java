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
package org.springframework.webflow.context.servlet;

import java.io.IOException;
import java.io.Writer;
import java.security.Principal;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Provides contextual information about an HTTP Servlet environment that has interacted with Spring Web Flow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public class ServletExternalContext implements ExternalContext {

	/**
	 * The context.
	 */
	private ServletContext context;

	/**
	 * The request.
	 */
	private HttpServletRequest request;

	/**
	 * The response.
	 */
	private HttpServletResponse response;

	/**
	 * An accessor for the HTTP request parameter map.
	 */
	private ParameterMap requestParameterMap;

	/**
	 * An accessor for the HTTP request attribute map.
	 */
	private MutableAttributeMap requestMap;

	/**
	 * An accessor for the HTTP session map.
	 */
	private SharedAttributeMap sessionMap;

	/**
	 * An accessor for the servlet context application map.
	 */
	private SharedAttributeMap applicationMap;

	/**
	 * A flag indicating if the flow committed the response. Set to true by requesting an execution redirect, definition
	 * redirect, external redirect, or by calling {@link ExternalContext#recordResponseComplete()}
	 */
	private boolean responseComplete;

	/**
	 * A flag indicating if a flow execution redirect has been requested.
	 */
	private boolean flowExecutionRedirectRequested;

	/**
	 * A string specifying the id of the flow to redirect to after request processing. If null, no flow definition
	 * redirect has been requested.
	 */
	private String flowDefinitionRedirectFlowId;

	/**
	 * Input to pass the flow definition upon redirecting. May be null. Never set unless
	 * {@link #flowDefinitionRedirectFlowId} has been set.
	 */
	private MutableAttributeMap flowDefinitionRedirectFlowInput;

	/**
	 * A string specifying an arbitrary
	 */
	private String externalRedirectUrl;

	/**
	 * The strategy for generating flow execution urls.
	 */
	private FlowUrlHandler flowUrlHandler;

	/**
	 * Whether this external request context originated from an Ajax request or not.
	 */
	private boolean ajaxRequest;

	/**
	 * In the case where a redirect response is requested, this flag indicates if the redirect should be issued from a
	 * popup dialog.
	 */
	private boolean redirectInPopup;

	/**
	 * Create a new external context wrapping given servlet HTTP request and response and given servlet context.
	 * @param context the servlet context
	 * @param request the http servlet request
	 * @param response the http servlet response
	 */
	public ServletExternalContext(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
		init(context, request, response, new DefaultFlowUrlHandler());
	}

	/**
	 * Create a new external context wrapping given servlet HTTP request and response and given servlet context.
	 * @param context the servlet context
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param flowUrlHandler the flow url handler
	 */
	public ServletExternalContext(ServletContext context, HttpServletRequest request, HttpServletResponse response,
			FlowUrlHandler flowUrlHandler) {
		init(context, request, response, flowUrlHandler);
	}

	/**
	 * Indicates if the current request from this client is an ajax request. This flag may effect the handling of
	 * response writing within Spring Web Flow.
	 * @param ajaxRequest the ajax request flag
	 */
	public void setAjaxRequest(boolean ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	// implementing external context

	public String getContextPath() {
		return request.getContextPath();
	}

	public ParameterMap getRequestParameterMap() {
		return requestParameterMap;
	}

	public MutableAttributeMap getRequestMap() {
		return requestMap;
	}

	public SharedAttributeMap getSessionMap() {
		return sessionMap;
	}

	public SharedAttributeMap getGlobalSessionMap() {
		return getSessionMap();
	}

	public SharedAttributeMap getApplicationMap() {
		return applicationMap;
	}

	public Principal getCurrentUser() {
		return request.getUserPrincipal();
	}

	public Locale getLocale() {
		return request.getLocale();
	}

	public Object getNativeContext() {
		return context;
	}

	public Object getNativeRequest() {
		return request;
	}

	public Object getNativeResponse() {
		return response;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public String getFlowExecutionUrl(String flowId, String flowExecutionKey) {
		return flowUrlHandler.createFlowExecutionUrl(flowId, flowExecutionKey, request);
	}

	public Writer getResponseWriter() throws IllegalStateException {
		assertResponseAllowed();
		try {
			return response.getWriter();
		} catch (IOException e) {
			IllegalStateException ise = new IllegalStateException("Unable to access the response Writer");
			ise.initCause(e);
			throw ise;
		}
	}

	public boolean isResponseAllowed() {
		return !responseComplete;
	}

	public boolean isResponseComplete() {
		return responseComplete;
	}

	public void recordResponseComplete() {
		responseComplete = true;
	}

	public boolean isResponseCompleteFlowExecutionRedirect() {
		return flowExecutionRedirectRequested;
	}

	public void requestFlowExecutionRedirect() throws IllegalStateException {
		assertResponseAllowed();
		flowExecutionRedirectRequested = true;
		recordResponseComplete();
	}

	public void requestFlowDefinitionRedirect(String flowId, MutableAttributeMap input) throws IllegalStateException {
		assertResponseAllowed();
		flowDefinitionRedirectFlowId = flowId;
		flowDefinitionRedirectFlowInput = input;
		recordResponseComplete();
	}

	public void requestExternalRedirect(String location) throws IllegalStateException {
		assertResponseAllowed();
		externalRedirectUrl = location;
		recordResponseComplete();
	}

	public void requestRedirectInPopup() throws IllegalStateException {
		if (isRedirectRequested()) {
			redirectInPopup = true;
		} else {
			throw new IllegalStateException(
					"Only call requestRedirectInPopup after a redirect has been requested by calling requestFlowExecutionRedirect, requestFlowDefinitionRedirect, or requestExternalRedirect");
		}
	}

	// implementation specific methods

	/**
	 * Returns the flag indicating if a flow execution redirect response has been requested by the flow.
	 */
	public boolean getFlowExecutionRedirectRequested() {
		return flowExecutionRedirectRequested;
	}

	/**
	 * Returns the flag indicating if a flow definition redirect response has been requested by the flow.
	 */
	public boolean getFlowDefinitionRedirectRequested() {
		return flowDefinitionRedirectFlowId != null;
	}

	/**
	 * Returns the id of the flow definition to redirect to. Only set when {@link #getFlowDefinitionRedirectRequested()}
	 * returns true.
	 */
	public String getFlowRedirectFlowId() {
		return flowDefinitionRedirectFlowId;
	}

	/**
	 * Returns the input to pass the flow definition through the redirect. Only set when
	 * {@link #getFlowDefinitionRedirectRequested()} returns true.
	 */
	public MutableAttributeMap getFlowRedirectFlowInput() {
		return flowDefinitionRedirectFlowInput;
	}

	/**
	 * Returns the flag indicating if an external redirect response has been requested by the flow.
	 */
	public boolean getExternalRedirectRequested() {
		return externalRedirectUrl != null;
	}

	/**
	 * Returns the URL to redirect to. Only set if {@link #getExternalRedirectRequested()} returns true.
	 */
	public String getExternalRedirectUrl() {
		return externalRedirectUrl;
	}

	/**
	 * If a redirect response has been requested, indicates if the redirect should be issued from a popup dialog.
	 */
	public boolean getRedirectInPopup() {
		return redirectInPopup;
	}

	// hooks for subclasses

	/**
	 * Returns the servlet context.
	 */
	protected ServletContext getContext() {
		return context;
	}

	/**
	 * Returns the underlying HttpServletRequest.
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the underlying HttpServletResponse.
	 */
	protected HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Returns the configured flow url handler.
	 */
	protected FlowUrlHandler getFlowUrlHandler() {
		return flowUrlHandler;
	}

	// private helpers

	private void init(ServletContext context, HttpServletRequest request, HttpServletResponse response,
			FlowUrlHandler flowUrlHandler) {
		this.context = context;
		this.request = request;
		this.response = response;
		this.requestParameterMap = new LocalParameterMap(new HttpServletRequestParameterMap(request));
		this.requestMap = new LocalAttributeMap(new HttpServletRequestMap(request));
		this.sessionMap = new LocalSharedAttributeMap(new HttpSessionMap(request));
		this.applicationMap = new LocalSharedAttributeMap(new HttpServletContextMap(context));
		this.flowUrlHandler = flowUrlHandler;
	}

	private void assertResponseAllowed() throws IllegalStateException {
		if (!isResponseAllowed()) {
			if (getFlowExecutionRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because a redirect has already been requested on this ExternalContext");
			}
			if (getFlowDefinitionRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because a flowRedirect has already been requested on this ExternalContext");
			}
			if (getExternalRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because an externalRedirect has already been requested on this ExternalContext");
			}
			throw new IllegalStateException(
					"A response is not allowed because one has already been completed on this ExternalContext");
		}
	}

	private boolean isRedirectRequested() {
		return getFlowExecutionRedirectRequested() || getFlowDefinitionRedirectRequested()
				|| getExternalRedirectRequested();
	}

}