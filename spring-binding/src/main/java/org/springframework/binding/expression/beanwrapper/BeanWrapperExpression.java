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
package org.springframework.binding.expression.beanwrapper;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.expression.ValueCoercionException;

/**
 * An expression that delegates to a {@link BeanWrapperImpl bean wrapper} to evaluate or set a property of a context.
 *
 * Also supports the configuration of a {@link ConversionService} to allow StringToObject type conversion to occur as
 * part of setting a property. The StringToObject ConversionExecutors are automatically adapted and registered as
 * PropertyEditors.
 *
 * Mainly exists to take advantage of BeanWrapper's unique property access features as an Expression implementation,
 * notably the ability to infer types of generic collections and maps and perform type coersion on collection elements
 * when setting values.
 *
 * Note that Spring's BeanWrapper is not a full-blown EL implementation: it only supports property access, and does not
 * support method invocation, arithmetic operations, or logic operations.
 *
 * @author Keith Donald
 * @author Scott Andrews
 */
public class BeanWrapperExpression implements Expression {

	private String expression;

	private ConversionService conversionService;

	private boolean autoGrowNestedPaths = false;

	private int autoGrowCollectionLimit = Integer.MAX_VALUE;


	/**
	 * Creates a new bean wrapper expression.
	 * @param expression the property expression string
	 * @param conversionService the conversion service containing converters to use as PropertyEditors for type
	 * conversion
	 */
	public BeanWrapperExpression(String expression, ConversionService conversionService) {
		this.expression = expression;
		this.conversionService = conversionService;
	}

	/**
	 * Set whether this BeanWrapper should attempt to "auto-grow" a nested path that contains a null value.
	 * <p>If "true", a null path location will be populated with a default object value and traversed
	 * instead of resulting in a {@link NullValueInNestedPathException}. Turning this flag on also
	 * enables auto-growth of collection elements when accessing an out-of-bounds index.
	 * <p>Default is "false" on a plain BeanWrapper.
	 */
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		this.autoGrowNestedPaths = autoGrowNestedPaths;
	}

	/**
	 * Specify a limit for array and collection auto-growing.
	 * <p>Default is unlimited on a plain BeanWrapper.
	 */
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
		this.autoGrowCollectionLimit = autoGrowCollectionLimit;
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
			beanWrapper.setAutoGrowNestedPaths(autoGrowNestedPaths);
			beanWrapper.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
			return beanWrapper.getPropertyValue(expression);
		} catch (NotReadablePropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), expression, e);
		} catch (BeansException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"A BeansException occurred getting the value for expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "]", e);
		}
	}

	public void setValue(Object context, Object value) {
		try {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(context);
			beanWrapper.setAutoGrowNestedPaths(autoGrowNestedPaths);
			beanWrapper.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
			beanWrapper.setConversionService(conversionService.getDelegateConversionService());
			beanWrapper.setPropertyValue(expression, value);
		} catch (NotWritablePropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), expression, e);
		} catch (TypeMismatchException e) {
			throw new ValueCoercionException(context.getClass(), expression, value, e.getRequiredType(), e);
		} catch (BeansException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"A BeansException occurred setting the value of expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "] to [" + value + "]", e);
		}
	}

	public Class<?> getValueType(Object context) {
		try {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(context);
			return beanWrapper.getPropertyType(expression);
		} catch (NotReadablePropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), expression, e);
		} catch (BeansException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"An BeansException occurred getting the value type for expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "]", e);
		}
	}

	public String getExpressionString() {
		return expression;
	}

	public String toString() {
		return expression;
	}

}
