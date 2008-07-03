package org.springframework.binding.expression.beanwrapper;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;

public class BeanWrapperExpression implements Expression {

	private String expression;

	private ConversionService conversionService;

	public BeanWrapperExpression(String expression, ConversionService conversionService) {
		this.expression = expression;
		this.conversionService = conversionService;
	}

	public boolean equals(Object o) {
		if (!(o instanceof BeanWrapperExpression)) {
			return false;
		}
		BeanWrapperExpression other = (BeanWrapperExpression) o;
		return expression.equals(other.expression);
	}

	public int hashCode() {
		return expression.hashCode();
	}

	public Object getValue(Object context) throws EvaluationException {
		try {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(context);
			return beanWrapper.getPropertyValue(expression);
		} catch (InvalidPropertyException e) {
			throw new PropertyNotFoundException(new EvaluationAttempt(this, context), e);
		} catch (BeansException e) {
			throw new EvaluationException(new EvaluationAttempt(this, context), e);
		}
	}

	public void setValue(Object context, Object value) {
		try {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(context);
			ConversionExecutor[] converters = conversionService.getConversionExecutors(String.class);
			for (int i = 0; i < converters.length; i++) {
				ConversionExecutor converter = converters[i];
				beanWrapper.registerCustomEditor(converter.getTargetClass(), new PropertyEditorConverter(converter));
			}
			beanWrapper.setPropertyValue(expression, value);
		} catch (InvalidPropertyException e) {
			throw new PropertyNotFoundException(new EvaluationAttempt(this, context), e);
		} catch (BeansException e) {
			throw new EvaluationException(new EvaluationAttempt(this, context), e);
		}
	}

	public Class getValueType(Object context) {
		try {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(context);
			return beanWrapper.getPropertyType(expression);
		} catch (InvalidPropertyException e) {
			throw new PropertyNotFoundException(new EvaluationAttempt(this, context), e);
		} catch (BeansException e) {
			throw new EvaluationException(new EvaluationAttempt(this, context), e);
		}
	}

	public String getExpressionString() {
		return expression;
	}

	public String toString() {
		return expression;
	}

	public static class PropertyEditorConverter extends PropertyEditorSupport {

		private ConversionExecutor converter;

		public PropertyEditorConverter(ConversionExecutor converter) {
			this.converter = converter;
		}

		public void setAsText(String text) throws IllegalArgumentException {
			setValue(converter.execute(text));
		}
	}
}
