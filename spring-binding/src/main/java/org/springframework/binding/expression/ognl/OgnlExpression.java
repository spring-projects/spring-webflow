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
package org.springframework.binding.expression.ognl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.SetValueAttempt;
import org.springframework.util.Assert;

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

	public int hashCode() {
		return expression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof OgnlExpression)) {
			return false;
		}
		OgnlExpression other = (OgnlExpression) o;
		return expression.equals(other.expression);
	}

	public Object getValue(Object context) throws EvaluationException {
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			return Ognl.getValue(expression, evaluationContext, context, expectedResultType);
		} catch (OgnlException e) {
			if (e.getReason() != null && e.getReason() != e) {
				// unwrap the OgnlException since the actual exception is wrapped inside it
				// and there is not generic (getCause) way to get to it later on
				throw new EvaluationException(new EvaluationAttempt(this, context), e.getReason());
			} else {
				throw new EvaluationException(new EvaluationAttempt(this, context), e);
			}
		}
	}

	public void setValue(Object context, Object value) {
		Assert.notNull(context, "The context to set the provided value in is required");
		try {
			Map evaluationContext = Ognl.addDefaultContext(context, getVariables(context));
			Ognl.setValue(expression, evaluationContext, context, value);
		} catch (OgnlException e) {
			throw new EvaluationException(new SetValueAttempt(this, context, value), e);
		}
	}

	public Class getValueType(Object context) {
		try {
			if (Ognl.isSimpleProperty(expression)) {
				// TODO
				throw new UnsupportedOperationException("Not yet implemented - in progress");
			} else {
				return null;
			}
		} catch (OgnlException e) {
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