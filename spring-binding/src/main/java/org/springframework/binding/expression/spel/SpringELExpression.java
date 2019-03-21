/*
 * Copyright 2004-2018 the original author or authors.
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
package org.springframework.binding.expression.spel;

import java.util.List;
import java.util.Map;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * A wrapper for a Spring EL {@link org.springframework.expression.Expression}
 * allowing it to be used under the Spring Binding {@link Expression} abstraction.
 *
 * @author Rossen Stoyanchev
 * @since 2.1.0
 */
public class SpringELExpression implements Expression {

	private final org.springframework.expression.Expression expression;

	private final Class<?> expectedType;

	private final EvaluationContextFactory contextFactory;


	/**
	 * Constructor for SpringELExpression.
	 *
	 * @param expression a parsed Spring EL expression instance. Must not be null.
	 * @param expressionVars provides a mapping between variables names and
	 * parsed Spring EL expression instances.
	 * This parameter is optional (may be null).
	 * @param expectedType the target type expected from the evaluation of the expression or null.
	 * This parameter is optional (may be null).
	 * @param conversionService the Spring ConversionService instance to use for type conversion
	 * @param propertyAccessors propertyAccessors for Spring EL to use when evaluating expressions
	 */
	public SpringELExpression(org.springframework.expression.Expression expression,
			Map<String, Expression> expressionVars, Class<?> expectedType, ConversionService conversionService,
			List<PropertyAccessor> propertyAccessors) {

		this(expression, expectedType,
				new StandardEvaluationContextFactory(propertyAccessors, conversionService, expressionVars));
	}

	/**
	 * Generalized constructor variant that accepts an {@link EvaluationContextFactory}.
	 * @since 2.4.8
	 */
	public SpringELExpression(org.springframework.expression.Expression expression, Class<?> expectedType,
			EvaluationContextFactory contextFactory) {

		Assert.notNull(expression, "The SpelExpression is required for evaluation");
		Assert.notNull(contextFactory, "The EvaluationContextFactory is required");
		this.expression = expression;
		this.expectedType = expectedType;
		this.contextFactory = contextFactory;
	}

	public String getExpressionString() {
		return expression.getExpressionString();
	}

	@SuppressWarnings("deprecation")
	public Object getValue(Object rootObject) throws EvaluationException {
		try {
			EvaluationContext context = contextFactory.createContext(rootObject);
			if (context instanceof StandardEvaluationContext) {
				extendEvaluationContext((StandardEvaluationContext) context);
			}
			return expression.getValue(context, expectedType);
		} catch (SpelEvaluationException e) {
			if (e.getMessageCode().equals(SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE)) {
				throw new PropertyNotFoundException(rootObject.getClass(), getExpressionString(), e);
			}
			if (e.getMessageCode().equals(SpelMessage.TYPE_CONVERSION_ERROR)) {
				throw new ValueCoercionException(rootObject.getClass(), getExpressionString(), null, expectedType, e);
			}
			throw new EvaluationException(rootObject.getClass(), expression.getExpressionString(),
					"An ELException occurred getting the value for expression '" + getExpressionString()
							+ "' on context [" + rootObject.getClass() + "]", e);
		}
	}

	@SuppressWarnings("deprecation")
	public Class<?> getValueType(Object rootObject) throws EvaluationException {
		try {
			EvaluationContext context = contextFactory.createContext(rootObject);
			if (context instanceof StandardEvaluationContext) {
				extendEvaluationContext((StandardEvaluationContext) context);
			}
			return expression.getValueType(context);
		} catch (SpelEvaluationException e) {
			if (e.getMessageCode().equals(SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE)) {
				throw new PropertyNotFoundException(rootObject.getClass(), getExpressionString(), e);
			}
			throw new EvaluationException(rootObject.getClass(), getExpressionString(),
					"An ELException occurred getting the value type for expression '" + getExpressionString()
							+ "' on context [" + rootObject.getClass() + "]", e);
		}
	}

	@SuppressWarnings("deprecation")
	public void setValue(Object rootObject, Object value) throws EvaluationException {
		try {
			EvaluationContext context = contextFactory.createContext(rootObject);
			if (context instanceof StandardEvaluationContext) {
				extendEvaluationContext((StandardEvaluationContext) context);
			}
			expression.setValue(context, value);
		} catch (SpelEvaluationException e) {
			if (e.getMessageCode().equals(SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE)) {
				throw new PropertyNotFoundException(rootObject.getClass(), getExpressionString(), e);
			}
			if (e.getMessageCode().equals(SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE)) {
				throw new ValueCoercionException(rootObject.getClass(), getExpressionString(), value, expectedType, e);
			}
			throw new EvaluationException(rootObject.getClass(), getExpressionString(),
					"An ELException occurred setting the value of expression '" + getExpressionString()
							+ "' on context [" + rootObject.getClass() + "] to [" + value + "]", e);
		}
	}

	/**
	 * Invoked every time an evaluation context is created allowing further
	 * initialization from sub-classes.
	 * @deprecated as of 2.4.8, to customize the context, please use the constructor
	 * that accepts an {@link EvaluationContextFactory}.
	 */
	@Deprecated
	protected void extendEvaluationContext(StandardEvaluationContext context) {
	}

	public String toString() {
		return getExpressionString();
	}

}
