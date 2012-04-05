/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.webflow.context.portlet;

import java.io.IOException;
import java.io.Writer;
import java.security.Principal;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Provides contextual information about an portlet environment that has interacted with Spring Web Flow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 * @author Scott Andrews
 */
public class PortletExternalContext implements ExternalContext {

	protected static final short ACTION_PHASE = 1;

	protected static final short RENDER_PHASE = 2;

	protected static final short RESOURCE_PHASE = 3;

	/**
	 * The context.
	 */
	private PortletContext context;

	/**
	 * The request.
	 */
	private PortletRequest request;

	/**
	 * The response.
	 */
	private PortletResponse response;

	/**
	 * The portlet request phase: render, action, resource
	 */
	private short requestPhase;

	/**
	 * An accessor for the HTTP request parameter map.
	 */
	private ParameterMap requestParameterMap;

	/**
	 * An accessor for the HTTP request attribute map.
	 */
	private MutableAttributeMap<Object> requestMap;

	/**
	 * An accessor for the HTTP session map.
	 */
	private SharedAttributeMap<Object> sessionMap;

	/**
	 * An accessor for the servlet context application map.
	 */
	private SharedAttributeMap<Object> applicationMap;

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
	private MutableAttributeMap<Object> flowDefinitionRedirectFlowInput;

	/**
	 * A string specifying an arbitrary
	 */
	private String externalRedirectUrl;

	/**
	 * The strategy for generating flow execution urls.
	 */
	private FlowUrlHandler flowUrlHandler;

	/**
	 * In the case where a redirect response is requested, this flag indicates if the redirect should be issued from a
	 * popup dialog.
	 */
	private boolean redirectInPopup;

	/**
	 * Create a new external context wrapping given portlet action request and response and given portlet context.
	 * @param context the portal context
	 * @param request the portlet request
	 * @param response the portlet response
	 */
	public PortletExternalContext(PortletContext context, PortletRequest request, PortletResponse response) {
		init(context, request, response, new DefaultFlowUrlHandler());
	}

	/**
	 * Create a new external context wrapping given portlet action request and response and given portlet context.
	 * @param context the portal context
	 * @param request the portlet request
	 * @param response the portlet response
	 * @param flowUrlHandler the flow url handler
	 */
	public PortletExternalContext(PortletContext context, PortletRequest request, PortletResponse response,
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

	public MutableAttributeMap<Object> getRequestMap() {
		return requestMap;
	}

	public SharedAttributeMap<Object> getSessionMap() {
		return sessionMap;
	}

	public SharedAttributeMap<Object> getGlobalSessionMap() {
		return getSessionMap();
	}

	public SharedAttributeMap<Object> getApplicationMap() {
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
		return false;
	}

	public String getFlowExecutionUrl(String flowId, String flowExecutionKey) {
		if (isRenderPhase()) {
			return flowUrlHandler.createFlowExecutionUrl(flowId, flowExecutionKey, (RenderResponse) response);
		} else if (isResourcePhase()) {
			return flowUrlHandler.createFlowExecutionUrl(flowId, flowExecutionKey, (ResourceResponse) response);
		} else {
			throw new IllegalStateException(
					"A flow execution action URL can only be obtained in a RenderRequest or a ResourceRequest");
		}
	}

	public Writer getResponseWriter() throws IllegalStateException {
		assertResponseAllowed();
		try {
			return ((MimeResponse) response).getWriter();
		} catch (IOException e) {
			IllegalStateException ise = new IllegalStateException("Unable to access the response Writer");
			ise.initCause(e);
			throw ise;
		}
	}

	public boolean isResponseAllowed() {
		return (isRenderPhase() || isResourcePhase()) && !responseComplete;
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
		assertRedirectResponseAllowed();
		flowExecutionRedirectRequested = true;
		recordResponseComplete();
	}

	public void requestFlowDefinitionRedirect(String flowId, MutableAttributeMap<?> input) throws IllegalStateException {
		assertRedirectResponseAllowed();
		flowDefinitionRedirectFlowId = flowId;
		flowDefinitionRedirectFlowInput = new LocalAttributeMap<Object>();
		if (input != null) {
			flowDefinitionRedirectFlowInput.putAll(input);
		}
		recordResponseComplete();
	}

	public void requestExternalRedirect(String uri) throws IllegalStateException {
		assertRedirectResponseAllowed();
		externalRedirectUrl = uri;
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
	public MutableAttributeMap<Object> getFlowRedirectFlowInput() {
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

	/**
	 * Returns true if the current request phase is the action phase
	 */
	public boolean isActionPhase() {
		return requestPhase == ACTION_PHASE;
	}

	/**
	 * Returns true if the current request phase is the render phase
	 */
	public boolean isRenderPhase() {
		return requestPhase == RENDER_PHASE;
	}

	/**
	 * Returns true if the current request phase is the resource phase
	 */
	public boolean isResourcePhase() {
		return requestPhase == RESOURCE_PHASE;
	}

	// private helpers

	private void init(PortletContext context, PortletRequest request, PortletResponse response,
			FlowUrlHandler flowUrlHandler) {
		this.context = context;
		this.request = request;
		this.response = response;
		this.requestParameterMap = new LocalParameterMap(new PortletRequestParameterMap(request));
		this.requestMap = new LocalAttributeMap<Object>(new PortletRequestMap(request));
		this.sessionMap = new LocalSharedAttributeMap<Object>(new PortletSessionMap(request));
		this.applicationMap = new LocalSharedAttributeMap<Object>(new PortletContextMap(context));
		this.flowUrlHandler = flowUrlHandler;
		if (request instanceof ActionRequest && response instanceof ActionResponse) {
			requestPhase = ACTION_PHASE;
		} else if (request instanceof RenderRequest && response instanceof RenderResponse) {
			requestPhase = RENDER_PHASE;
		} else if (request instanceof ResourceRequest && response instanceof ResourceResponse) {
			requestPhase = RESOURCE_PHASE;
		} else {
			throw new IllegalArgumentException("Unknown portlet phase, expected: action, render, or resource");
		}
	}

	private void assertResponseAllowed() throws IllegalStateException {
		if (!isRenderPhase() && !isResourcePhase()) {
			throw new IllegalStateException(
					"A response is not allowed because the current PortletRequest is neither a RenderRequest nor a ResourceRequest");
		}
		if (responseComplete) {
			throw new IllegalStateException(
					"A response is not allowed because recordResponseComplete() has already been called on this ExternalContext");
		}
	}

	private void assertRedirectResponseAllowed() throws IllegalStateException {
		if (!isActionPhase()) {
			throw new IllegalStateException(
					"A redirect is not allowed because the current PortletRequest is not a ActionRequest");
		}
		if (responseComplete) {
			throw new IllegalStateException(
					"A redirect is not allowed because a response has already been completed on this ExternalContext");
		}
	}

	private boolean isRedirectRequested() {
		return getFlowExecutionRedirectRequested() || getFlowDefinitionRedirectRequested()
				|| getExternalRedirectRequested();
	}

}
