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
package org.springframework.binding.expression.ognl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.expression.SetValueAttempt;

/**
 * Evaluates a parsed Ognl expression.
 * 
 * @author Keith Donald
 */
class OgnlExpression implements Expression {

	/**
	 * The expression.
	 */
	private Object expression;

	/**
	 * Expression variable initial values.
	 */
	private Map variableExpressions;

	/**
	 * The expected type of object returned from evaluating the expression.
	 */
	private Class expectedResultType;

	/**
	 * The original expression string.
	 */
	private String expressionString;

	/**
	 * Creates a new OGNL expression.
	 */
	public OgnlExpression(Object expression, Map variableExpressions, Class expectedResultType, String expressionString) {
		this.expression = expression;
		this.variableExpressions = variableExpressions;
		this.expectedResultType = expectedResultType;
		this.expressionString = expressionString;
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

	public Object getValue(Object context) throws EvaluationException {
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			return Ognl.getValue(expression, evaluationContext, context, expectedResultType);
		} catch (NoSuchPropertyException e) {
			throw new PropertyNotFoundException(new EvaluationAttempt(this, context), e);
		} catch (OgnlException e) {
			throw new EvaluationException(new EvaluationAttempt(this, context), e);
		}
	}

	public void setValue(Object context, Object value) {
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			Ognl.setValue(expression, evaluationContext, context, value);
		} catch (NoSuchPropertyException e) {
			throw new PropertyNotFoundException(new SetValueAttempt(this, context, value), e);
		} catch (OgnlException e) {
			throw new EvaluationException(new SetValueAttempt(this, context, value), e);
		}
	}

	public Class getValueType(Object context) {
		try {
			// OGNL has no native way to get this information
			return new BeanWrapperImpl(context).getPropertyDescriptor(expressionString).getPropertyType();
		} catch (InvalidPropertyException e) {
			throw new PropertyNotFoundException(new EvaluationAttempt(this, context), e);
		} catch (BeansException e) {
			throw new EvaluationException(new EvaluationAttempt(this, context), e);
		}
	}

	public String getExpressionString() {
		return expressionString;
	}

	private Map getVariables(Object context) {
		if (variableExpressions == null) {
			return Collections.EMPTY_MAP;
		}
		Map variables = new HashMap(variableExpressions.size(), 1);
		for (Iterator it = variableExpressions.entrySet().iterator(); it.hasNext();) {
			Map.Entry var = (Map.Entry) it.next();
			Expression valueExpression = (Expression) var.getValue();
			variables.put(var.getKey(), valueExpression.getValue(context));
		}
		return variables;
	}

	public String toString() {
		return expression.toString();
	}
}