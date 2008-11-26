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

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.style.ToStringCreator;

class DefaultMessageResolver implements MessageResolver, MessageSourceResolvable {
	private Object source;
	private String[] codes;
	private Severity severity;
	private Object[] args;
	private String defaultText;

	public DefaultMessageResolver(Object source, String[] codes, Severity severity, Object[] args,
			String defaultText) {
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

	public String toString() {
		return new ToStringCreator(this).append("source", source).append("severity", severity).append("codes",
				codes).append("args", args).append("defaultText", defaultText).toString();
	}
}