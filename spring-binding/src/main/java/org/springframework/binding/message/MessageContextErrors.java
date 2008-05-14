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

import java.util.List;

import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;

/**
 * Adapts a MessageContext object to the Spring Errors interface. Allows Spring Validators to record errors that are
 * managed by a backing MessageContext.
 * 
 * @author Keith Donald
 */
public class MessageContextErrors extends AbstractErrors {

	private MessageContext messageContext;

	/**
	 * Creates a new message context errors adapter.
	 * @param messageContext the backing message context
	 */
	public MessageContextErrors(MessageContext messageContext) {
		this.messageContext = messageContext;
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().code(errorCode).args(errorArgs).defaultText(
				defaultMessage).build());
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		messageContext.addMessage(new MessageBuilder().error().source(field).code(errorCode).args(errorArgs)
				.defaultText(defaultMessage).build());
	}

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Not expected to be called by a validator");
	}

	public List getFieldErrors() {
		throw new UnsupportedOperationException("Not expected to be called by a validator");
	}

	public Object getFieldValue(String field) {
		throw new UnsupportedOperationException("Not expected to be called by a validator");
	}

	public List getGlobalErrors() {
		throw new UnsupportedOperationException("Not expected to be called by a validator");
	}

	public String getObjectName() {
		throw new UnsupportedOperationException("Not expected to be called by a validator");
	}

}
