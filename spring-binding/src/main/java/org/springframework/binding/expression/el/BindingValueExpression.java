package org.springframework.binding.expression.el;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.util.Assert;

/**
 * A ValueExpression decorator that decorates a target ValueExpression returned by a EL Implementation's
 * {@link ExpressionFactory} to allow use of Spring's type conversion system for coersing expression values.
 * 
 * The Unified EL specification currently provides no standard way of plugging in custom type converters. This decorator
 * allows Spring type converters to be utilized with any EL implementation.
 * 
 * @author Keith Donald
 */
class BindingValueExpression extends ValueExpression {

	private ValueExpression targetExpression;

	private Class<?> expectedType;

	private ConversionService conversionService;

	private boolean template;

	public BindingValueExpression(ValueExpression targetExpression, Class<?> expectedType,
			ConversionService conversionService, boolean template) {
		Assert.notNull(expectedType, "The expectedType Class is required");
		Assert.notNull(conversionService, "The ConversionService to perform type coersions is required");
		this.targetExpression = targetExpression;
		this.expectedType = expectedType;
		this.conversionService = conversionService;
		this.template = template;
	}

	public Class<?> getExpectedType() {
		return targetExpression.getExpectedType();
	}

	public Class<?> getType(ELContext context) throws NullPointerException, ELException {
		return targetExpression.getType(context);
	}

	public Object getValue(ELContext context) throws NullPointerException, ELException,
			ValueCoercionException {
		Object value = targetExpression.getValue(context);
		return convertValueIfNecessary(value, expectedType, context);
	}

	public boolean isReadOnly(ELContext context) throws NullPointerException, ELException {
		return targetExpression.isReadOnly(context);
	}

	public void setValue(ELContext context, Object value) throws NullPointerException, ELException, ValueCoercionException {
		value = convertValueIfNecessary(value, targetExpression.getType(context), context);
		targetExpression.setValue(context, value);
	}

	public String getExpressionString() {
		if (template) {
			return targetExpression.getExpressionString();
		} else {
			String rawExpressionString = targetExpression.getExpressionString();
			return rawExpressionString.substring("#{".length(), rawExpressionString.length() - 1);
		}
	}

	public boolean isLiteralText() {
		return targetExpression.isLiteralText();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BindingValueExpression)) {
			return false;
		}
		BindingValueExpression exp = (BindingValueExpression) obj;
		return targetExpression.equals(exp.targetExpression);
	}

	public int hashCode() {
		return targetExpression.hashCode();
	}

	private Object convertValueIfNecessary(Object value, Class<?> expectedType, Object context)
			throws ValueCoercionException {
		if (expectedType == null) {
			return value;
		} else {
			try {
				return conversionService.executeConversion(value, expectedType);
			} catch (ConversionException e) {
				throw new ValueCoercionException(context.getClass(), getExpressionString(), value, expectedType, e);
			}
		}
	}

}
