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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Adapts a MessageContext object to the Spring Errors interface. Allows Spring Validators to record errors that are
 * managed by a backing MessageContext.
 * 
 * @author Keith Donald
 */
public class MessageContextErrors extends AbstractErrors {

	private MessageContext messageContext;

	private String objectName;

	private Object boundObject;

	private ExpressionParser expressionParser;

	private MappingResults mappingResults;

	/**
	 * Creates a new message context errors adapter.
	 * @param messageContext the backing message context
	 * @param objectName the object name
	 */
	public MessageContextErrors(MessageContext messageContext, String objectName, Object boundObject,
			ExpressionParser expressionParser, MappingResults mappingResults) {
		this.messageContext = messageContext;
		this.messageContext = messageContext;
		this.boundObject = boundObject;
		this.objectName = objectName;
		this.expressionParser = expressionParser;
		this.mappingResults = mappingResults;
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
		Iterator it = errors.getAllErrors().iterator();
		while (it.hasNext()) {
			ObjectError error = (ObjectError) it.next();
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				rejectValue(fieldError.getField(), error.getCode(), error.getArguments(), error.getDefaultMessage());
			} else {
				reject(error.getCode(), error.getArguments(), error.getDefaultMessage());
			}
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public List getGlobalErrors() {
		Message[] messages = messageContext.getMessagesByCriteria(GLOBAL_ERROR);
		if (messages.length == 0) {
			return Collections.EMPTY_LIST;
		}
		List errors = new ArrayList(messages.length);
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			errors.add(new ObjectError(objectName, message.getText()));
		}
		return errors;
	}

	public List getFieldErrors() {
		Message[] messages = messageContext.getMessagesByCriteria(FIELD_ERROR);
		if (messages.length == 0) {
			return Collections.EMPTY_LIST;
		}
		List errors = new ArrayList(messages.length);
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			errors.add(new FieldError(objectName, (String) message.getSource(), message.getText()));
		}
		return errors;
	}

	public Object getFieldValue(String field) {
		if (mappingResults != null) {
			List results = mappingResults.getResults(new FieldErrorResult(field));
			if (!results.isEmpty()) {
				MappingResult fieldError = (MappingResult) results.get(0);
				return fieldError.getResult().getOriginalValue();
			}
		}
		return parseFieldExpression(field).getValue(boundObject);
	}

	private Expression parseFieldExpression(String field) {
		return expressionParser.parseExpression(field, new FluentParserContext().evaluate(boundObject.getClass()));
	}

	private static MessageCriteria GLOBAL_ERROR = new MessageCriteria() {
		public boolean test(Message message) {
			if (message.getSource() == null && message.getSeverity().equals(Severity.ERROR)) {
				return true;
			} else {
				return false;
			}
		}
	};

	private static MessageCriteria FIELD_ERROR = new MessageCriteria() {
		public boolean test(Message message) {
			if (message.getSource() != null && message.getSeverity().equals(Severity.ERROR)) {
				return true;
			} else {
				return false;
			}
		}
	};

	private static class FieldErrorResult implements MappingResultsCriteria {

		private String field;

		public FieldErrorResult(String field) {
			this.field = field;
		}

		public boolean test(MappingResult result) {
			if (field.equals(result.getMapping().getTargetExpression().getExpressionString())) {
				return true;
			} else {
				return false;
			}
		}
	}
}