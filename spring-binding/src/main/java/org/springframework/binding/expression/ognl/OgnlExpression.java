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
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionVariable;
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
	private Map variableMap;;

	/**
	 * Creates a new OGNL expression.
	 * @param expression the parsed expression
	 */
	public OgnlExpression(Object expression, ExpressionVariable[] variables) {
		init(expression, variables);
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
			return Ognl.getValue(expression, variableMap, context);
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
			Ognl.setValue(expression, variableMap, context, value);
		} catch (OgnlException e) {
			throw new EvaluationException(new SetValueAttempt(this, context, value), e);
		}
	}

	private void init(Object expression, ExpressionVariable[] variables) {
		this.expression = expression;
		variableMap = createVariableMap(variables);
	}

	private Map createVariableMap(ExpressionVariable[] variables) {
		if (variables != null && variables.length > 0) {
			Map variableMap = new HashMap(variables.length);
			for (int i = 0; i < variables.length; i++) {
				ExpressionVariable var = variables[i];
				variableMap.put(var.getName(), var.getValue());
			}
			return variableMap;
		} else {
			return Collections.EMPTY_MAP;
		}
	}

	public String toString() {
		return expression.toString();
	}
}