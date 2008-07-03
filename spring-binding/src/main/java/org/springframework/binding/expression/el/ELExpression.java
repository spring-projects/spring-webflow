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
package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.util.Assert;

/**
 * Evaluates a parsed EL expression.
 * 
 * @author Jeremy Grelle
 */
public class ELExpression implements Expression {

	private ELContextFactory elContextFactory;

	private ValueExpression valueExpression;

	private boolean template;

	private ConversionService conversionService;

	/**
	 * Creates a new el expression
	 * @param factory the el context factory for creating the EL context that will be used during expression evaluation
	 * @param valueExpression the value expression to evaluate
	 * @param template whether or not this expression is a template expression; if not it was parsed as an implict eval
	 * expression (without delimiters)
	 */
	public ELExpression(ELContextFactory factory, ValueExpression valueExpression, ConversionService conversionService,
			boolean template) {
		Assert.notNull(factory, "The ELContextFactory is required to evaluate EL expressions");
		Assert.notNull(valueExpression, "The EL ValueExpression is required for evaluation");
		Assert.notNull(conversionService, "The ConversionService to perform type coersions is required");
		this.elContextFactory = factory;
		this.valueExpression = valueExpression;
		this.conversionService = conversionService;
		this.template = template;
	}

	public Object getValue(Object context) throws EvaluationException {
		ELContext ctx = elContextFactory.getELContext(context);
		try {
			Object result = valueExpression.getValue(ctx);
			if (result == null && !ctx.isPropertyResolved()) {
				if (getExpressionString().equals("null")) {
					// special case for handling reserved null keyword
					return null;
				} else {
					throw new EvaluationException(context.getClass(), getExpressionString(), "The expression '"
							+ getExpressionString() + "' did not resolve... is the base variable ''"
							+ getBaseVariable() + "' spelled correctly?");
				}
			}
			return result;
		} catch (javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (ELException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"An ELException occurred getting the value for expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "]", e);
		}
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		ELContext ctx = elContextFactory.getELContext(context);
		try {
			Class targetType = getValueType(context);
			if (value != null && targetType != null) {
				ConversionExecutor converter = conversionService.getConversionExecutor(value.getClass(), targetType);
				value = converter.execute(value);
			}
			valueExpression.setValue(ctx, value);
			if (!ctx.isPropertyResolved()) {
				throw new EvaluationException(context.getClass(), getExpressionString(), "The expression '"
						+ getExpressionString() + "' did not resolve... is the base variable ''" + getBaseVariable()
						+ "' spelled correctly?");
			}
		} catch (javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (ELException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"An ELException occurred setting the value of expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "] to [" + value + "]", e);
		}
	}

	public Class getValueType(Object context) {
		ELContext ctx = elContextFactory.getELContext(context);
		try {
			return valueExpression.getType(ctx);
		} catch (javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (ELException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"An ELException occurred getting the value type for expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "]", e);
		}
	}

	public String getExpressionString() {
		if (template) {
			return valueExpression.getExpressionString();
		} else {
			String rawExpressionString = valueExpression.getExpressionString();
			return rawExpressionString.substring("#{".length(), rawExpressionString.length() - 1);
		}
	}

	private String getBaseVariable() {
		String expressionString = getExpressionString();
		int firstDot = expressionString.indexOf('.');
		if (firstDot == -1) {
			return expressionString;
		} else {
			return expressionString.substring(0, firstDot);
		}
	}

	public int hashCode() {
		return valueExpression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof ELExpression)) {
			return false;
		}
		ELExpression other = (ELExpression) o;
		return valueExpression.equals(other.valueExpression);
	}

	public String toString() {
		return getExpressionString();
	}
}