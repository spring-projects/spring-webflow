/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.binding.expression.support;

import java.util.Collections;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationContext;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.SetValueAttempt;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.util.Assert;

/**
 * Evaluates a parsed Ognl expression.
 * <p>
 * IMPLEMENTATION NOTE: Ognl 2.6.7 expression objects do not respect equality
 * properly, so the equality operations defined within this class do not
 * function properly.
 * 
 * @author Keith Donald
 */
class OgnlExpression implements SettableExpression {

	/**
	 * The expression.
	 */
	private Object expression;

	/**
	 * Creates a new OGNL expression.
	 * @param expression the parsed expression
	 */
	public OgnlExpression(Object expression) {
		this.expression = expression;
	}

	public int hashCode() {
		return expression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof OgnlExpression)) {
			return false;
		}
		// as late as Ognl 2.6.7, their expression objects don't implement
		// equals
		// so this always returns false
		OgnlExpression other = (OgnlExpression) o;
		return expression.equals(other.expression);
	}

	public Object evaluate(Object target, EvaluationContext context) throws EvaluationException {
		Assert.notNull(target, "The target object to evaluate is required");
		Map contextAttributes = (context != null ? context.getAttributes() : Collections.EMPTY_MAP);
		try {
			return Ognl.getValue(expression, contextAttributes, target);
		}
		catch (OgnlException e) {
			throw new EvaluationException(new EvaluationAttempt(this, target, context), e);
		}
	}

	public void evaluateToSet(Object target, Object value, EvaluationContext context) {
		Assert.notNull(target, "The target object to evaluate is required");
		Map contextAttributes = (context != null ? context.getAttributes() : Collections.EMPTY_MAP);
		try {
			Ognl.setValue(expression, contextAttributes, target, value);
		}
		catch (OgnlException e) {
			throw new EvaluationException(new SetValueAttempt(this, target, value, context), e);
		}
	}

	public String toString() {
		return expression.toString();
	}
}