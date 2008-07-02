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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.style.ToStringCreator;

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
 * @author Jeremy Grelle
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
	 * Records that the message being built is a fatal message.
	 * @return this, for fluent API usage
	 */
	public MessageBuilder fatal() {
		severity = Severity.FATAL;
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
	 * Records that the message being built should try and resolve its text using the codes provided. Adds the codes to
	 * the codes list. Successive calls to this method add additional codes. Codes are applied in the order they are
	 * added.
	 * @param codes the message codes; if null, no changes will be made
	 * @return this, for fluent API usage
	 */
	public MessageBuilder codes(String[] codes) {
		if (codes == null) {
			return this;
		}
		this.codes.addAll(Arrays.asList(codes));
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
	 * Records that the message being built has variable arguments. Adds the args to the args list. Successive calls to
	 * this method add additional args. Args are applied in the order they are added.
	 * @param args the message argument values, if null no changes will be made
	 * @return this, for fluent API usage
	 */
	public MessageBuilder args(Object[] args) {
		if (args == null) {
			return this;
		}
		this.args.addAll(Arrays.asList(args));
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
	 * Records that the message being built has variable arguments, whose display values are also
	 * {@link MessageSourceResolvable} instances. Adds the args to the args list. Successive calls to this method add
	 * additional resolvable args. Args are applied in the order they are added.
	 * @param args the resolvable message arguments
	 * @return this, for fluent API usage
	 */
	public MessageBuilder resolvableArgs(Object[] args) {
		if (args == null) {
			return this;
		}
		for (int i = 0; i < args.length; i++) {
			this.args.add(new ResolvableArgument(args[i]));
		}
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
		return new DefaultMessageResolver(source, codesArray, severity, argsArray, defaultText);
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

		public String toString() {
			return new ToStringCreator(this).append("arg", arg).toString();
		}

	}

}
