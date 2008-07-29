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
package org.springframework.webflow.mvc.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.Severity;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;

/**
 * Makes the properties of the "model" object available to Spring views during rendering. Also makes data binding (aka
 * mapping) results available after a form postback attempt. Also makes error messages available to the view.
 * 
 * This class is a Spring Errors adapter, basically, for use with spring form and bind tags.
 * 
 * @see AbstractMvcView
 * 
 * @author Keith Donald
 */
public class BindingModel extends AbstractErrors {

	private String objectName;

	private Object boundObject;

	private ExpressionParser expressionParser;

	private ConversionService conversionService;

	private MappingResults mappingResults;

	private MessageContext messageContext;

	private BinderConfiguration binderConfiguration;

	/**
	 * Creates a new Spring Binding model.
	 * @param objectName the name of the bound model object
	 * @param boundObject the bound model object
	 * @param expressionParser the expression parser used to access model object properties
	 * @param conversionService the registry used to access converters for formatting properties
	 * @param messageContext the message context containing flow messages to display
	 */
	public BindingModel(String objectName, Object boundObject, ExpressionParser expressionParser,
			ConversionService conversionService, MessageContext messageContext) {
		Assert.hasText(objectName, "The object name is required");
		Assert.notNull(boundObject, "The bound object instance is required");
		this.objectName = objectName;
		this.boundObject = boundObject;
		this.expressionParser = expressionParser;
		this.conversionService = conversionService;
		this.messageContext = messageContext;
	}

	/**
	 * Sets the results of a data mapping attempt onto the bound model object from the view.
	 * @see AbstractMvcView#processUserEvent()
	 * @param results
	 */
	public void setMappingResults(MappingResults results) {
		this.mappingResults = results;
	}

	public void setBinderConfiguration(BinderConfiguration binderConfiguration) {
		this.binderConfiguration = binderConfiguration;
	}

	// implementing Errors

	public List getAllErrors() {
		return toErrors(messageContext.getMessagesByCriteria(ERRORS_ANY_SOURCE));
	}

	public List getGlobalErrors() {
		return toErrors(messageContext.getMessagesByCriteria(ERRORS_NULL_SOURCE));
	}

	public List getFieldErrors(String field) {
		return toErrors(messageContext.getMessagesByCriteria(new FieldErrorMessage(field)));
	}

	public Class getFieldType(String field) {
		return parseFieldExpression(field).getValueType(boundObject);
	}

	public Object getFieldValue(String field) {
		if (mappingResults != null) {
			List results = mappingResults.getResults(new FieldErrorResult(field));
			if (!results.isEmpty()) {
				MappingResult fieldError = (MappingResult) results.get(0);
				return fieldError.getResult().getOriginalValue();
			}
		}
		return getFormattedValue(parseFieldExpression(field));
	}

	// not typically used by mvc views, but implemented to be on the safe side

	public List getFieldErrors() {
		return toErrors(messageContext.getMessagesByCriteria(new FieldErrorMessage()));
	}

	// internal helpers

	private Expression parseFieldExpression(String field) {
		return expressionParser.parseExpression(field, new FluentParserContext().evaluate(boundObject.getClass()));
	}

	private Object getFormattedValue(Expression fieldExpression) {
		ConversionExecutor converter = getConverter(fieldExpression);
		if (converter != null) {
			return converter.execute(fieldExpression.getValue(boundObject));
		} else {
			return fieldExpression.getValue(boundObject);
		}
	}

	private ConversionExecutor getConverter(Expression fieldExpression) {
		if (conversionService != null) {
			Class valueType = fieldExpression.getValueType(boundObject);
			// special handling for array, collection, map types
			// necessary as getFieldValue is called by form tags for non-formattable properties, too
			// TODO - investigate how to improve this in Spring MVC
			if (valueType.isArray() || Collection.class.isAssignableFrom(valueType)
					|| Map.class.isAssignableFrom(valueType)) {
				return null;
			}
			if (binderConfiguration != null) {
				Binding binding = binderConfiguration.getBinding(fieldExpression.getExpressionString());
				if (binding != null) {
					String converterId = binding.getConverter();
					if (converterId != null) {
						return conversionService.getConversionExecutor(converterId, valueType, String.class);
					}
				}
			}
			return conversionService.getConversionExecutor(valueType, String.class);
		} else {
			return null;
		}
	}

	private List toErrors(Message[] messages) {
		if (messages == null || messages.length == 0) {
			return Collections.EMPTY_LIST;
		}
		ArrayList errors = new ArrayList(messages.length);
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			if (message.getSource() == null) {
				errors.add(new ObjectError(objectName, message.getText()));
			} else {
				errors.add(new FieldError(objectName, (String) message.getSource(), message.getText()));
			}
		}
		return errors;
	}

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

	private static final MessageCriteria ERRORS_ANY_SOURCE = new MessageCriteria() {
		public boolean test(Message message) {
			return message.getSeverity() == Severity.ERROR;
		}
	};

	private static final MessageCriteria ERRORS_NULL_SOURCE = new MessageCriteria() {
		public boolean test(Message message) {
			return message.getSource() == null && message.getSeverity() == Severity.ERROR;
		}
	};

	private static class FieldErrorMessage implements MessageCriteria {
		private String field;

		public FieldErrorMessage() {
		}

		public FieldErrorMessage(String field) {
			this.field = field;
		}

		public boolean test(Message message) {
			if (field != null) {
				return field.equals(message.getSource()) && message.getSeverity() == Severity.ERROR;
			} else {
				return message.getSource() != null && message.getSeverity() == Severity.ERROR;
			}
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

}
