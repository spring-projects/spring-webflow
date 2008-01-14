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
 * <p>
 * IMPLEMENTATION NOTE: Ognl 2.6.7 expression objects do not respect equality properly, so the equality operations
 * defined within this class do not function properly.
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
	private ExpressionVariable[] variables;

	/**
	 * Creates a new OGNL expression.
	 * @param expression the parsed expression
	 */
	public OgnlExpression(Object expression, ExpressionVariable[] variables) {
		this.expression = expression;
		this.variables = variables;
	}

	public int hashCode() {
		return expression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof OgnlExpression)) {
			return false;
		}
		// as late as Ognl 2.6.7, their expression objects don't implement equals
		// so this always returns false
		OgnlExpression other = (OgnlExpression) o;
		return expression.equals(other.expression);
	}

	public Object getValue(Object target) throws EvaluationException {
		Assert.notNull(target, "The target object to evaluate is required");
		try {
			return Ognl.getValue(expression, getContext(), target);
		} catch (OgnlException e) {
			if (e.getReason() != null && e.getReason() != e) {
				// unwrap the OgnlException since the actual exception is wrapped inside it
				// and there is not generic (getCause) way to get to it later on
				throw new EvaluationException(new EvaluationAttempt(this, target), e.getReason());
			} else {
				throw new EvaluationException(new EvaluationAttempt(this, target), e);
			}
		}
	}

	public void setValue(Object target, Object value) {
		Assert.notNull(target, "The target object to evaluate is required");
		try {
			// TODO context map
			Ognl.setValue(expression, getContext(), target, value);
		} catch (OgnlException e) {
			throw new EvaluationException(new SetValueAttempt(this, target, value), e);
		}
	}

	private Map getContext() {
		if (variables != null && variables.length > 0) {
			return new HashMap(variables.length);
		} else {
			return Collections.EMPTY_MAP;
		}
	}

	public String toString() {
		return expression.toString();
	}
}