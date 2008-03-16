/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.binding.mapping.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.mapping.Mapping;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * A single mapping definition, encapsulating the information necessary to map the result of evaluating an expression on
 * a source object to a property on a target object, optionally applying a type conversion during the mapping process.
 * 
 * @author Keith Donald
 */
public class DefaultMapping implements Mapping {

	private static final Log logger = LogFactory.getLog(DefaultMapping.class);

	/**
	 * The source expression to evaluate against a source object to map from.
	 */
	private final Expression sourceExpression;

	/**
	 * The target expression to set on a target object to map to.
	 */
	private final Expression targetExpression;

	/**
	 * Whether or not this is a required mapping; if true, the source expression must return a non-null value.
	 */
	private boolean required;

	/**
	 * A specific type conversion routine to apply during the mapping process.
	 */
	private ConversionExecutor typeConverter;

	/**
	 * Creates a new mapping.
	 * @param sourceExpression the source expression
	 * @param targetExpression the target expression
	 */
	public DefaultMapping(Expression sourceExpression, Expression targetExpression) {
		Assert.notNull(sourceExpression, "The source expression is required");
		Assert.notNull(targetExpression, "The target expression is required");
		this.sourceExpression = sourceExpression;
		this.targetExpression = targetExpression;
	}

	// implementing mapping

	public Expression getSourceExpression() {
		return sourceExpression;
	}

	public Expression getTargetExpression() {
		return targetExpression;
	}

	public boolean isRequired() {
		return required;
	}

	// optional impl getters/setters

	public ConversionExecutor getTypeConverter() {
		return typeConverter;
	}

	public void setTypeConverter(ConversionExecutor typeConverter) {
		this.typeConverter = typeConverter;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Execute this mapping.
	 * @param context the mapping context
	 */
	public void map(MappingContext context) {
		context.setCurrentMapping(this);
		Object sourceValue;
		try {
			sourceValue = sourceExpression.getValue(context.getSource());
		} catch (EvaluationException e) {
			context.setSourceAccessError(e);
			return;
		}
		if (required && (sourceValue == null || isEmptyString(sourceValue))) {
			context.setRequiredErrorResult(sourceValue);
			return;
		}
		Object targetValue = sourceValue;
		if (sourceValue != null) {
			if (typeConverter != null) {
				try {
					targetValue = typeConverter.execute(targetValue);
				} catch (ConversionException e) {
					context.setTypeConversionErrorResult(sourceValue, e.getTargetClass());
					return;
				}
			} else {
				ConversionService conversionService = context.getConversionService();
				if (conversionService != null) {
					Class targetType;
					try {
						targetType = targetExpression.getValueType(context.getTarget());
					} catch (EvaluationException e) {
						context.setTargetAccessError(sourceValue, e);
						return;
					}
					if (targetType != null && !targetType.isInstance(targetValue)) {
						try {
							ConversionExecutor typeConverter = conversionService.getConversionExecutor(sourceValue
									.getClass(), targetType);
							targetValue = typeConverter.execute(sourceValue);
						} catch (ConversionException e) {
							context.setTypeConversionErrorResult(sourceValue, e.getTargetClass());
							return;
						}
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Mapping '" + sourceExpression + "' value [" + sourceValue + "] to target '"
					+ targetExpression + "'; setting target value to [" + targetValue + "]");
		}
		try {
			targetExpression.setValue(context.getTarget(), targetValue);
			context.setSuccessResult(sourceValue, targetValue);
		} catch (EvaluationException e) {
			context.setTargetAccessError(sourceValue, e);
		}
	}

	private boolean isEmptyString(Object sourceValue) {
		if (sourceValue instanceof CharSequence) {
			return ((CharSequence) sourceValue).length() == 0;
		} else {
			return false;
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof DefaultMapping)) {
			return false;
		}
		DefaultMapping other = (DefaultMapping) o;
		return sourceExpression.equals(other.sourceExpression) && targetExpression.equals(other.targetExpression);
	}

	public int hashCode() {
		return sourceExpression.hashCode() + targetExpression.hashCode();
	}

	public String toString() {
		return new ToStringCreator(this).append(sourceExpression + " -> " + targetExpression).toString();
	}
}