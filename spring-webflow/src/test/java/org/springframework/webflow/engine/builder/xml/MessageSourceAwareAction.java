/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder.xml;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class MessageSourceAwareAction extends AbstractAction implements MessageSourceAware {

	private MessageSourceAccessor messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = new MessageSourceAccessor(messageSource);
	}

	protected Event doExecute(RequestContext context) throws Exception {
		Assert.notNull(messageSource.getMessage("foo"));
		Assert.isTrue(messageSource.getMessage("foo").equals("bar"));
		try {
			messageSource.getMessage("bar");
			throw new IllegalStateException();
		} catch (NoSuchMessageException e) {
			// expected
		}
		return success();
	}

}
