/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.binding.expression.ognl;

import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.TypeConverter;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.binding.expression.spel.SpringELExpression;

/**
 * Evaluates a parsed Ognl expression.
 *
 * @author Keith Donald
 * @author Scott Andrews
 *
 * @deprecated in favor of Spring EL, see {@link SpringELExpression}.
 */
class OgnlExpression implements Expression {

	private Object expression;

	private Map<String, Expression> variableExpressions;

	private Class<?> expectedResultType;

	private String expressionString;

	private ConversionService conversionService;

	/**
	 * Creates a new OGNL expression.
	 */
	public OgnlExpression(Object expression, Map<String, Expression> variableExpressions, Class<?> expectedResultType,
			String expressionString, ConversionService conversionService) {
		this.expression = expression;
		this.variableExpressions = variableExpressions;
		this.expectedResultType = expectedResultType;
		this.expressionString = expressionString;
		this.conversionService = conversionService;
	}

	public boolean equals(Object o) {
		if (!(o instanceof OgnlExpression)) {
			return false;
		}
		OgnlExpression other = (OgnlExpression) o;
		return expressionString.equals(other.expressionString);
	}

	public int hashCode() {
		return expressionString.hashCode();
	}

	@SuppressWarnings("rawtypes")
	public Object getValue(Object context) throws EvaluationException {
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			Ognl.setTypeConverter(evaluationContext, createTypeConverter());
			return Ognl.getValue(expression, evaluationContext, context, expectedResultType);
		} catch (NoSuchPropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (OgnlException e) {
			if (e.getReason() instanceof ValueCoercionException) {
				throw (ValueCoercionException) e.getReason();
			} else {
				throw new EvaluationException(context.getClass(), getExpressionString(),
						"An OgnlException occurred getting the value for expression '" + getExpressionString()
								+ "' on context [" + context.getClass() + "]", causeFor(e));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void setValue(Object context, Object value) {
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			Ognl.setTypeConverter(evaluationContext, createTypeConverter());
			Ognl.setValue(expression, evaluationContext, context, value);
		} catch (NoSuchPropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (OgnlException e) {
			if (e.getReason() instanceof ValueCoercionException) {
				throw (ValueCoercionException) e.getReason();
			} else {
				throw new EvaluationException(context.getClass(), getExpressionString(),
						"An OgnlException occurred setting the value of expression '" + getExpressionString()
								+ "' on context [" + context.getClass() + "] to [" + value + "]", causeFor(e));
			}
		}
	}

	public Class<?> getValueType(Object context) {
		try {
			// OGNL has no native way to get this information
			return new BeanWrapperImpl(context).getPropertyType(expressionString);
		} catch (InvalidPropertyException e) {
			throw new PropertyNotFoundException(context.getClass(), getExpressionString(), e);
		} catch (BeansException e) {
			throw new EvaluationException(context.getClass(), getExpressionString(),
					"An BeansException occurred getting the value type for expression '" + getExpressionString()
							+ "' on context [" + context.getClass() + "]", e);
		}
	}

	public String getExpressionString() {
		return expressionString;
	}

	// internal helpers

	private Throwable causeFor(OgnlException e) {
		if (e.getReason() != null) {
			if (e.getCause() == null) {
				try {
					e.initCause(e.getReason());
				} catch (IllegalStateException ex) {
					// we tried
				}
			}
			return e;
		} else {
			return e;
		}
	}

	@SuppressWarnings("rawtypes")
	private TypeConverter createTypeConverter() {
		return new TypeConverter() {
			public Object convertValue(Map context, Object target, Member member, String propertyName, Object value,
					Class toType) throws ValueCoercionException {
				try {
					return conversionService.executeConversion(value, toType);
				} catch (ConversionException e) {
					throw new ValueCoercionException(context.getClass(), expressionString, value, toType, e);
				}
			}
		};
	}

	private Map<String, Object> getVariables(Object context) {
		if (variableExpressions == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> variables = new HashMap<String, Object>(variableExpressions.size(), 1);
		for (Map.Entry<String, Expression> var : variableExpressions.entrySet()) {
			Expression valueExpression = var.getValue();
			variables.put(var.getKey(), valueExpression.getValue(context));
		}
		return variables;
	}

	public String toString() {
		return expressionString;
	}
}
