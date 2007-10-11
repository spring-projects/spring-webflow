package org.springframework.binding.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * A factory for a Message. Allows a Message to be internationalized and to be resolved from a
 * {@link MessageSource message resource bundle}.
 * 
 * @author Keith Donald
 * @see Message
 * @see MessageSource
 */
public interface MessageResolver {

	/**
	 * Resolve the message from the message source using the current locale.
	 * @param messageSource the message source, an abstraction for a resource bundle
	 * @param locale the current locale of this request
	 * @return the resolved message
	 */
	public Message resolveMessage(MessageSource messageSource, Locale locale);
}
