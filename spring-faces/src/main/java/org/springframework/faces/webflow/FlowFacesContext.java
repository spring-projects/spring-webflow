/*
 * Copyright 2004-2008 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.message.Severity;
import org.springframework.context.MessageSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Custom {@link FacesContext} implementation that delegates all standard FacesContext messaging functionality to a
 * Spring {@link MessageSource} made accessible as part of the current Web Flow request. Additionally, it manages the
 * {@code responseComplete} and {@code renderResponse} flags in flash scope so that the execution of the JSF
 * {@link Lifecycle} may span multiple requests in the case of the POST+REDIRECT+GET pattern being enabled.
 * 
 * @author Jeremy Grelle
 */
public class FlowFacesContext extends FacesContext {

	/**
	 * The key for storing the responseComplete flag
	 */
	static final String RESPONSE_COMPLETE_KEY = "flowResponseComplete";

	/**
	 * The key for storing the renderResponse flag
	 */
	static final String RENDER_RESPONSE_KEY = "flowRenderResponse";

	/**
	 * Key for identifying summary messages
	 */
	static final String SUMMARY_MESSAGE_KEY = "_summary";

	/**
	 * Key for identifying detail messages
	 */
	static final String DETAIL_MESSAGE_KEY = "_detail";

	/**
	 * The key for storing the renderResponse flag
	 */
	private RequestContext context;

	/**
	 * The base FacesContext delegate
	 */
	private FacesContext delegate;

