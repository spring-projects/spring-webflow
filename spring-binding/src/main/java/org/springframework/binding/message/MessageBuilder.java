package org.springframework.binding.message;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

/**
 * A convenient builder for building {@link MessageResolver} objects programmatically. Often used by model code such as
 * validation logic to conveniently record validation messages. Supports the production of message resolvers that
 * hard-code their message text, as well as message resolvers that retrieve their text from a
 * {@link MessageSource message resource bundle}.
 * 
 * Usage example:
 * <p>
 * <code>
 * new MessageBuilder().error().source(this).code(&quot;mycode&quot;).args(new Object[] { arg1, arg2 }).defaultText(&quot;Fallback text&quot;)
 * 		.build();
 * </code>
 * </p>
 * @author Keith Donald
 */
public class MessageBuilder {

	private Object source;

	private String[] codes;

	private Severity severity;

	private Object[] args;

	private String defaultText;

	/**
	 * Records that the message being built is from the source provided.
	 * @param source the source generating the message
	 * @return this, for fluent API usage
	 */
	public MessageBuilder source(Object source) {
		this.source = source;
		return this;
	}

	/**
	 * Records that the message being built should have its text resolved using the code provided.
	 * @param code the message code
	 * @return this, for fluent API usage
	 */
	public MessageBuilder code(String code) {
		codes = new String[] { code };
		return this;
	}

	/**
	 * Records that the message being built should have its text resolved using the codes provided. The codes are tried
	 * in-order until their is a match.
	 * @param codes the message codes
	 * @return this, for fluent API usage
	 */
	public MessageBuilder codes(String[] codes) {
		this.codes = codes;
		return this;
	}

	/**
	 * Records that the message being built is an informational message.
	 * @return this, for fluent API usage
	 */
	public MessageBuilder info() {
		severity = Severity.INFO;
		return this;
	}

	/**
	 * Records that the message being built is a warning message.
	 * @return this, for fluent API usage
	 */
	public MessageBuilder warning() {
		severity = Severity.WARNING;
		return this;
	}

	/**
	 * Records that the message being built is an error message.
	 * @return this, for fluent API usage
	 */
	public MessageBuilder error() {
		severity = Severity.ERROR;
		return this;
	}

	/**
	 * Records that the message being built has a single argument.
	 * @param arg the message argument
	 * @return this, for fluent API usage
	 */
	public MessageBuilder arg(Object arg) {
		this.args = new Object[] { arg };
		return this;
	}

	/**
	 * Records that the message being built has arguments.
	 * @param args the message arguments
	 * @return this, for fluent API usage
	 */
	public MessageBuilder args(Object[] args) {
		this.args = args;
		return this;
	}

	/**
	 * Records the fallback text of the message being built. If the message has no codes, this will always be used as
	 * the text.
	 * @param text the default text
	 * @return this, for fluent API usage
	 */
	public MessageBuilder defaultText(String text) {
		defaultText = text;
		return this;
	}

	/**
	 * Builds the message that will be resolved. Called after the end of recording builder instructions.
	 * @return the built message resolver
	 */
	public MessageResolver build() {
		if (severity == null) {
			severity = Severity.INFO;
		}
		if (codes == null && defaultText == null) {
			throw new IllegalArgumentException(
					"A message code or the message text is required to build this message resolver");
		}
		return new BuiltMessageResolver(source, codes, severity, args, defaultText);
	}

	private static class BuiltMessageResolver implements MessageResolver, MessageSourceResolvable {
		private Object source;
		private String[] codes;
		private Severity severity;
		private Object[] args;
		private String defaultText;

		public BuiltMessageResolver(Object source, String[] codes, Severity severity, Object[] args, String defaultText) {
			this.source = source;
			this.codes = codes;
			this.severity = severity;
			this.args = args;
			this.defaultText = defaultText;
		}

		public Message resolveMessage(MessageSource messageSource, Locale locale) {
			return new Message(source, messageSource.getMessage(this, locale), severity);
		}

		// implementing MessageSourceResolver

		public String[] getCodes() {
			return codes;
		}

		public Object[] getArguments() {
			return args;
		}

		public String getDefaultMessage() {
			return defaultText;
		}
	}

}
