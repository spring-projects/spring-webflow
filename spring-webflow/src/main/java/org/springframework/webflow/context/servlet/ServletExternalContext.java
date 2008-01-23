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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;
import org.springframework.webflow.context.AbstractFlowRequestInfo;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.FlowDefinitionRequestInfo;
import org.springframework.webflow.context.FlowExecutionRequestInfo;
import org.springframework.webflow.context.RequestPath;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * Provides contextual information about an HTTP Servlet environment that has interacted with Spring Web Flow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public class ServletExternalContext implements ExternalContext {

	/** The default encoding scheme: UTF-8 */
	private static final String DEFAULT_ENCODING_SCHEME = "UTF-8";

	/** The accept header value that signifies an Ajax request */
	private static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";

	/** Alternate request paramater to indicate an ajax request for cases when control of the header is not available */
	private static final String AJAX_SOURCE_PARAM = "ajaxSource";

	/** The response header to be set on an Ajax redirect */
	private static final String FLOW_REDIRECT_URL_HEADER = "Flow-Redirect-URL";

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

	private FlowRequestInfo flowRequestInfo;

	private String encodingScheme = DEFAULT_ENCODING_SCHEME;

	private FlowExecutionRedirector flowExecutionRedirector;

	private FlowDefinitionRedirector flowDefinitionRedirector;

	private String resourceUri;

	private short result;

	private String processedFlowExecutionKey;

	private FlowException exception;

	/**
	 * Create a new external context wrapping given servlet HTTP request and response and given servlet context.
	 * @param context the servlet context
	 * @param request the servlet request
	 * @param response the servlet response
	 */
	public ServletExternalContext(ServletContext context, ServletRequest request, ServletResponse response) {
		this.context = context;
		try {
			this.request = (HttpServletRequest) request;
			this.response = (HttpServletResponse) response;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Request and response objects must be HTTP objects", e);
		}
		this.requestParameterMap = new LocalParameterMap(new HttpServletRequestParameterMap(this.request));
		this.requestMap = new LocalAttributeMap(new HttpServletRequestMap(this.request));
		this.sessionMap = new LocalSharedAttributeMap(new HttpSessionMap(this.request));
		this.applicationMap = new LocalSharedAttributeMap(new HttpServletContextMap(context));
		this.flowRequestInfo = parseFlowRequestInfo(this.request);
	}

	public static class FlowRequestInfo {
		private String flowId;

		private String flowExecutionKey;

		private RequestPath requestPath;

		public FlowRequestInfo(String flowId, String flowExecutionKey, RequestPath requestPath) {
			this.flowId = flowId;
			this.flowExecutionKey = flowExecutionKey;
			this.requestPath = requestPath;
		}

		public String getFlowId() {
			return flowId;
		}

		public String getFlowExecutionKey() {
			return flowExecutionKey;
		}

		public RequestPath getRequestPath() {
			return requestPath;
		}
	}

	public String getFlowId() {
		return flowRequestInfo.getFlowId();
	}

	public String getFlowExecutionKey() {
		return flowRequestInfo.getFlowExecutionKey();
	}

	public RequestPath getRequestPath() {
		return flowRequestInfo.getRequestPath();
	}

	public String getRequestMethod() {
		return request.getMethod();
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

	public Object getContext() {
		return context;
	}

	public Object getRequest() {
		return request;
	}

	public Object getResponse() {
		return response;
	}

	public boolean isResponseCommitted() {
		return flowExecutionRedirector != null || flowDefinitionRedirector != null || resourceUri != null;
	}

	public PrintWriter getResponseWriter() {
		try {
			return response.getWriter();
		} catch (IOException e) {
			// TODO - handle how?
			throw new RuntimeException(e);
		}
	}

	public String encode(String string) {
		try {
			return URLEncoder.encode(string, encodingScheme);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported encoding errors should never happen", e);
		}
	}

	public String buildFlowDefinitionUrl(FlowDefinitionRequestInfo requestInfo) {
		return getFlowExecutorPath() + "/" + requestInfo.getFlowDefinitionId() + requestPath(requestInfo)
				+ requestParameters(requestInfo) + fragment(requestInfo);
	}

	public String buildFlowExecutionUrl(FlowExecutionRequestInfo requestInfo, boolean contextRelative) {
		String contextRelativeUrl = getFlowExecutionsPath() + requestInfo.getFlowDefinitionId() + "/"
				+ requestInfo.getFlowExecutionKey() + requestPath(requestInfo) + requestParameters(requestInfo)
				+ fragment(requestInfo);
		if (contextRelative) {
			return contextRelativeUrl;
		} else {
			return request.getScheme() + request.getServerName() + contextRelativeUrl;
		}
	}

	public void sendFlowExecutionRedirect(FlowExecutionRequestInfo request) {
		flowExecutionRedirector = new FlowExecutionRedirector(request);
	}

	public void sendFlowDefinitionRedirect(FlowDefinitionRequestInfo request) {
		flowDefinitionRedirector = new FlowDefinitionRedirector(request);
	}

	public void sendExternalRedirect(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	// execution processing result setters

	public void setPausedResult(String flowExecutionKey) {
		result = 0;
		processedFlowExecutionKey = flowExecutionKey;
	}

	public void setEndedResult(String flowExecutionKey) {
		result = 1;
		processedFlowExecutionKey = flowExecutionKey;
	}

	public void setExceptionResult(FlowException e) {
		result = 2;
		exception = e;
		processedFlowExecutionKey = flowRequestInfo.getFlowExecutionKey();
	}

	/**
	 * Execute the flow request lifecycle, including provision of the final response.
	 * @param flowExecutor the flow executor for calling into the Spring Web Flow system
	 * @throws IOException an IOException occurred issuing the response
	 */
	public void executeFlowRequest(FlowExecutor flowExecutor) throws IOException {
		ExternalContextHolder.setExternalContext(this);
		try {
			flowExecutor.executeFlowRequest(this);
			if (isPausedResult()) {
				if (flowExecutionRedirector != null) {
					flowExecutionRedirector.issueRedirect();
				} else if (flowDefinitionRedirector != null) {
					flowDefinitionRedirector.issueRedirect();
				} else if (resourceUri != null) {
					sendRedirect(resourceUri);
				} else {
					// commit response?
				}
			} else if (isEndResult()) {
				if (flowExecutionRedirector != null) {
					if (flowExecutionRedirector.redirectsTo(processedFlowExecutionKey)) {
						throw new IllegalStateException(
								"You cannot send a flow execution redirect when this execution has ended - programmer error");
					} else {
						flowExecutionRedirector.issueRedirect();
					}
				} else if (flowDefinitionRedirector != null) {
					flowDefinitionRedirector.issueRedirect();
				} else if (resourceUri != null) {
					sendRedirect(resourceUri);
				} else {
					// commit response?
				}
			} else if (isExceptionResult()) {
				// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
				throw exception;
			}
		} finally {
			ExternalContextHolder.setExternalContext(null);
		}
	}

	// hooks subclasses may override

	protected FlowRequestInfo parseFlowRequestInfo(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			throw new IllegalArgumentException(
					"The requestPathInfo is null: unable to extract flow definition id or flow execution key");
		}
		String flowId;
		String flowExecutionKey;
		RequestPath path = new RequestPath(pathInfo);
		if (path.getElement(0).equals("executions")) {
			flowId = path.getElement(1);
			flowExecutionKey = path.getElement(2);
			if (path.getElementCount() > 3) {
				path = path.pop(3);
			} else {
				path = null;
			}
		} else {
			flowId = path.getElement(0);
			flowExecutionKey = null;
			if (path.getElementCount() > 1) {
				path = path.pop(1);
			} else {
				path = null;
			}
		}
		return new FlowRequestInfo(flowId, flowExecutionKey, path);
	}

	// private helpers

	private String getFlowExecutorPath() {
		return request.getContextPath() + request.getServletPath();
	}

	private String getFlowExecutionsPath() {
		return getFlowExecutorPath() + "/executions/";
	}

	private boolean isPausedResult() {
		return result == 0;
	}

	private boolean isEndResult() {
		return result == 1;
	}

	private boolean isExceptionResult() {
		return result == 2;
	}

	private String requestPath(AbstractFlowRequestInfo requestInfo) {
		if (requestInfo.getRequestPath() == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		String[] requestElements = requestInfo.getRequestPath().getElements();
		for (int i = 0; i < requestElements.length; i++) {
			buffer.append('/').append(encode(requestElements[i], encodingScheme));
		}
		return buffer.toString();
	}

	private String requestParameters(AbstractFlowRequestInfo requestInfo) {
		if (requestInfo.getRequestParameters() == null) {
			return "";
		}
		StringBuffer queryString = new StringBuffer();
		queryString.append('?');
		ParameterMap requestParameters = requestInfo.getRequestParameters();
		Iterator it = requestParameters.asMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String parameterName = encode((String) entry.getKey(), encodingScheme);
			String parameterValue = encode((String) entry.getValue(), encodingScheme);
			queryString.append(parameterName).append('=').append(parameterValue);
			if (it.hasNext()) {
				queryString.append('&');
			}
		}
		return queryString.toString();
	}

	private String fragment(AbstractFlowRequestInfo requestInfo) {
		if (requestInfo.getFragment() == null || requestInfo.getFragment().length() == 0) {
			return "";
		}
		return "#" + requestInfo.getFragment();
	}

	private String encode(String value, String encodingScheme) {
		try {
			return URLEncoder.encode(value, encodingScheme);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendRedirect(String targetUrl) throws IOException {
		if (isAjaxRequest()) {
			setResponseHeader(FLOW_REDIRECT_URL_HEADER, response.encodeRedirectURL(targetUrl));
		} else {
			response.sendRedirect(response.encodeRedirectURL(targetUrl));
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("requestParameterMap", getRequestParameterMap()).toString();
	}

	private abstract class FlowRedirector {
		private AbstractFlowRequestInfo requestInfo;

		public FlowRedirector(AbstractFlowRequestInfo requestInfo) {
			this.requestInfo = requestInfo;
		}

		protected AbstractFlowRequestInfo getRequestInfo() {
			return requestInfo;
		}

		public abstract void issueRedirect() throws IOException;
	}

	private class FlowExecutionRedirector extends FlowRedirector {
		public FlowExecutionRedirector(FlowExecutionRequestInfo requestInfo) {
			super(requestInfo);
		}

		public boolean redirectsTo(String flowExecutionKey) {
			FlowExecutionRequestInfo requestInfo = (FlowExecutionRequestInfo) getRequestInfo();
			return requestInfo.getFlowExecutionKey().equals(flowExecutionKey);
		}

		public void issueRedirect() throws IOException {
			FlowExecutionRequestInfo requestInfo = (FlowExecutionRequestInfo) getRequestInfo();
			String targetUrl = buildFlowExecutionUrl(requestInfo, true);
			sendRedirect(targetUrl);
		}
	}

	private class FlowDefinitionRedirector extends FlowRedirector {
		public FlowDefinitionRedirector(FlowDefinitionRequestInfo redirect) {
			super(redirect);
		}

		public void issueRedirect() throws IOException {
			FlowDefinitionRequestInfo requestInfo = (FlowDefinitionRequestInfo) getRequestInfo();
			String targetUrl = buildFlowDefinitionUrl(requestInfo);
			sendRedirect(targetUrl);
		}
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

	public void setResponseHeader(String name, String value) {
		response.setHeader(name, value);
	}
}