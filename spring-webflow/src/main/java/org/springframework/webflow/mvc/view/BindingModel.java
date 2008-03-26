package org.springframework.webflow.mvc.view;

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
import org.springframework.binding.message.MessageContext;

public class BindingModel extends ViewRenderingErrors {

	private Object boundObject;

	private ExpressionParser expressionParser;

	private FormatterRegistry formatterRegistry;

	private MappingResults mappingResults;

	public BindingModel(Object boundObject, ExpressionParser expressionParser, FormatterRegistry formatterRegistry,
			MessageContext messageContext) {
		this.boundObject = boundObject;
		this.expressionParser = expressionParser;
		this.formatterRegistry = formatterRegistry;
	}

	public void setMappingResults(MappingResults results) {
		this.mappingResults = results;
	}

	public List getAllErrors() {
		// TODO
		return Collections.EMPTY_LIST;
	}

	public List getGlobalErrors() {
		// TODO
		return Collections.EMPTY_LIST;
	}

	public List getFieldErrors(String field) {
		// TODO
		return Collections.EMPTY_LIST;
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
		return getFormattedValue(field);
	}

	private Expression parseFieldExpression(String field) {
		return expressionParser.parseExpression(field, new FluentParserContext().evaluate(boundObject.getClass()));
	}

	private Object getFormattedValue(String field) {
		Expression fieldExpression = parseFieldExpression(field);
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
