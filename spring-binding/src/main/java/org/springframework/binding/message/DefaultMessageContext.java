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
package org.springframework.binding.message;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.CachingMapDecorator;

/**
 * The default message context implementation. Uses a {@link MessageSource} to resolve messages that are added by
 * callers.
 * 
 * @author Keith Donald
 */
public class DefaultMessageContext implements StateManageableMessageContext {

	private static final Log logger = LogFactory.getLog(DefaultMessageContext.class);

	private MessageSource messageSource;

	private Map sourceMessages = new CachingMapDecorator(new LinkedHashMap()) {
		protected Object create(Object source) {
			return new ArrayList();
		}
	};

	/**
	 * Creates a new default message context. Defaults to a message source that simply resolves default text and cannot
	 * resolve localized message codes.
	 */
	public DefaultMessageContext() {
		init(null);
	}

	/**
	 * Creates a new default message context.
	 * @param messageSource the message source to resolve messages added to this context
	 */
	public DefaultMessageContext(MessageSource messageSource) {
		init(messageSource);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	// implementing message context

	public Message[] getAllMessages() {
		List messages = new ArrayList();
		for (Iterator it = sourceMessages.values().iterator(); it.hasNext();) {
			messages.addAll((List) it.next());
		}
		return (Message[]) messages.toArray(new Message[messages.size()]);
	}

	public Message[] getMessagesBySource(Object source) {
		List messages = (List) sourceMessages.get(source);
		return (Message[]) messages.toArray(new Message[messages.size()]);
	}

	public Message[] getMessagesByCriteria(MessageCriteria criteria) {
		List messages = new ArrayList();
		Iterator it = sourceMessages.values().iterator();
		while (it.hasNext()) {
			List sourceMessages = (List) it.next();
			for (Iterator it2 = sourceMessages.iterator(); it2.hasNext();) {
				Message message = (Message) it2.next();
				if (criteria.test(message)) {
					messages.add(message);
				}
			}
		}
		return (Message[]) messages.toArray(new Message[messages.size()]);
	}

	public boolean hasErrorMessages() {
		Iterator it = sourceMessages.values().iterator();
		while (it.hasNext()) {
			List sourceMessages = (List) it.next();
			for (Iterator it2 = sourceMessages.iterator(); it2.hasNext();) {
				Message message = (Message) it2.next();
				if (message.getSeverity() == Severity.ERROR) {
					return true;
				}
			}
		}
		return false;
	}

	public void addMessage(MessageResolver messageResolver) {
		Locale currentLocale = LocaleContextHolder.getLocale();
		if (logger.isDebugEnabled()) {
			logger.debug("Resolving message using " + messageResolver);
		}
		Message message = messageResolver.resolveMessage(messageSource, currentLocale);
		List messages = (List) sourceMessages.get(message.getSource());
		if (logger.isDebugEnabled()) {
			logger.debug("Adding resolved message " + message);
		}
		messages.add(message);
	}

	public void clearMessages() {
		sourceMessages.clear();
	}

	// implementing state manageable message context

	public Serializable createMessagesMemento() {
		return new HashMap(sourceMessages);
	}

	public void restoreMessages(Serializable messagesMemento) {
		sourceMessages.putAll((Map) messagesMemento);
	}

	public void setMessageSource(MessageSource messageSource) {
		if (messageSource == null) {
			messageSource = new DefaultTextFallbackMessageSource();
		}
		this.messageSource = messageSource;
	}

	// internal helpers

	private void init(MessageSource messageSource) {
		setMessageSource(messageSource);
		// create the 'null' source message list eagerly to ensure global messages are indexed first
		this.sourceMessages.get(null);
	}

	public String toString() {
		return new ToStringCreator(this).append("sourceMessages", sourceMessages).toString();
	}

	private static class DefaultTextFallbackMessageSource extends AbstractMessageSource {
		protected MessageFormat resolveCode(String code, Locale locale) {
			return null;
		}
	}
}