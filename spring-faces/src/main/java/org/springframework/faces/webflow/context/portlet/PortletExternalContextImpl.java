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
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.http.Cookie;

import org.apache.myfaces.shared.context.flash.FlashImpl;
import org.springframework.binding.collection.MapAdaptable;
import org.springframework.faces.webflow.JsfRuntimeInformation;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.webflow.context.portlet.PortletContextMap;
import org.springframework.webflow.context.portlet.PortletRequestMap;
import org.springframework.webflow.context.portlet.PortletSessionMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;

import com.sun.faces.context.flash.ELFlash;

/**
 * An implementation of {@link ExternalContext} for use with Portlet requests.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 */
public class PortletExternalContextImpl extends ExternalContext {

	private Map<String, Object> applicationMap;

	private PortletContext portletContext;

	private PortletRequest request;

	private PortletResponse response;

	private boolean isActionRequest;

	private Map<String, String> initParameterMap;

	private Map<String, String> requestHeaderMap;

	private Map<String, String[]> requestHeaderValuesMap;

	private Map<String, Object> requestMap;

	private Map<String, String> requestParameterMap;

	private Map<String, String[]> requestParameterValuesMap;

	private MapAdaptable<String, Object> sessionMap;

	private Flash flash;

	public PortletExternalContextImpl(PortletContext portletContext, PortletRequest portletRequest,
			PortletResponse portletResponse) {
		this.portletContext = portletContext;
		this.request = portletRequest;
		this.response = portletResponse;
		if (portletRequest instanceof ActionRequest) {
			this.isActionRequest = true;
		}
	}

	public void release() {
		portletContext = null;
		request = null;
		response = null;
		applicationMap = null;
		sessionMap = null;
		requestMap = null;
		requestParameterMap = null;
		requestParameterValuesMap = null;
		requestHeaderMap = null;
		requestHeaderValuesMap = null;
		initParameterMap = null;
	}

	public Flash getFlash() {
		if(this.flash == null) {
			this.flash = createFlash();
		}
		return this.flash;
	}

	private Flash createFlash() {
		if (JsfRuntimeInformation.isMyFacesPresent()) {
			return new MyFacesFlashFactory().newFlash(this);
		} else {
			return new MojarraFlashFactory().newFlash(this);
		}
	}

	public void dispatch(String path) throws IOException {
		Assert.isTrue(!isActionRequest);
		PortletRequestDispatcher requestDispatcher = portletContext.getRequestDispatcher(path);
		try {
			requestDispatcher.include((RenderRequest) request, (RenderResponse) response);
		} catch (PortletException exception) {
			if (exception.getMessage() != null) {
				throw new FacesException(exception.getMessage(), exception);
			}
			throw new FacesException(exception);
		}
	}

	@Override
	public void redirect(String url) throws IOException {
		Assert.isInstanceOf(ActionResponse.class, response);
		((ActionResponse) response).sendRedirect(url);
	}

	public String encodeNamespace(String name) {
		Assert.isTrue(!isActionRequest);
		return name + ((RenderResponse) response).getNamespace();
	}

	public String encodeActionURL(String url) {
		Assert.notNull(url);
		return response.encodeURL(url);
	}

	@Override
	public String encodeResourceURL(String url) {
		Assert.notNull(url);
		return response.encodeURL(url);
	}

	public String encodePartialActionURL(String url) {
		Assert.notNull(url);
		return response.encodeURL(url);
	}

	public String encodeBookmarkableURL(String baseUrl, Map<String, List<String>> parameters) {
		return encodeUrl(baseUrl, parameters);
	}

	public String encodeRedirectURL(String baseUrl, Map<String, List<String>> parameters) {
		return response.encodeURL(encodeUrl(baseUrl, parameters));
	}