	public static FlowFacesContext newInstance(RequestContext context, Lifecycle lifecycle) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context.getExternalContext()
				.getNativeContext(), context.getExternalContext().getNativeRequest(), context.getExternalContext()
				.getNativeResponse(), lifecycle);
		return new FlowFacesContext(context, defaultFacesContext);
	}

	public FlowFacesContext(RequestContext context, FacesContext delegate) {
		this.context = context;
		this.delegate = delegate;
		setCurrentInstance(this);
	}

	/**
	 * Translates a FacesMessage to an SWF Message and adds it to the current MessageContext
	 */
	public void addMessage(String clientId, FacesMessage message) {
		String source = null;
		if (StringUtils.hasText(clientId)) {
			source = clientId;
		}

		StringBuffer summaryText = new StringBuffer();
		if (StringUtils.hasText(message.getSummary())) {
			summaryText.append(message.getSummary());
		}

		String summarySource = source + SUMMARY_MESSAGE_KEY;
		MessageResolver summaryResolver;
		if (message.getSeverity() == FacesMessage.SEVERITY_INFO) {
			summaryResolver = new MessageBuilder().source(summarySource).defaultText(summaryText.toString()).info()
					.build();
		} else if (message.getSeverity() == FacesMessage.SEVERITY_WARN) {
			summaryResolver = new MessageBuilder().source(summarySource).defaultText(summaryText.toString()).warning()
					.build();
		} else if (message.getSeverity() == FacesMessage.SEVERITY_ERROR) {
			summaryResolver = new MessageBuilder().source(summarySource).defaultText(summaryText.toString()).error()
					.build();
		} else {
			summaryResolver = new MessageBuilder().source(summarySource).defaultText(summaryText.toString()).fatal()
					.build();
		}
		context.getMessageContext().addMessage(summaryResolver);

		StringBuffer detailText = new StringBuffer();
		if (StringUtils.hasText(message.getDetail())) {
			detailText.append(message.getDetail());
		}
		String detailSource = source + DETAIL_MESSAGE_KEY;
		MessageResolver detailResolver;
		if (message.getSeverity() == FacesMessage.SEVERITY_INFO) {
			detailResolver = new MessageBuilder().source(detailSource).defaultText(detailText.toString()).info()
					.build();
		} else if (message.getSeverity() == FacesMessage.SEVERITY_WARN) {
			detailResolver = new MessageBuilder().source(detailSource).defaultText(detailText.toString()).warning()
					.build();
		} else if (message.getSeverity() == FacesMessage.SEVERITY_ERROR) {
			detailResolver = new MessageBuilder().source(detailSource).defaultText(detailText.toString()).error()
					.build();
		} else {
			detailResolver = new MessageBuilder().source(detailSource).defaultText(detailText.toString()).fatal()
					.build();
		}
		context.getMessageContext().addMessage(detailResolver);

	}

	/**
	 * Returns an Iterator for all component clientId's for which messages have been added.
	 */
	public Iterator getClientIdsWithMessages() {
		return new ClientIdIterator();
	}

	/**
	 * Return the maximum severity level recorded on any FacesMessages that has been queued, whether or not they are
	 * associated with any specific UIComponent. If no such messages have been queued, return null.
	 */
	public FacesMessage.Severity getMaximumSeverity() {
		if (context.getMessageContext().getAllMessages().length == 0) {
			return null;
		}
		FacesMessage.Severity max = FacesMessage.SEVERITY_INFO;
		Iterator i = getMessages();
		while (i.hasNext()) {
			FacesMessage message = (FacesMessage) i.next();
			if (message.getSeverity().getOrdinal() > max.getOrdinal()) {
				max = message.getSeverity();
			}
			if (max.getOrdinal() == FacesMessage.SEVERITY_FATAL.getOrdinal())
				break;
		}
		return max;
	}

	/**
	 * Returns an Iterator for all Messages in the current MessageContext that does translation to FacesMessages.
	 */
	public Iterator getMessages() {
		return new FacesMessageIterator();
	}

	/**
	 * Returns an Iterator for all Messages with the given clientId in the current MessageContext that does translation
	 * to FacesMessages.
	 */
	public Iterator getMessages(String clientId) {
		return new FacesMessageIterator(clientId);
	}

	public boolean getRenderResponse() {
		Boolean renderResponse = context.getFlashScope().getBoolean(RENDER_RESPONSE_KEY);
		if (renderResponse == null) {
			return false;
		}
		return renderResponse.booleanValue();
	}

	public boolean getResponseComplete() {
		Boolean responseComplete = context.getFlashScope().getBoolean(RESPONSE_COMPLETE_KEY);
		if (responseComplete == null) {
			return false;
		}
		return responseComplete.booleanValue();
	}

	public void renderResponse() {
		// stored in flash scope to survive a redirect when transitioning from one view to another
		context.getFlashScope().put(RENDER_RESPONSE_KEY, Boolean.TRUE);
	}

	public void responseComplete() {
		context.getFlashScope().put(RESPONSE_COMPLETE_KEY, Boolean.TRUE);
	}

	// ------------------ Pass-through delegate methods ----------------------//

	public Application getApplication() {
		return delegate.getApplication();
	}

	public ELContext getELContext() {
		Method delegateMethod = ClassUtils.getMethodIfAvailable(delegate.getClass(), "getELContext", null);
		if (delegateMethod != null) {
			try {
				return (ELContext) delegateMethod.invoke(delegate, null);
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public ExternalContext getExternalContext() {
		return new FlowExternalContext(delegate.getExternalContext());
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

	// ------------------ Private helper methods ----------------------//

	private FacesMessage toFacesMessage(Message summaryMessage, Message detailMessage) {
		if (summaryMessage.getSeverity() == Severity.INFO) {
			return new FacesMessage(FacesMessage.SEVERITY_INFO, summaryMessage.getText(), detailMessage.getText());
		} else if (summaryMessage.getSeverity() == Severity.WARNING) {
			return new FacesMessage(FacesMessage.SEVERITY_WARN, summaryMessage.getText(), detailMessage.getText());
		} else if (summaryMessage.getSeverity() == Severity.ERROR) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMessage.getText(), detailMessage.getText());
		} else {
			return new FacesMessage(FacesMessage.SEVERITY_FATAL, summaryMessage.getText(), detailMessage.getText());
		}
	}

	private class FacesMessageIterator implements Iterator {

		private Object[] messages;

		private int currentIndex = -1;

		protected FacesMessageIterator() {
			Message[] summaryMessages = context.getMessageContext().getMessagesByCriteria(new SummaryMessageCriteria());
			Message[] detailMessages = context.getMessageContext().getMessagesByCriteria(new DetailMessageCriteria());
			Message[] userMessages = context.getMessageContext().getMessagesByCriteria(new UserMessageCriteria());

			List translatedMessages = new ArrayList();
			for (int i = 0; i < summaryMessages.length; i++) {
				translatedMessages.add(toFacesMessage(summaryMessages[i], detailMessages[i]));
			}
			for (int z = 0; z < userMessages.length; z++) {
				translatedMessages.add(toFacesMessage(userMessages[z], userMessages[z]));
			}

			this.messages = translatedMessages.toArray();
		}

		protected FacesMessageIterator(String clientId) {
			Message[] summaryMessages = context.getMessageContext().getMessagesBySource(clientId + SUMMARY_MESSAGE_KEY);
			Message[] detailMessages = context.getMessageContext().getMessagesBySource(clientId + DETAIL_MESSAGE_KEY);
			Message[] userMessages = context.getMessageContext().getMessagesBySource(clientId);

			List translatedMessages = new ArrayList();
			for (int i = 0; i < summaryMessages.length; i++) {
				translatedMessages.add(toFacesMessage(summaryMessages[i], detailMessages[i]));
			}
			for (int z = 0; z < userMessages.length; z++) {
				translatedMessages.add(toFacesMessage(userMessages[z], userMessages[z]));
			}

			this.messages = translatedMessages.toArray();
		}

		public boolean hasNext() {
			return messages.length > currentIndex + 1;
		}

		public Object next() {
			currentIndex++;
			return messages[currentIndex];
		}

		public void remove() {
			throw new UnsupportedOperationException("Messages cannot be removed through this iterator.");
		}

	}

	private class ClientIdIterator implements Iterator {

		private Message[] messages;

		int currentIndex = -1;

		protected ClientIdIterator() {
			this.messages = context.getMessageContext().getMessagesByCriteria(new IdentifiedMessageCriteria());
		}

		public boolean hasNext() {
			return messages.length > currentIndex + 1;
		}

		public Object next() {
			Message next = messages[++currentIndex];
			if (next.getSource().toString().endsWith(SUMMARY_MESSAGE_KEY)) {
				return next.getSource().toString().replaceAll(SUMMARY_MESSAGE_KEY, "");
			} else {
				return next.getSource().toString();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException("Messages cannot be removed through this iterator.");
		}

	}

	private class FlowExternalContext extends ExternalContextWrapper {

		private static final String CUSTOM_RESPONSE = "customResponse";

		public FlowExternalContext(ExternalContext delegate) {
			super(delegate);
		}

		public Object getResponse() {
			if (context.getRequestScope().contains(CUSTOM_RESPONSE)) {
				return context.getRequestScope().get(CUSTOM_RESPONSE);
			}
			return delegate.getResponse();
		}

		/**
		 * Store the native response object to be used for the duration of the Faces Request
		 */
		public void setResponse(Object response) {
			context.getRequestScope().put(CUSTOM_RESPONSE, response);
			delegate.setResponse(response);
		}

	}

	private class SummaryMessageCriteria implements MessageCriteria {

		public boolean test(Message message) {
			if (message.getSource() == null) {
				return false;
			}
			return message.getSource().toString().endsWith(SUMMARY_MESSAGE_KEY);
		}
	}

	private class DetailMessageCriteria implements MessageCriteria {

		public boolean test(Message message) {
			if (message.getSource() == null) {
				return false;
			}
			return message.getSource().toString().endsWith(DETAIL_MESSAGE_KEY);
		}
	}

	private class UserMessageCriteria implements MessageCriteria {

		public boolean test(Message message) {
			if (message.getSource() == null) {
				return true;
			}
			return !message.getSource().toString().endsWith(SUMMARY_MESSAGE_KEY)
					&& !message.getSource().toString().endsWith(DETAIL_MESSAGE_KEY);
		}
	}

	private class IdentifiedMessageCriteria implements MessageCriteria {

		String nullSummaryId = null + SUMMARY_MESSAGE_KEY;

		public boolean test(Message message) {
			if (message.getSource() == null || message.getSource().equals("")
					|| message.getSource().equals(nullSummaryId)
					|| message.getSource().toString().endsWith(DETAIL_MESSAGE_KEY)) {
				return false;
			}
			return true;
		}
	}
}
