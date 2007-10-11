package org.springframework.binding.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * A convenient factory for creating {@link MessageResolver} objects programmatically. Often used by model code such as
 * validation logic to conveniently record validation messages. Supports the production of "text" message resolvers that
 * hard-code their message text, as well as message resolvers that retrieve their text from a
 * {@link MessageSource message resource bundle}.
 * 
 * @author Keith Donald
 */
public class Messages implements MessageResolver {

	private Object source;

	private String code;

	private Severity severity;

	private Object[] args;

	private String defaultText;

	private Messages(Object source, String code, Severity severity, Object[] args, String defaultText) {
		this.source = source;
		this.code = code;
		this.severity = severity;
		this.args = args;
		this.defaultText = defaultText;
	}

	public Message resolveMessage(MessageSource messageSource, Locale locale) {
		if (messageSource != null && (code != null && code.length() > 0)) {
			return new Message(source, getMessageText(messageSource, locale), severity);
		} else {
			return new Message(source, defaultText, severity);
		}
	}

	/**
	 * Creates a message resolver that creates a INFO {@link Message} with the text provided.
	 * @param text the raw message text that will be used as-is
	 * @return the message resolver
	 */
	public static Messages text(String text) {
		return new Messages(null, null, Severity.INFO, null, text);
	}

	/**
	 * Creates a message resolver that creates a {@link Message} with the text and severity provided.
	 * @param text the raw message text that will be used as-is
	 * @param severity the desired message severity
	 * @return the message resolver
	 */
	public static Messages text(String text, Severity severity) {
		return new Messages(null, null, severity, null, text);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#INFO info} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code.
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages info(String code) {
		return new Messages(null, code, Severity.INFO, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#WARNING warning} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code.
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages warning(String code) {
		return new Messages(null, code, Severity.WARNING, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#ERROR error} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code.
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages error(String code) {
		return new Messages(null, code, Severity.ERROR, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#INFO info} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code and message arguments.
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages info(String code, Object[] args) {
		return new Messages(null, code, Severity.INFO, args, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#WARNING warning} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code and message arguments.
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages warning(String code, Object[] args) {
		return new Messages(null, code, Severity.WARNING, args, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#ERROR error} {@link Message message} with its text
	 * resolved from a message bundle by using the provided message code and message arguments.
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages error(String code, Object[] args) {
		return new Messages(null, code, Severity.ERROR, args, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#INFO info} {@link Message message} from the source with
	 * the text provided.
	 * @param source the source of the message
	 * @param text the message text
	 * @return the message resolver
	 */
	public static Messages text(Object source, String text) {
		return new Messages(source, null, Severity.INFO, null, text);
	}

	/**
	 * Creates a message resolver that creates a {@link Message message} from the source with the text and severity
	 * provided.
	 * @param source the source of the message
	 * @param text the message text
	 * @param severity the message severity
	 * @return the message resolver
	 */
	public static Messages text(Object source, String text, Severity severity) {
		return new Messages(source, null, severity, null, text);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#INFO info} {@link Message message} from the source with
	 * its text resolved from a message bundle by using the provided message code.
	 * @param source the source of the message
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages info(Object source, String code) {
		return new Messages(source, code, Severity.INFO, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#WARNING warning} {@link Message message} from the
	 * source with its text resolved from a message bundle by using the provided message code.
	 * @param source the source of the message
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages warning(Object source, String code) {
		return new Messages(source, code, Severity.WARNING, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#ERROR error} {@link Message message} from the source
	 * with its text resolved from a message bundle by using the provided message code.
	 * @param source the source of the message
	 * @param code the message code
	 * @return the message resolver
	 */
	public static Messages error(Object source, String code) {
		return new Messages(source, code, Severity.ERROR, null, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#INFO info} {@link Message message} from the source with
	 * its text resolved from a message bundle by using the provided message code and message arguments.
	 * @param source the source of the message
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages info(Object source, String code, Object[] args) {
		return new Messages(source, code, Severity.INFO, args, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#WARNING warning} {@link Message message} from the
	 * source with its text resolved from a message bundle by using the provided message code and message arguments.
	 * @param source the source of the message
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages warning(Object source, String code, Object[] args) {
		return new Messages(source, code, Severity.WARNING, args, null);
	}

	/**
	 * Creates a message resolver that creates a {@link Severity#ERROR error} {@link Message message} from the source
	 * with its text resolved from a message bundle by using the provided message code and message arguments.
	 * @param source the source of the message
	 * @param code the message code
	 * @param args the message arguments
	 * @return the message resolver
	 */
	public static Messages error(Object source, String code, Object[] args) {
		return new Messages(source, code, Severity.ERROR, args, null);
	}

	private String getMessageText(MessageSource source, Locale locale) {
		if (defaultText == null) {
			return source.getMessage(code, args, locale);
		} else {
			return source.getMessage(code, args, defaultText, locale);
		}
	}

}
