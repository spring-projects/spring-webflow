/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.List;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.util.StringUtils;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
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

	private MessageCodesResolver bindingErrorMessageCodesResolver;

	/**
	 * Creates a new message context errors adapter.
	 * @param messageContext the backing message context
	 * @param objectName the object name
	 * @param boundObject the model object
	 * @param expressionParser the expression parser
	 * @param bindingErrorMessageCodesResolver the message codes resolver
	 * @param mappingResults object mapping results
	 */
	public MessageContextErrors(MessageContext messageContext, String objectName, Object boundObject,
			ExpressionParser expressionParser, MessageCodesResolver bindingErrorMessageCodesResolver,
			MappingResults mappingResults) {
		this.messageContext = messageContext;
		this.objectName = objectName;
		this.boundObject = boundObject;
		this.expressionParser = expressionParser;
		this.bindingErrorMessageCodesResolver = bindingErrorMessageCodesResolver;
		this.mappingResults = mappingResults;
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		String[] messageCodes = bindingErrorMessageCodesResolver.resolveMessageCodes(errorCode, objectName);
		messageContext.addMessage(new MessageBuilder().error().codes(messageCodes).args(errorArgs)
				.defaultText(defaultMessage).build());
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		field = fixedField(field);
		Class<?> fieldType;
		if (StringUtils.hasLength(field) && (expressionParser != null)) {
			FluentParserContext parserContext = new FluentParserContext().evaluate(boundObject.getClass());
			fieldType = expressionParser.parseExpression(field, parserContext).getValueType(boundObject);
		} else {
			fieldType = null;
		}
		String[] messageCodes;
		if (StringUtils.hasLength(field)) {
			messageCodes = bindingErrorMessageCodesResolver
					.resolveMessageCodes(errorCode, objectName, field, fieldType);
		} else {
			messageCodes = bindingErrorMessageCodesResolver.resolveMessageCodes(errorCode, objectName);
		}
		messageContext.addMessage(new MessageBuilder().error().source(field).codes(messageCodes).args(errorArgs)
				.defaultText(defaultMessage).build());
	}

	public void addAllErrors(Errors errors) {
		for (ObjectError error : errors.getAllErrors()) {
			MessageBuilder builder = new MessageBuilder().error().codes(error.getCodes()).args(error.getArguments())
					.defaultText(error.getDefaultMessage());
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				builder.source(fieldError.getField());
			}
			messageContext.addMessage(builder.build());
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public List<ObjectError> getGlobalErrors() {
		Message[] messages = messageContext.getMessagesByCriteria(GLOBAL_ERROR);
		if (messages.length == 0) {
			return Collections.emptyList();
		}
		List<ObjectError> errors = new ArrayList<>(messages.length);
		for (Message message : messages) {
			errors.add(new ObjectError(objectName, message.getText()));
		}
		return Collections.unmodifiableList(errors);
	}

	public List<FieldError> getFieldErrors() {
		Message[] messages = messageContext.getMessagesByCriteria(FIELD_ERROR);
		if (messages.length == 0) {
			return Collections.emptyList();
		}
		List<FieldError> errors = new ArrayList<>(messages.length);
		for (Message message : messages) {
			errors.add(new FieldError(objectName, (String) message.getSource(), message.getText()));
		}
		return Collections.unmodifiableList(errors);
	}

	public Object getFieldValue(String field) {
		field = fixedField(field);
		// requires boundObject and expressionParser to be set to work
		if (mappingResults != null) {
			List<MappingResult> results = mappingResults.getResults(new PropertyErrorMappingResult(field));
			if (!results.isEmpty()) {
				MappingResult fieldError = results.get(0);
				return fieldError.getOriginalValue();
			}
		}
		return parseFieldExpression(field).getValue(boundObject);
	}

	// internal helpers

	private Expression parseFieldExpression(String field) {
		return expressionParser.parseExpression(field, new FluentParserContext().evaluate(boundObject.getClass()));
	}

	private static MessageCriteria GLOBAL_ERROR = message -> {
		if (message.getSeverity() == Severity.ERROR && message.getSource() == null) {
			return true;
		} else {
			return false;
		}
	};

	private static MessageCriteria FIELD_ERROR = message -> {
		if (message.getSeverity() == Severity.ERROR && message.getSource() instanceof String) {
			return true;
		} else {
			return false;
		}
	};

	private static class PropertyErrorMappingResult implements MappingResultsCriteria {

		private String property;

		public PropertyErrorMappingResult(String property) {
			this.property = property;
		}

		public boolean test(MappingResult result) {
			if (result.isError() && property.equals(result.getMapping().getTargetExpression().getExpressionString())) {
				return true;
			} else {
				return false;
			}
		}
	}
}
