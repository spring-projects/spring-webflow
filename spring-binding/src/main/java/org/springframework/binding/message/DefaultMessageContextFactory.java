package org.springframework.binding.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;

/**
 * Default message context factory that simply stores messages indexed in a map by their source. Suitable for use in
 * most Spring applications that use Spring message sources for message resource bundles. Holds a reference to a Spring
 * message resource bundle for performing message text resolution.
 * 
 * @author Keith Donald
 */
public class DefaultMessageContextFactory implements MessageContextFactory {

	private MessageSource messageSource;

	/**
	 * Create a new message context factory.
	 * @param messageSource
	 */
	public DefaultMessageContextFactory(MessageSource messageSource) {
		Assert.notNull(messageSource, "The message source is required");
		this.messageSource = messageSource;
	}

	public StateManageableMessageContext createMessageContext() {
		return new MessageContextImpl(messageSource);
	}

	private static class MessageContextImpl implements StateManageableMessageContext {

		private MessageSource messageSource;

		private Map objectMessages = new CachingMapDecorator() {
			protected Object create(Object objectId) {
				return new ArrayList();
			}
		};

		public MessageContextImpl(MessageSource messageSource) {
			this.messageSource = messageSource;
		}

		public Serializable createMessagesMemento() {
			return new HashMap(objectMessages);
		}

		public void restoreMessages(Serializable messagesMemento) {
			this.objectMessages.putAll((Map) messagesMemento);
		}

		public void addMessage(MessageResolver messageResolver) {
			Locale currentLocale = LocaleContextHolder.getLocale();
			Message message = messageResolver.resolveMessage(messageSource, currentLocale);
			List messages = (List) objectMessages.get(message.getSource());
			messages.add(message);
		}

		public Message[] getMessages() {
			List messages = new ArrayList();
			Iterator i = objectMessages.keySet().iterator();
			while (i.hasNext()) {
				messages.addAll((List) objectMessages.get(i.next()));
			}
			return (Message[]) messages.toArray(new Message[messages.size()]);
		}

		public Message[] getMessages(Object source) {
			List messages = (List) objectMessages.get(source);
			return (Message[]) messages.toArray(new Message[messages.size()]);
		}

		public void clearMessages() {
			objectMessages.clear();
		}

	}
}