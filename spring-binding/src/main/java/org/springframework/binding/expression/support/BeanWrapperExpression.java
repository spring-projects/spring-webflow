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
package org.springframework.binding.expression.support;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationContext;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.SetValueAttempt;
import org.springframework.binding.expression.SettableExpression;
import org.springframework.util.Assert;

/**
 * An expression evaluator that uses the Spring bean wrapper.
 * 
 * @author Keith Donald
 */
class BeanWrapperExpression implements SettableExpression {

	/**
	 * The expression.
	 */
	private String expression;

	public BeanWrapperExpression(String expression) {
		this.expression = expression;
	}

	public int hashCode() {
		return expression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof BeanWrapperExpression)) {
			return false;
		}
		BeanWrapperExpression other = (BeanWrapperExpression)o;
		return expression.equals(other.expression);
	}

	public Object evaluate(Object target, EvaluationContext context) throws EvaluationException {
		try {
			return new BeanWrapperImpl(target).getPropertyValue(expression);
		}
		catch (BeansException e) {
			throw new EvaluationException(new EvaluationAttempt(this, target, context), e);
		}
	}

	public void evaluateToSet(Object target, Object value, EvaluationContext context) throws EvaluationException {
		try {
			Assert.notNull(target, "The target object to evaluate is required");
			new BeanWrapperImpl(target).setPropertyValue(expression, value);
		}
		catch (BeansException e) {
			throw new EvaluationException(new SetValueAttempt(this, target, value, context), e);
		}
	}

	public String toString() {
		return expression;
	}
}