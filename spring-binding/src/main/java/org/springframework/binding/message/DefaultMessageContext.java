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
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;

/**
 * Default message context implementation.
 * 
 * @author Keith Donald
 */
class DefaultMessageContext implements StateManageableMessageContext {

	private static final Log logger = LogFactory.getLog(DefaultMessageContext.class);

	private MessageSource messageSource;

	private Map sourceMessages = new CachingMapDecorator(new LinkedHashMap()) {
		protected Object create(Object source) {
			return new ArrayList();
		}
	};

	public DefaultMessageContext(MessageSource messageSource) {
		init(messageSource);
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
		Assert.notNull(criteria, "The message criteria is required");
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

	// internal helpers

	private void init(MessageSource messageSource) {
		this.messageSource = messageSource;
		// create the 'null' source message list eagerly to ensure global messages are indexed first
		this.sourceMessages.get(null);
	}

	public String toString() {
		return new ToStringCreator(this).append("sourceMessages", sourceMessages).toString();
	}

}