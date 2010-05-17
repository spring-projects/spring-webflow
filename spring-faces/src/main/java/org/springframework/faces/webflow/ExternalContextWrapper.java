package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;

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

	public Map getApplicationMap() {
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

	public Map getRequestCookieMap() {
		return delegate.getRequestCookieMap();
	}

	public Map getRequestHeaderMap() {
		return delegate.getRequestHeaderMap();
	}

	public Map getRequestHeaderValuesMap() {
		return delegate.getRequestHeaderValuesMap();
	}

	public Locale getRequestLocale() {
		return delegate.getRequestLocale();
	}

	public Iterator getRequestLocales() {
		return delegate.getRequestLocales();
	}

	public Map getRequestMap() {
		return delegate.getRequestMap();
	}

	public Map getRequestParameterMap() {
		return delegate.getRequestParameterMap();
	}

	public Iterator getRequestParameterNames() {
		return delegate.getRequestParameterNames();
	}

	public Map getRequestParameterValuesMap() {
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

	public Set getResourcePaths(String path) {
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

	public Map getSessionMap() {
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

	// --------------- JSF 2.0 Pass-through delegate methods ------------------//

	public String getContextName() {
		return delegate.getContextName();
	}

	public void addResponseCookie(String name, String value, Map properties) {
		delegate.addResponseCookie(name, value, properties);
	}

	public Flash getFlash() {
		return delegate.getFlash();
	}

	public String getMimeType(String file) {
		return delegate.getMimeType(file);
	}

	public String getRequestScheme() {
		return delegate.getRequestScheme();
	}

	public String getRequestServerName() {
		return delegate.getRequestServerName();
	}

	public int getRequestServerPort() {
		return delegate.getRequestServerPort();
	}

	public String getRealPath(String path) {
		return delegate.getRealPath(path);
	}

	public int getRequestContentLength() {
		return delegate.getRequestContentLength();
	}

	public OutputStream getResponseOutputStream() throws IOException {
		return delegate.getResponseOutputStream();
	}

	public Writer getResponseOutputWriter() throws IOException {
		return delegate.getResponseOutputWriter();
	}

	public void setResponseContentType(String contentType) {
		delegate.setResponseContentType(contentType);
	}

	public void invalidateSession() {
		delegate.invalidateSession();
	}

	public void setResponseHeader(String name, String value) {
		delegate.setResponseHeader(name, value);
	}

	public void addResponseHeader(String name, String value) {
		delegate.addResponseHeader(name, value);
	}

	public void setResponseBufferSize(int size) {
		delegate.setResponseBufferSize(size);
	}

	public int getResponseBufferSize() {
		return delegate.getResponseBufferSize();
	}

	public boolean isResponseCommitted() {
		return delegate.isResponseCommitted();
	}

	public void responseReset() {
		delegate.responseReset();
	}

	public void responseSendError(int statusCode, String message) throws IOException {
		delegate.responseSendError(statusCode, message);
	}

	public void setResponseStatus(int statusCode) {
		delegate.setResponseStatus(statusCode);
	}

	public void responseFlushBuffer() throws IOException {
		delegate.responseFlushBuffer();
	}

	public void setResponseContentLength(int length) {
		delegate.setResponseContentLength(length);
	}

	public String encodeBookmarkableURL(String baseUrl, Map parameters) {
		return delegate.encodeBookmarkableURL(baseUrl, parameters);
	}

	public String encodeRedirectURL(String baseUrl, Map parameters) {
		return delegate.encodeRedirectURL(baseUrl, parameters);
	}

	public String encodePartialActionURL(String url) {
		return delegate.encodePartialActionURL(url);
	}

}
