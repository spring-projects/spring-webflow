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
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;

class ExternalContextWrapper extends ExternalContext {

	protected ExternalContext delegate;

	public ExternalContextWrapper(ExternalContext delegate) {
		this.delegate = delegate;
	}

	public void dispatch(String path) throws IOException {
		delegate.dispatch(path);
	}

	public String encodeActionURL(String url) {
		return delegate.encodeActionURL(url);
	}

	public String encodeNamespace(String name) {
		return delegate.encodeNamespace(name);
	}

	public String encodeResourceURL(String url) {
		return delegate.encodeResourceURL(url);
	}

	public Map<String, Object> getApplicationMap() {
		return delegate.getApplicationMap();
	}

	public String getAuthType() {
		return delegate.getAuthType();
	}

	public Object getContext() {
		return delegate.getContext();
	}

	public String getInitParameter(String name) {
		return delegate.getInitParameter(name);
	}

	@SuppressWarnings("rawtypes")
	public Map getInitParameterMap() {
		return delegate.getInitParameterMap();
	}

	public String getRemoteUser() {
		return delegate.getRemoteUser();
	}

	public Object getRequest() {
		return delegate.getRequest();
	}

	public String getRequestCharacterEncoding() {
		return delegate.getRequestCharacterEncoding();
	}

	public String getRequestContentType() {
		return delegate.getRequestContentType();
	}

	public String getRequestContextPath() {
		return delegate.getRequestContextPath();
	}

	public Map<String, Object> getRequestCookieMap() {
		return delegate.getRequestCookieMap();
	}

	public Map<String, String> getRequestHeaderMap() {
		return delegate.getRequestHeaderMap();
	}

	public Map<String, String[]> getRequestHeaderValuesMap() {
		return delegate.getRequestHeaderValuesMap();
	}

	public Locale getRequestLocale() {
		return delegate.getRequestLocale();
	}

	public Iterator<Locale> getRequestLocales() {
		return delegate.getRequestLocales();
	}

	public Map<String, Object> getRequestMap() {
		return delegate.getRequestMap();
	}

	public Map<String, String> getRequestParameterMap() {
		return delegate.getRequestParameterMap();
	}

	public Iterator<String> getRequestParameterNames() {
		return delegate.getRequestParameterNames();
	}

	public Map<String, String[]> getRequestParameterValuesMap() {
		return delegate.getRequestParameterValuesMap();
	}

	public String getRequestPathInfo() {
		return delegate.getRequestPathInfo();
	}

	public String getRequestServletPath() {
		return delegate.getRequestServletPath();
	}

	public URL getResource(String path) throws MalformedURLException {
		return delegate.getResource(path);
	}

	public InputStream getResourceAsStream(String path) {
		return delegate.getResourceAsStream(path);
	}

	public Set<String> getResourcePaths(String path) {
		return delegate.getResourcePaths(path);
	}

	public Object getResponse() {
		return delegate.getResponse();
	}

	public String getResponseCharacterEncoding() {
		return delegate.getResponseCharacterEncoding();
	}

	public String getResponseContentType() {
		return delegate.getResponseContentType();
	}

	public Object getSession(boolean create) {
		return delegate.getSession(create);
	}

	public Map<String, Object> getSessionMap() {
		return delegate.getSessionMap();
	}

	public Principal getUserPrincipal() {
		return delegate.getUserPrincipal();
	}

	public boolean isUserInRole(String role) {
		return delegate.isUserInRole(role);
	}

	public void log(String message, Throwable exception) {
		delegate.log(message, exception);
	}

	public void log(String message) {
		delegate.log(message);
	}

	public void redirect(String url) throws IOException {
		delegate.redirect(url);
	}

	public void setRequest(Object request) {
		delegate.setRequest(request);
	}

	public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		delegate.setRequestCharacterEncoding(encoding);
	}

	public void setResponse(Object response) {
		delegate.setResponse(response);
	}

	public void setResponseCharacterEncoding(String encoding) {
		delegate.setResponseCharacterEncoding(encoding);
	}

}
