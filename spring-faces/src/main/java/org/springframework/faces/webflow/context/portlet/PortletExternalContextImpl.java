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

	private MapAdaptable<String, Object> sessionMap;

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
		Assert.isTrue(!this.isActionRequest);
		PortletRequestDispatcher requestDispatcher = this.portletContext.getRequestDispatcher(path);
		try {
			requestDispatcher.include((RenderRequest) this.portletRequest, (RenderResponse) this.portletResponse);
		} catch (PortletException exception) {
			if (exception.getMessage() != null) {
				throw new FacesException(exception.getMessage(), exception);
			}
			throw new FacesException(exception);
		}
	}

	public String encodeActionURL(String url) {
		Assert.notNull(url);
		return this.portletResponse.encodeURL(url);
	}

	public String encodeNamespace(String name) {
		Assert.isTrue(!this.isActionRequest);
		return name + ((RenderResponse) this.portletResponse).getNamespace();
	}

	@Override
	public String encodeResourceURL(String url) {
		Assert.notNull(url);
		return this.portletResponse.encodeURL(url);
	}

	@Override
	public Map<String, Object> getApplicationMap() {
		if (this.applicationMap == null) {
			this.applicationMap = new PortletContextMap(this.portletContext);
		}
		return this.applicationMap;
	}

	@Override
	public String getAuthType() {
		return this.portletRequest.getAuthType();
	}

	@Override
	public Object getContext() {
		return this.portletContext;
	}

	@Override
	public String getInitParameter(String name) {
		return this.portletContext.getInitParameter(name);
	}

	@Override
	public Map<String, String> getInitParameterMap() {
		if (this.initParameterMap == null) {
			this.initParameterMap = new InitParameterMap(this.portletContext);
		}
		return this.initParameterMap;
	}

	@Override
	public String getRemoteUser() {
		return this.portletRequest.getRemoteUser();
	}

	@Override
	public Object getRequest() {
		return this.portletRequest;
	}

	@Override
	public String getRequestContentType() {
		return null;
	}

	@Override
	public String getRequestContextPath() {
		return this.portletRequest.getContextPath();
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		if (this.requestHeaderMap == null) {
			this.requestHeaderMap = new SingleValueRequestPropertyMap(this.portletRequest);
		}
		return this.requestHeaderMap;
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		if (this.requestHeaderValuesMap == null) {
			this.requestHeaderValuesMap = new MultiValueRequestPropertyMap(this.portletRequest);
		}
		return this.requestHeaderValuesMap;
	}

	@Override
	public Locale getRequestLocale() {
		return this.portletRequest.getLocale();
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		return CollectionUtils.toIterator(this.portletRequest.getLocales());
	}

	@Override
	public Map<String, Object> getRequestMap() {
		if (this.requestMap == null) {
			this.requestMap = new PortletRequestMap(this.portletRequest);
		}
		return this.requestMap;
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		if (this.requestParameterMap == null) {
			this.requestParameterMap = new SingleValueRequestParameterMap(this.portletRequest);
		}
		return this.requestParameterMap;
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		return CollectionUtils.toIterator(this.portletRequest.getParameterNames());
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		if (this.requestParameterValuesMap == null) {
			this.requestParameterValuesMap = new MultiValueRequestParameterMap(this.portletRequest);
		}
		return this.requestParameterValuesMap;
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
		return this.portletContext.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		Assert.notNull(path);
		return this.portletContext.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		Assert.notNull(path);
		return this.portletContext.getResourcePaths(path);
	}

	@Override
	public Object getResponse() {
		return this.portletResponse;
	}

	@Override
	public String getResponseContentType() {
		return null;
	}

	@Override
	public Object getSession(boolean create) {
		return this.portletRequest.getPortletSession(create);
	}

	@Override
	public Map<String, Object> getSessionMap() {
		if (this.sessionMap == null) {
			this.sessionMap = new LocalAttributeMap<Object>(new PortletSessionMap(this.portletRequest));
		}
		return this.sessionMap.asMap();
	}

	@Override
	public Principal getUserPrincipal() {
		return this.portletRequest.getUserPrincipal();
	}

	@Override
	public boolean isUserInRole(String role) {
		Assert.notNull(role);
		return this.portletRequest.isUserInRole(role);
	}

	@Override
	public void log(String message) {
		Assert.notNull(message);
		this.portletContext.log(message);
	}

	@Override
	public void log(String message, Throwable exception) {
		Assert.notNull(message);
		Assert.notNull(exception);
		this.portletContext.log(message, exception);
	}

	@Override
	public void redirect(String url) throws IOException {
		if (this.actionRequest instanceof ActionResponse) {
			((ActionResponse) this.portletResponse).sendRedirect(url);
		} else {
			throw new IllegalArgumentException("Only ActionResponse supported");
		}
	}

	public void release() {
		this.portletContext = null;
		this.portletRequest = null;
		this.portletResponse = null;
		this.applicationMap = null;
		this.sessionMap = null;
		this.requestMap = null;
		this.requestParameterMap = null;
		this.requestParameterValuesMap = null;
		this.requestHeaderMap = null;
		this.requestHeaderValuesMap = null;
		this.initParameterMap = null;
		this.actionRequest = null;
	}

	@Override
	public void setRequest(Object request) {
		this.portletRequest = (PortletRequest) request;
		this.actionRequest = (this.portletRequest instanceof ActionRequest) ? (ActionRequest) request : null;
	}

	public void setRequestCharacterEncoding(String encoding) throws java.io.UnsupportedEncodingException {
		Assert.notNull(this.actionRequest, "The request be an action request.");
		this.actionRequest.setCharacterEncoding(encoding);
	}

	@Override
	public String getRequestCharacterEncoding() {
		Assert.notNull(this.actionRequest, "The request be an action request.");
		return this.actionRequest.getCharacterEncoding();
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
