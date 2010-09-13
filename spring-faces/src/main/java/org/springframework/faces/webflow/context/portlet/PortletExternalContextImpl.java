/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.faces.webflow.context.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.faces.webflow.JsfRuntimeInformation;
import org.springframework.util.Assert;
import org.springframework.webflow.context.portlet.PortletContextMap;
import org.springframework.webflow.context.portlet.PortletRequestMap;
import org.springframework.webflow.context.portlet.PortletSessionMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;

/**
 * An implementation of {@link ExternalContext} for use with Portlet requests.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class PortletExternalContextImpl extends ExternalContext {

	private ActionRequest actionRequest;

	private Map<String, Object> applicationMap;

	private boolean isActionRequest;

	private PortletContext portletContext;

	private PortletRequest portletRequest;

	private PortletResponse portletResponse;

	private Map<String, String> initParameterMap;

	private Map<String, String> requestHeaderMap;

	private Map<String, String[]> requestHeaderValuesMap;

	private Map<String, Object> requestMap;

	private Map<String, String> requestParameterMap;

	private Map<String, String[]> requestParameterValuesMap;

	private MapAdaptable sessionMap;

	public PortletExternalContextImpl(PortletContext portletContext, PortletRequest portletRequest,
			PortletResponse portletResponse) {
		this.portletContext = portletContext;
		this.portletRequest = portletRequest;
		this.portletResponse = portletResponse;
		if (portletRequest instanceof ActionRequest) {
			this.actionRequest = (ActionRequest) portletRequest;
			this.isActionRequest = true;
		}
	}

	public void dispatch(String path) throws IOException {
		Assert.isTrue(!isActionRequest);
		PortletRequestDispatcher requestDispatcher = portletContext.getRequestDispatcher(path);
		try {
			requestDispatcher.include((RenderRequest) portletRequest, (RenderResponse) portletResponse);
		} catch (PortletException exception) {
			if (exception.getMessage() != null) {
				throw new FacesException(exception.getMessage(), exception);
			}
			throw new FacesException(exception);
		}
	}

	public String encodeActionURL(String url) {
		Assert.notNull(url);
		return portletResponse.encodeURL(url);
	}

	public String encodeNamespace(String name) {
		Assert.isTrue(!isActionRequest);
		return name + ((RenderResponse) portletResponse).getNamespace();
	}

	@Override
	public String encodeResourceURL(String url) {
		Assert.notNull(url);
		return portletResponse.encodeURL(url);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getApplicationMap() {
		if (applicationMap == null) {
			applicationMap = new PortletContextMap(portletContext);
		}
		return applicationMap;
	}

	@Override
	public String getAuthType() {
		return portletRequest.getAuthType();
	}

	@Override
	public Object getContext() {
		return portletContext;
	}

	@Override
	public String getInitParameter(String name) {
		return portletContext.getInitParameter(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getInitParameterMap() {
		if (initParameterMap == null) {
			initParameterMap = new InitParameterMap(portletContext);
		}
		return initParameterMap;
	}

	@Override
	public String getRemoteUser() {
		return portletRequest.getRemoteUser();
	}

	@Override
	public Object getRequest() {
		return portletRequest;
	}

	@Override
	public String getRequestContentType() {
		return null;
	}

	@Override
	public String getRequestContextPath() {
		return portletRequest.getContextPath();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRequestCookieMap() {
		return Collections.EMPTY_MAP;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getRequestHeaderMap() {
		if (requestHeaderMap == null) {
			RequestPropertyMap map = new RequestPropertyMap(portletRequest);
			map.setUseArrayForMultiValueAttributes(Boolean.FALSE);
			requestHeaderMap = map;
		}
		return requestHeaderMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String[]> getRequestHeaderValuesMap() {
		if (requestHeaderValuesMap == null) {
			RequestPropertyMap map = new RequestPropertyMap(portletRequest);
			map.setUseArrayForMultiValueAttributes(Boolean.TRUE);
			requestHeaderValuesMap = map;
		}
		return requestHeaderValuesMap;
	}

	@Override
	public Locale getRequestLocale() {
		return portletRequest.getLocale();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<Locale> getRequestLocales() {
		return CollectionUtils.toIterator(portletRequest.getLocales());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRequestMap() {
		if (requestMap == null) {
			requestMap = new PortletRequestMap(portletRequest);
		}
		return requestMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getRequestParameterMap() {
		if (requestParameterMap == null) {
			RequestParameterMap map = new RequestParameterMap(portletRequest);
			map.setUseArrayForMultiValueAttributes(Boolean.FALSE);
			requestParameterMap = map;
		}
		return requestParameterMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<String> getRequestParameterNames() {
		return CollectionUtils.toIterator(portletRequest.getParameterNames());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String[]> getRequestParameterValuesMap() {
		if (requestParameterValuesMap == null) {
			RequestParameterMap map = new RequestParameterMap(portletRequest);
			map.setUseArrayForMultiValueAttributes(Boolean.TRUE);
			requestParameterValuesMap = map;
		}
		return requestParameterValuesMap;
	}

	@Override
	public String getRequestPathInfo() {
		return null;
	}

	@Override
	public String getRequestServletPath() {
		//
		// Return "" instead of null in order to prevent NullPointerException in Apache MyFaces 1.2 when it tries to
		// determine the servlet mappings in DefaultViewHandlerSupport.calculateFacesServletMapping(..).
		// Note that the FacesServlet mapping in Web Flow is not relevant so this should be ok.
		//
		// Alternatively this method could be implemented to provide an actual servlet path derived from the
		// viewId when that becomes available during rendering as the MyFaces Portlet Bridge does.
		//
		return (JsfRuntimeInformation.isMyFacesPresent()) ? "" : null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		Assert.notNull(path);
		return portletContext.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		Assert.notNull(path);
		return portletContext.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		Assert.notNull(path);
		return portletContext.getResourcePaths(path);
	}

	@Override
	public Object getResponse() {
		return portletResponse;
	}

	@Override
	public String getResponseContentType() {
		return null;
	}

	@Override
	public Object getSession(boolean create) {
		return portletRequest.getPortletSession(create);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSessionMap() {
		if (sessionMap == null) {
			sessionMap = new LocalAttributeMap(new PortletSessionMap(portletRequest));
		}
		return sessionMap.asMap();
	}

	@Override
	public Principal getUserPrincipal() {
		return portletRequest.getUserPrincipal();
	}

	@Override
	public boolean isUserInRole(String role) {
		Assert.notNull(role);
		return portletRequest.isUserInRole(role);
	}

	@Override
	public void log(String message) {
		Assert.notNull(message);
		portletContext.log(message);
	}

	@Override
	public void log(String message, Throwable exception) {
		Assert.notNull(message);
		Assert.notNull(exception);
		portletContext.log(message, exception);
	}

	@Override
	public void redirect(String url) throws IOException {
		if (actionRequest instanceof ActionResponse) {
			((ActionResponse) portletResponse).sendRedirect(url);
		} else {
			throw new IllegalArgumentException("Only ActionResponse supported");
		}
	}

	public void release() {
		portletContext = null;
		portletRequest = null;
		portletResponse = null;
		applicationMap = null;
		sessionMap = null;
		requestMap = null;
		requestParameterMap = null;
		requestParameterValuesMap = null;
		requestHeaderMap = null;
		requestHeaderValuesMap = null;
		initParameterMap = null;
		actionRequest = null;
	}

	@Override
	public void setRequest(Object request) {
		this.portletRequest = (PortletRequest) request;
		this.actionRequest = (portletRequest instanceof ActionRequest) ? (ActionRequest) request : null;
	}

	public void setRequestCharacterEncoding(String encoding) throws java.io.UnsupportedEncodingException {
		Assert.notNull(actionRequest, "The request be an action request.");
		actionRequest.setCharacterEncoding(encoding);
	}

	@Override
	public String getRequestCharacterEncoding() {
		Assert.notNull(actionRequest, "The request be an action request.");
		return actionRequest.getCharacterEncoding();
	}

	@Override
	public String getResponseCharacterEncoding() {
		return null;
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		// no-op
	}

	@Override
	public void setResponse(Object response) {
		this.portletResponse = (PortletResponse) response;
	}

}
