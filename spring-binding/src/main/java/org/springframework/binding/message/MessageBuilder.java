package org.springframework.binding.message;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
 * new MessageBuilder().error().source(this).code(&quot;mycode&quot;).arg(arg1).arg(arg2).defaultText(&quot;text&quot;).build();
 * </code>
 * </p>
 * @author Keith Donald
 */
public class MessageBuilder {

	private Object source;

	private Set codes = new LinkedHashSet();

	private Severity severity;

	private List args = new ArrayList();

	private String defaultText;

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
	 * Records that the message being built is against the provided source.
	 * @param source the source generating the message
	 * @return this, for fluent API usage
	 */
	public MessageBuilder source(Object source) {
		this.source = source;
		return this;
	}

	/**
	 * Records that the message being built should try and resolve its text using the code provided. Adds the code to
	 * the codes list. Successive calls to this method add additional codes. Codes are applied in the order they are
	 * added.
	 * @param code the message code
	 * @return this, for fluent API usage
	 */
	public MessageBuilder code(String code) {
		codes.add(code);
		return this;
	}

	/**
	 * Records that the message being built has a variable argument. Adds the arg to the args list. Successive calls to
	 * this method add additional args. Args are applied in the order they are added.
	 * @param arg the message argument value
	 * @return this, for fluent API usage
	 */
	public MessageBuilder arg(Object arg) {
		args.add(arg);
		return this;
	}

	/**
	 * Records that the message being built has a variable argument, whose display value is also
	 * {@link MessageSourceResolvable}. Adds the arg to the args list. Successive calls to this method add additional
	 * resolvable args. Args are applied in the order they are added.
	 * @param arg the resolvable message argument
	 * @return this, for fluent API usage
	 */
	public MessageBuilder resolvableArg(Object arg) {
		args.add(new ResolvableArgument(arg));
		return this;
	}

	/**
	 * Records the fallback text of the message being built. If the message has no codes, this will always be used as
	 * the text. If the message has codes but none can be resolved, this will alway be used as the text.
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
		String[] codesArray = (String[]) codes.toArray(new String[codes.size()]);
		Object[] argsArray = args.toArray(new Object[args.size()]);
		return new BuiltMessageResolver(source, codesArray, severity, argsArray, defaultText);
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

	private static class ResolvableArgument implements MessageSourceResolvable {

		private Object arg;

		public ResolvableArgument(Object arg) {
			this.arg = arg;
		}

		public Object[] getArguments() {
			return null;
		}

		public String[] getCodes() {
			return new String[] { arg.toString() };
		}

		public String getDefaultMessage() {
			return arg.toString();
		}

	}

}
