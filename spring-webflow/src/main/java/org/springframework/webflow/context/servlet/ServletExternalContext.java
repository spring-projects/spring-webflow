/*
 * Copyright 2004-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.webflow.context.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
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

	/** The accept header value that signifies an Ajax request */
	private static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";

	/** Alternate request parameter to indicate an Ajax request for cases when control of the header is not available */
	private static final String AJAX_SOURCE_PARAM = "ajaxSource";

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
	private AttributeMap flowDefinitionRedirectFlowInput;

	/**
	 * A string specifying an arbitrary
	 */
	private String externalRedirectUrl;

	/**
	 * The strategy for generating flow execution urls.
	 */
	private FlowUrlHandler flowUrlHandler;

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
		String acceptHeader = request.getHeader("Accept");
		String ajaxParam = request.getParameter(AJAX_SOURCE_PARAM);
		if (AJAX_ACCEPT_CONTENT_TYPE.equals(acceptHeader) || StringUtils.hasText(ajaxParam)) {
			return true;
		} else {
			return false;
		}
	}

	public String getFlowExecutionUri(String flowId, String flowExecutionKey) {
		return flowUrlHandler.createFlowExecutionUrl(flowId, flowExecutionKey, request);
	}

	public Writer getResponseWriter() {
		try {
			return response.getWriter();
		} catch (IOException e) {
			throw new IllegalStateException("Unable to obtain response writer", e);
		}
	}

	public void setResponseHeader(String name, String value) {
		response.setHeader(name, value);
	}

	public boolean isResponseCommitted() {
		return flowExecutionRedirectRequested() || flowDefinitionRedirectRequested() || externalRedirectRequested();
	}

	public void requestFlowExecutionRedirect() {
		flowExecutionRedirectRequested = true;
	}

	public void requestExternalRedirect(String uri) {
		externalRedirectUrl = uri;
	}

	public void requestFlowDefinitionRedirect(String flowId, AttributeMap input) {
		flowDefinitionRedirectFlowId = flowId;
		flowDefinitionRedirectFlowInput = input;
	}

	// implementation specific methods

	public boolean flowExecutionRedirectRequested() {
		return flowExecutionRedirectRequested;
	}

	public boolean flowDefinitionRedirectRequested() {
		return flowDefinitionRedirectFlowId != null;
	}

	public String getFlowRedirectFlowId() {
		return flowDefinitionRedirectFlowId;
	}

	public AttributeMap getFlowRedirectFlowInput() {
		return flowDefinitionRedirectFlowInput;
	}

	public boolean externalRedirectRequested() {
		return externalRedirectUrl != null;
	}

	public String getExternalRedirectUrl() {
		return externalRedirectUrl;
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

}