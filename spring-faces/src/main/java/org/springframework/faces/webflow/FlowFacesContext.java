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
package org.springframework.faces.webflow;

import java.util.Iterator;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.message.Messages;
import org.springframework.binding.message.Severity;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom {@link FacesContext} implementation that delegates all standard FacesContext messaging functionality to a
 * Spring {@link MessageSource} made accessible as part of the current Web Flow request.
 * 
 * @author Jeremy Grelle
 */
public class FlowFacesContext extends FacesContext {

	/**
	 * The Web Flow request context.
	 */
	private RequestContext context;

	/**
	 * The base FacesContext delegate
	 */
	private FacesContext delegate;

	public FlowFacesContext(RequestContext context, FacesContext delegate) {
		this.context = context;
		this.delegate = delegate;
		setCurrentInstance(this);
	}

	/**
	 * Translates a FacesMessage to an SWF Message and adds it to the current MessageContext
	 */
	public void addMessage(String clientId, FacesMessage message) {
		MessageResolver messageResolver;
		StringBuffer msgText = new StringBuffer();
		if (StringUtils.hasText(message.getSummary())) {
			msgText.append(message.getSummary());
		}
		if (message.getSeverity() == FacesMessage.SEVERITY_INFO) {
			messageResolver = Messages.text(msgText.toString(), Severity.INFO);
		} else if (message.getSeverity() == FacesMessage.SEVERITY_WARN) {
			messageResolver = Messages.text(msgText.toString(), Severity.WARNING);
		} else {
			messageResolver = Messages.text(msgText.toString(), Severity.ERROR);
		}
		context.getMessageContext().addMessage(messageResolver);
	}

	/**
	 * Returns an Iterator for all component clientId's for which messages have been added.
	 */
	public Iterator<String> getClientIdsWithMessages() {
		return new ClientIdIterator();
	}

	/**
	 * Return the maximum severity level recorded on any FacesMessages that has been queued, whether or not they are
	 * associated with any specific UIComponent. If no such messages have been queued, return null.
	 */
	public FacesMessage.Severity getMaximumSeverity() {
		if (context.getMessageContext().getMessages().length == 0) {
			return null;
		}
		FacesMessage.Severity max = FacesMessage.SEVERITY_INFO;
		Iterator<FacesMessage> i = getMessages();
		while (i.hasNext()) {
			FacesMessage message = i.next();
			if (message.getSeverity().getOrdinal() > max.getOrdinal()) {
				max = message.getSeverity();
			}
			if (max.getOrdinal() == FacesMessage.SEVERITY_ERROR.getOrdinal())
				break;
		}
		return max;
	}

	/**
	 * Returns an Iterator for all Messages in the current MessageContext that does translation to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages() {
		return new FacesMessageIterator();
	}

	/**
	 * Returns an Iterator for all Messages with the given clientId in the current MessageContext that does translation
	 * to FacesMessages.
	 */
	public Iterator<FacesMessage> getMessages(String clientId) {
		return new FacesMessageIterator(clientId);
	}

	public boolean getRenderResponse() {
		return delegate.getRenderResponse();
	}

	public boolean getResponseComplete() {
		return delegate.getResponseComplete();
	}

	public void renderResponse() {
		delegate.renderResponse();
	}

	public void responseComplete() {
		delegate.responseComplete();
	}

	// ------------------ Pass-through delegate methods ----------------------//

	public Application getApplication() {
		return delegate.getApplication();
	}

	public ELContext getELContext() {
		return delegate.getELContext();
	}

	public ExternalContext getExternalContext() {
		return delegate.getExternalContext();
	}

	public RenderKit getRenderKit() {
		return delegate.getRenderKit();
	}

	public ResponseStream getResponseStream() {
		return delegate.getResponseStream();
	}

	public ResponseWriter getResponseWriter() {
		return delegate.getResponseWriter();
	}

	public UIViewRoot getViewRoot() {
		return delegate.getViewRoot();
	}

	public void release() {
		delegate.release();
		setCurrentInstance(null);
	}

	public void setResponseStream(ResponseStream responseStream) {
		delegate.setResponseStream(responseStream);
	}

	public void setResponseWriter(ResponseWriter responseWriter) {
		delegate.setResponseWriter(responseWriter);
	}

	public void setViewRoot(UIViewRoot root) {
		delegate.setViewRoot(root);
	}

	protected FacesContext getDelegate() {
		return delegate;
	}

	private class FacesMessageIterator implements Iterator<FacesMessage> {

		private Message[] messages;

		private int currentIndex = -1;

		protected FacesMessageIterator() {
			this.messages = RequestContextHolder.getRequestContext().getMessageContext().getMessages();
		}

		protected FacesMessageIterator(String clientId) {
			this.messages = RequestContextHolder.getRequestContext().getMessageContext().getMessages(clientId);
		}

		public boolean hasNext() {
			return messages.length > currentIndex + 1;
		}

		public FacesMessage next() {
			currentIndex++;
			Message nextMessage = messages[currentIndex];
			FacesMessage facesMessage;
			if (nextMessage.getSeverity() == Severity.INFO) {
				facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, nextMessage.getText(), nextMessage
						.getText());
			} else if (nextMessage.getSeverity() == Severity.WARNING) {
				facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, nextMessage.getText(), nextMessage
						.getText());
			} else {
				facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, nextMessage.getText(), nextMessage
						.getText());
			}
			return facesMessage;
		}

		public void remove() {
			throw new UnsupportedOperationException("Messages cannot be removed through this iterator.");
		}

	}

	private class ClientIdIterator implements Iterator<String> {

		private Message[] messages;

		int currentIndex = -1;

		protected ClientIdIterator() {
			this.messages = RequestContextHolder.getRequestContext().getMessageContext().getMessages();
		}

		public boolean hasNext() {
			while (messages.length > currentIndex + 1) {
				Message next = messages[currentIndex + 1];
				if (next.getSource() != null && !"".equals(next.getSource())) {
					return true;
				}
				currentIndex++;
			}
			return false;
		}

		public String next() {
			Message next = messages[++currentIndex];
			while (next.getSource() == null || "".equals(next.getSource())) {
				next = messages[++currentIndex];
			}
			return next.getSource().toString();
		}

		public void remove() {
			throw new UnsupportedOperationException("Messages cannot be removed through this iterator.");
		}

	}

}
