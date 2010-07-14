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
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;

import org.springframework.webflow.execution.RequestContext;

/**
 * Extends FlowFacesContext in order to provide JSF 2 delegation method. This is necessary because some of the methods
 * use JSF 2 specific types as input or output parameters.
 * 
 * @author Rossen Stoyanchev
 */
public class Jsf2FlowFacesContext extends FlowFacesContext {

	public Jsf2FlowFacesContext(RequestContext context, FacesContext delegate) {
		super(context, delegate);
	}

	public ExternalContext getExternalContext() {
		return new Jsf2FlowExternalContext(getDelegate().getExternalContext());
	}

	// --------------- JSF 2.0 Pass-through delegate methods ------------------//

	public Map<Object, Object> getAttributes() {
		return getDelegate().getAttributes();
	}

	public PartialViewContext getPartialViewContext() {
		return getDelegate().getPartialViewContext();
	}

	public List<FacesMessage> getMessageList() {
		return getDelegate().getMessageList();
	}

	public List<FacesMessage> getMessageList(String clientId) {
		return getDelegate().getMessageList(clientId);
	}

	public boolean isPostback() {
		return getDelegate().isPostback();
	}

	public PhaseId getCurrentPhaseId() {
		return getDelegate().getCurrentPhaseId();
	}

	public void setCurrentPhaseId(PhaseId currentPhaseId) {
		getDelegate().setCurrentPhaseId(currentPhaseId);
	}

	public ExceptionHandler getExceptionHandler() {
		return getDelegate().getExceptionHandler();
	}

	public boolean isProcessingEvents() {
		return getDelegate().isProcessingEvents();
	}

	public boolean isProjectStage(ProjectStage stage) {
		return getDelegate().isProjectStage(stage);
	}

	public boolean isValidationFailed() {
		return getDelegate().isValidationFailed();
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		getDelegate().setExceptionHandler(exceptionHandler);
	}

	public void setProcessingEvents(boolean processingEvents) {
		getDelegate().setProcessingEvents(processingEvents);
	}

	public void validationFailed() {
		getDelegate().validationFailed();
	}

	protected class Jsf2FlowExternalContext extends FlowExternalContext {

		public Jsf2FlowExternalContext(ExternalContext delegate) {
			super(delegate);
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
}