	private String encodeUrl(String baseUrl, Map<String, List<String>> parameters) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
		for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue().toArray());
		}
		return builder.buildAndExpand().toUriString();
	}


	@Override
	public Object getContext() {
		return portletContext;
	}

	public String getContextName() {
		return portletContext.getPortletContextName();
	}

	public String getMimeType(String file) {
		return portletContext.getMimeType(file);
	}

	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
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
	public Map<String, Object> getApplicationMap() {
		if (applicationMap == null) {
			applicationMap = new PortletContextMap(portletContext);
		}
		return applicationMap;
	}

	@Override
	public String getInitParameter(String name) {
		return portletContext.getInitParameter(name);
	}

	@Override
	public Map<String, String> getInitParameterMap() {
		if (initParameterMap == null) {
			initParameterMap = new InitParameterMap(portletContext);
		}
		return initParameterMap;
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
	public Object getRequest() {
		return request;
	}

	@Override
	public void setRequest(Object request) {
		this.request = (PortletRequest) request;
	}

	@Override
	public String getRequestContentType() {
		if (request instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) request;
			return clientDataRequest.getContentType();
		}
		return null;
	}

	@Override
	public String getRequestContextPath() {
		return request.getContextPath();
	}

	public String getRequestScheme() {
		return request.getScheme();
	}

	public String getRequestServerName() {
		return request.getServerName();
	}

	public int getRequestServerPort() {
		return request.getServerPort();
	}

	@Override
	public Locale getRequestLocale() {
		return request.getLocale();
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		return CollectionUtils.toIterator(request.getLocales());
	}

	@Override
	public String getRequestCharacterEncoding() {
		return getActionRequest().getCharacterEncoding();
	}

	public void setRequestCharacterEncoding(String encoding) throws java.io.UnsupportedEncodingException {
		getActionRequest().setCharacterEncoding(encoding);
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		if (requestHeaderMap == null) {
			requestHeaderMap = new SingleValueRequestPropertyMap(request);
		}
		return requestHeaderMap;
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		if (requestHeaderValuesMap == null) {
			requestHeaderValuesMap = new MultiValueRequestPropertyMap(request);
		}
		return requestHeaderValuesMap;
	}

	@Override
	public Map<String, Object> getRequestMap() {
		if (requestMap == null) {
			requestMap = new PortletRequestMap(request);
		}
		return requestMap;
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		if (requestParameterMap == null) {
			requestParameterMap = new SingleValueRequestParameterMap(request);
		}
		return requestParameterMap;
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		return CollectionUtils.toIterator(request.getParameterNames());
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		if (requestParameterValuesMap == null) {
			requestParameterValuesMap = new MultiValueRequestParameterMap(request);
		}
		return requestParameterValuesMap;
	}

	@Override
	public String getRequestPathInfo() {
		return null;
	}

	@Override
	public String getRequestServletPath() {
		// Return "" instead of null in order to prevent NullPointerException in Apache MyFaces 1.2 when it tries to
		// determine the servlet mappings in DefaultViewHandlerSupport.calculateFacesServletMapping(..).
		// Note that the FacesServlet mapping in Web Flow is not relevant so this should be ok.
		//
		// Alternatively this method could be implemented to provide an actual servlet path derived from the
		// viewId when that becomes available during rendering as the MyFaces Portlet Bridge does.
		//
		return (JsfRuntimeInformation.isMyFacesPresent()) ? "" : null;
	}

	public int getRequestContentLength() {
		Assert.isInstanceOf(ClientDataRequest.class, request);
		return ((ClientDataRequest) request).getContentLength();
	}

	private ActionRequest getActionRequest() {
		Assert.isInstanceOf(ActionRequest.class, request);
		return (ActionRequest) request;
	}

	@Override
	public String getAuthType() {
		return request.getAuthType();
	}

	@Override
	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	@Override
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	@Override
	public boolean isUserInRole(String role) {
		Assert.notNull(role);
		return request.isUserInRole(role);
	}

	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public Object getResponse() {
		return response;
	}

	@Override
	public void setResponse(Object response) {
		this.response = (PortletResponse) response;
	}

	@Override
	public String getResponseContentType() {
		return getMimeResponse().getContentType();
	}

	@Override
	public String getResponseCharacterEncoding() {
		return getMimeResponse().getCharacterEncoding();
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		// no-op
	}

	public OutputStream getResponseOutputStream() throws IOException {
		return getMimeResponse().getPortletOutputStream();
	}

	public Writer getResponseOutputWriter() throws IOException {
		return getMimeResponse().getWriter();
	}

	public void addResponseCookie(String name, String value, Map<String, Object> properties) {
		Cookie cookie = new Cookie(name, value);
		setCookieProperties(cookie, properties);
		response.addProperty(cookie);
	}

	private void setCookieProperties(Cookie cookie, Map<String, Object> properties) {
		if (properties != null) {
			for (Map.Entry<String, Object> entry : properties.entrySet()) {
				setCookieProperty(cookie, entry.getKey(), entry.getValue());
			}
		}
	}

	private void setCookieProperty(Cookie cookie, String property, Object value) {
		if ("domain".equalsIgnoreCase(property)) {
			cookie.setDomain((String) value);
			return;
		}
		if ("maxAge".equalsIgnoreCase(property)) {
			cookie.setMaxAge((Integer) value);
			return;
		}
		if ("path".equalsIgnoreCase(property)) {
			cookie.setPath((String) value);
			return;
		}
		if ("secure".equalsIgnoreCase(property)) {
			cookie.setSecure((Boolean) value);
			return;
		}
		throw new IllegalStateException("Unknown cookie property " + property);
	}

	public void addResponseHeader(String name, String value) {
		response.addProperty(name, value);
	}

	public void responseFlushBuffer() throws IOException {
		getMimeResponse().flushBuffer();
	}

	public void responseReset() {
		MimeResponse mimeResponse = getMimeResponse(false);
		if (mimeResponse != null) {
			mimeResponse.reset();
		}
	}

	public void responseSendError(int statusCode, String message) throws IOException {
		throw new IOException(statusCode + ": " + message);
	}

	public void setResponseBufferSize(int size) {
		getMimeResponse().setBufferSize(size);
	}

	public void setResponseContentLength(int length) {
		if (portletContext instanceof ResourceResponse) {
			((ResourceResponse) portletContext).setContentLength(length);
		}
	}

	public void setResponseContentType(String contentType) {
		MimeResponse mimeResponse = getMimeResponse(false);
		if (mimeResponse != null) {
			mimeResponse.setContentType(contentType);
		}
	}

	public void setResponseHeader(String name, String value) {
		response.setProperty(name, value);
	}

	public void setResponseStatus(int statusCode) {
		response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(statusCode));
		PortletResponseUtils.setStatusCodeForPluto(response, statusCode);
	}

	public boolean isResponseCommitted() {
		MimeResponse mimeResponse = getMimeResponse(false);
		return ((mimeResponse != null) ? mimeResponse.isCommitted() : false);
	}

	public int getResponseBufferSize() {
		return getMimeResponse().getBufferSize();

	}

	private MimeResponse getMimeResponse() {
		return getMimeResponse(true);
	}

	private MimeResponse getMimeResponse(boolean required) {
		if (response instanceof MimeResponse) {
			return (MimeResponse) response;
		}
		if (!required) {
			return null;
		}
		throw new IllegalStateException("Portlet response is not a MimeResponse");
	}

	@Override
	public Object getSession(boolean create) {
		return request.getPortletSession(create);
	}

	@Override
	public Map<String, Object> getSessionMap() {
		if (sessionMap == null) {
			sessionMap = new LocalAttributeMap<Object>(new PortletSessionMap(request));
		}
		return sessionMap.asMap();
	}

	public int getSessionMaxInactiveInterval() {
		return request.getPortletSession().getMaxInactiveInterval();
	}

	public void invalidateSession() {
		PortletSession portletSession = request.getPortletSession(false);
		if (portletSession != null) {
			portletSession.invalidate();
		}
	}

	public void setSessionMaxInactiveInterval(int interval) {
		request.getPortletSession().setMaxInactiveInterval(interval);
	}

	private static class MojarraFlashFactory {
		public Flash newFlash(ExternalContext context) {
			return ELFlash.getFlash(context, true);
		}
	}

	private static class MyFacesFlashFactory {
		public Flash newFlash(ExternalContext context) {
			return FlashImpl.getCurrentInstance(context);
		}
	}
}
