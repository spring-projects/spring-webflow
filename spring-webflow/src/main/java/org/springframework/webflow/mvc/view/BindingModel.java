package org.springframework.webflow.mvc.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterRegistry;
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

/**
 * Makes the properties of the "model" object available to Spring views during rendering. Also makes data binding (aka
 * mapping) results available after a form postback attempt. Also makes error messages available to the view.
 * 
 * This class is a Spring Errors adapter, basically, for use with spring form and bind tags.
 * 
 * @see MvcView
 * 
 * @author Keith Donald
 */
public class BindingModel extends AbstractErrors {

	private String objectName;

	private Object boundObject;

	private ExpressionParser expressionParser;

	private FormatterRegistry formatterRegistry;

	private MappingResults mappingResults;

	private MessageContext messageContext;

	/**
	 * Creates a new Spring Binding model.
	 * @param objectName the name of the bound model object
	 * @param boundObject the bound model object
	 * @param expressionParser the expression parser used to access model object properties
	 * @param formatterRegistry the formatter registry used to access formatters for formatting properties
	 * @param messageContext the message context containing flow messages to display
	 */
	public BindingModel(String objectName, Object boundObject, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry, MessageContext messageContext) {
		Assert.hasText(objectName, "The object name is required");
		Assert.notNull(boundObject, "The bound object instance is required");
		this.objectName = objectName;
		this.boundObject = boundObject;
		this.expressionParser = expressionParser;
		this.formatterRegistry = formatterRegistry;
		this.messageContext = messageContext;
	}

	/**
	 * Sets the results of a data mapping attempt onto the bound model object from the view.
	 * @see MvcView#processUserEvent()
	 * @param results
	 */
	public void setMappingResults(MappingResults results) {
		this.mappingResults = results;
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
		Formatter formatter = getFormatter(fieldExpression);
		if (formatter != null) {
			return formatter.format(fieldExpression.getValue(boundObject));
		} else {
			return fieldExpression.getValue(boundObject);
		}
	}

	private Formatter getFormatter(Expression fieldExpression) {
		if (formatterRegistry != null) {
			Class valueType = fieldExpression.getValueType(boundObject);
			return formatterRegistry.getFormatter(valueType);
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
