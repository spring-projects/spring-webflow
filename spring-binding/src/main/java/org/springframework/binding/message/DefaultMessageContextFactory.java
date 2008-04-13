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

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;

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
		if (messageSource == null) {
			messageSource = new DefaultTextFallbackMessageSource();
		}
		this.messageSource = messageSource;
	}

	public StateManageableMessageContext createMessageContext() {
		return new DefaultMessageContext(messageSource);
	}

	private class DefaultTextFallbackMessageSource extends AbstractMessageSource {
		protected MessageFormat resolveCode(String code, Locale locale) {
			return null;
		}
	}
}