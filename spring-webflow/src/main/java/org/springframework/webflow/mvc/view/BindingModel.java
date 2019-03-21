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
package org.springframework.webflow.mvc.view;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyEditorRegistry;
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
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.webflow.engine.builder.BinderConfiguration;

/**
 * Makes the properties of the "model" object available to Spring views during rendering. Also makes data binding (aka
 * mapping) results available after a form postback attempt. Also makes error messages available to the view.
 * 
 * This class is a Spring Errors adapter, basically, for use with spring form and bind tags.
 * 
 * @see AbstractMvcView
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class BindingModel extends AbstractErrors implements BindingResult {

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

	public List<ObjectError> getAllErrors() {
		return toErrors(messageContext.getMessagesByCriteria(ERRORS_ANY_SOURCE), ALL_ERRORS);
	}

	public List<ObjectError> getGlobalErrors() {
		return toErrors(messageContext.getMessagesByCriteria(ERRORS_WITHOUT_FIELD_SOURCE), ALL_ERRORS);
	}

	public List<FieldError> getFieldErrors(String field) {
		field = fixedField(field);
		MessageCriteria messageCriteria;
		if (field.endsWith("*")) {
			String prefix = field.substring(0, field.length() - 1);
			messageCriteria = new FieldPrefixErrorMessage(prefix);
		} else {
			messageCriteria = new FieldErrorMessage(field);
		}
		return toErrors(messageContext.getMessagesByCriteria(messageCriteria), FIELD_ERRORS);
	}

	public Class<?> getFieldType(String field) {
		return parseFieldExpression(fixedField(field), false).getValueType(boundObject);
	}

	public Object getFieldValue(String field) {
		field = fixedField(field);
		if (mappingResults != null) {
			List<MappingResult> results = mappingResults.getResults(new FieldErrorResult(field));
			if (!results.isEmpty()) {
				MappingResult fieldError = results.get(0);
				return fieldError.getOriginalValue();
			}
		}
		return getFormattedValue(field);
	}

	// not typically used by mvc views, but implemented to be on the safe side

	public List<FieldError> getFieldErrors() {
		return toErrors(messageContext.getMessagesByCriteria(ERRORS_FIELD_SOURCE), FIELD_ERRORS);
	}

	public String getObjectName() {
		return objectName;
	}

	// never expected to be called by mvc views

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	// implementing BindingResult

	public Object getTarget() {
		return boundObject;
	}

	public Object getRawFieldValue(String field) {
		return parseFieldExpression(fixedField(field), false).getValue(boundObject);
	}

	public PropertyEditor findEditor(String field, Class<?> valueType) {
		if (field != null) {
			field = fixedField(field);
		}
		return findSpringConvertingPropertyEditor(field, valueType);
	}

	// never expected to be called by mvc views

	public void addError(ObjectError error) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public Map<String, Object> getModel() {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public PropertyEditorRegistry getPropertyEditorRegistry() {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public String[] getSuppressedFields() {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public void recordSuppressedField(String field) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public String[] resolveMessageCodes(String errorCode, String field) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	public String[] resolveMessageCodes(String errorCode) {
		throw new UnsupportedOperationException("Should not be called during view rendering");
	}

	// internal helpers

	private Expression parseFieldExpression(String field, boolean useResultTypeHint) {
		FluentParserContext parserContext = new FluentParserContext().evaluate(boundObject.getClass());
		if (useResultTypeHint) {
			parserContext.expectResult(String.class);
		}
		return expressionParser.parseExpression(field, parserContext);
	}

	private Object getFormattedValue(String field) {
		Expression fieldExpression = parseFieldExpression(field, true);
		Class<?> valueType = fieldExpression.getValueType(boundObject);
		if (isCustomConverterConfigured(field) || avoidConversion(valueType)) {
			fieldExpression = parseFieldExpression(fieldExpression.getExpressionString(), false);
		}
		Object value = fieldExpression.getValue(boundObject);
		if ((value instanceof String) == false) {
			if (avoidConversion(valueType) == false) {
				PropertyEditor editor = findSpringConvertingPropertyEditor(field, valueType);
				if (editor != null) {
					editor.setValue(value);
					value = editor.getAsText();
				}
			}
		}
		return value;
	}

	private boolean isCustomConverterConfigured(String field) {
		if (binderConfiguration == null) {
			return false;
		}
		return (binderConfiguration.getConverterId(field) != null);
	}

	private boolean avoidConversion(Class<?> valueType) {
		// special handling for array, collection, map types
		// necessary as getFieldValue is called by form tags for non-formattable properties, too
		// TODO - investigate how to improve this in Spring MVC
		if (valueType == null || valueType.isArray() || Collection.class.isAssignableFrom(valueType)
				|| Map.class.isAssignableFrom(valueType)) {
			return true;
		}
		return false;
	}

	private PropertyEditor findSpringConvertingPropertyEditor(String field, Class<?> valueType) {
		if (conversionService != null) {
			String converterId = null;
			if (field != null) {
				if (binderConfiguration != null) {
					converterId = binderConfiguration.getConverterId(field);
				}
				if (valueType == null) {
					valueType = parseFieldExpression(field, false).getValueType(boundObject);
				}
			}
			if (valueType != null) {
				BeanWrapper accessor = PropertyAccessorFactory.forBeanPropertyAccess(boundObject);
				TypeDescriptor typeDescriptor = accessor.getPropertyTypeDescriptor(field);
				return new ConvertingPropertyEditorAdapter(conversionService, converterId, typeDescriptor);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private <T extends ObjectError> List<T> toErrors(Message[] messages, ObjectErrorFactory<T> errorFactory) {
		if (messages == null || messages.length == 0) {
			return Collections.emptyList();
		}
		ArrayList<T> errors = new ArrayList<>(messages.length);
		for (Message message : messages) {
			T error = errorFactory.get(objectName, message);
			if (error != null) {
				errors.add(error);
			}
		}
		return Collections.unmodifiableList(errors);
	}

	private static class FieldErrorResult implements MappingResultsCriteria {

		private String field;

		public FieldErrorResult(String field) {
			this.field = field;
		}

		public boolean test(MappingResult result) {
			if (result.isError() && field.equals(result.getMapping().getTargetExpression().getExpressionString())) {
				return true;
			} else {
				return false;
			}
		}
	}

	private static final MessageCriteria ERRORS_ANY_SOURCE =
			message -> message.getSeverity() == Severity.ERROR;

	private static final MessageCriteria ERRORS_WITHOUT_FIELD_SOURCE =
			message -> (!message.hasField() && message.getSeverity() == Severity.ERROR);

	private static final MessageCriteria ERRORS_FIELD_SOURCE =
			message -> (message.hasField() && message.getSeverity() == Severity.ERROR);

	private static class FieldErrorMessage implements MessageCriteria {
		private String field;

		public FieldErrorMessage(String field) {
			Assert.hasText(field, "The field name is required");
			this.field = field;
		}

		public boolean test(Message message) {
			return message.getSeverity() == Severity.ERROR && field.equals(message.getSource());
		}
	}

	private static class FieldPrefixErrorMessage implements MessageCriteria {
		private String fieldPrefix;

		public FieldPrefixErrorMessage(String fieldPrefix) {
			Assert.hasText(fieldPrefix, "The fieldPrefix is required");
			this.fieldPrefix = fieldPrefix;
		}

		public boolean test(Message message) {
			return message.getSeverity() == Severity.ERROR && message.getSource() instanceof String
					&& ((String) message.getSource()).startsWith(fieldPrefix);
		}
	}

	private interface ObjectErrorFactory<T extends ObjectError> {
		T get(String objectName, Message message);
	}

	private static final ObjectErrorFactory<ObjectError> ALL_ERRORS = new ObjectErrorFactory<ObjectError>() {

		public ObjectError get(String objectName, Message message) {
			ObjectError error = FIELD_ERRORS.get(objectName, message);
			if (error == null) {
				error = new ObjectError(objectName, message.getText());
			}
			return error;
		}
	};

	private static final ObjectErrorFactory<FieldError> FIELD_ERRORS = (objectName, message) -> {
		if (message.getSource() != null) {
			return new FieldError(objectName, (String) message.getSource(), message.getText());
		}
		return null;
	};

}
