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
package org.springframework.webflow.test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.collection.SharedMapDecorator;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Mock implementation of the {@link ExternalContext} interface.
 * @see ExternalContext
 * @author Keith Donald
 */
public class MockExternalContext implements ExternalContext {

	private String contextPath;

	private ParameterMap requestParameterMap = new MockParameterMap();

	private MutableAttributeMap requestMap = new LocalAttributeMap();

	private SharedAttributeMap sessionMap = new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap()));

	private SharedAttributeMap globalSessionMap = sessionMap;

	private SharedAttributeMap applicationMap = new LocalSharedAttributeMap(new SharedMapDecorator(new HashMap()));

	private Object nativeContext = new Object();

	private Object nativeRequest = new Object();

	private Object nativeResponse = new Object();

	private StringWriter responseWriter = new StringWriter();

	private boolean ajaxRequest;

	private Map responseHeaders = new HashMap();

	private boolean flowExecutionRedirectRequested;

	private String flowDefinitionRedirectFlowId;

	private AttributeMap flowDefinitionRedirectFlowInput;

	private String externalRedirectUrl;

	/**
	 * Creates a mock external context with an empty request parameter map. Allows for bean style usage.
	 */
	public MockExternalContext() {
	}

	/**
	 * Creates a mock external context with the specified parameters in the request parameter map. All other properties
	 * of the external context can be set using the appropriate setter.
	 * @param requestParameterMap the request parameters
	 */
	public MockExternalContext(ParameterMap requestParameterMap) {
		if (requestParameterMap != null) {
			this.requestParameterMap = requestParameterMap;
		}
	}

	// implementing external context

	public String getContextPath() {
		return contextPath;
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
		return globalSessionMap;
	}

	public SharedAttributeMap getApplicationMap() {
		return applicationMap;
	}

	public Object getNativeContext() {
		return nativeContext;
	}

	public Object getNativeRequest() {
		return nativeRequest;
	}

	public Object getNativeResponse() {
		return nativeResponse;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public String getFlowExecutionUri(String flowId, String flowExecutionKey) {
		return "/" + flowId + "?execution=" + flowExecutionKey;
	}

	public Writer getResponseWriter() {
		return responseWriter;
	}

	public void setResponseHeader(String name, String value) {
		this.responseHeaders.put(name, value);
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

	/**
	 * Set the context path of the application.
	 * @param contextPath the context path
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Set the request parameter map.
	 * @see ExternalContext#getRequestParameterMap()
	 */
	public void setRequestParameterMap(ParameterMap requestParameterMap) {
		this.requestParameterMap = requestParameterMap;
	}

	/**
	 * Set the request attribute map.
	 * @see ExternalContext#getRequestMap()
	 */
	public void setRequestMap(MutableAttributeMap requestMap) {
		this.requestMap = requestMap;
	}

	/**
	 * Set the session attribute map.
	 * @see ExternalContext#getSessionMap()
	 */
	public void setSessionMap(SharedAttributeMap sessionMap) {
		this.sessionMap = sessionMap;
	}

	/**
	 * Set the global session attribute map. By default the session attribute map and the global session attribute map
	 * are one and the same.
	 * @see ExternalContext#getGlobalSessionMap()
	 */
	public void setGlobalSessionMap(SharedAttributeMap globalSessionMap) {
		this.globalSessionMap = globalSessionMap;
	}

	/**
	 * Set the application attribute map.
	 * @see ExternalContext#getApplicationMap()
	 */
	public void setApplicationMap(SharedAttributeMap applicationMap) {
		this.applicationMap = applicationMap;
	}

	/**
	 * Set the native context object.
	 * @param nativeContext the native context
	 */
	public void setNativeContext(Object nativeContext) {
		this.nativeContext = nativeContext;
	}

	/**
	 * Set the native request object.
	 * @param nativeRequest the native request object
	 */
	public void setNativeRequest(Object nativeRequest) {
		this.nativeRequest = nativeRequest;
	}

	/**
	 * Set the native response object.
	 * @param nativeResponse the native response object
	 */
	public void setNativeResponse(Object nativeResponse) {
		this.nativeResponse = nativeResponse;
	}

	// convenience helpers

	/**
	 * Returns the request parameter map as a {@link MockParameterMap} for convenient access in a unit test.
	 * @see #getRequestParameterMap()
	 */
	public MockParameterMap getMockRequestParameterMap() {
		return (MockParameterMap) requestParameterMap;
	}

	/**
	 * Puts a request parameter into the mock parameter map.
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public void putRequestParameter(String parameterName, String parameterValue) {
		getMockRequestParameterMap().put(parameterName, parameterValue);
	}

	/**
	 * Puts a multi-valued request parameter into the mock parameter map.
	 * @param parameterName the parameter name
	 * @param parameterValues the parameter values
	 */
	public void putRequestParameter(String parameterName, String[] parameterValues) {
		getMockRequestParameterMap().put(parameterName, parameterValues);
	}

	/**
	 * Set whether this request is an ajax request.
	 * @param ajaxRequest true or false
	 */
	public void setAjaxRequest(boolean ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	/**
	 * Returns the value of the response header entry
	 * @param name the entry name
	 * @return the entry value, or null if no entry was set with this name
	 */
	public String getResponseHeader(String name) {
		return (String) responseHeaders.get(name);
	}

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

}