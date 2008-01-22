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
package org.springframework.webflow.engine.support;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.execution.RequestContext;

/**
 * Transition criteria that tests the value of an expression. The expression is used to express a condition that guards
 * transition execution in a web flow. Expressions will be evaluated agains the request context and should return a
 * boolean result.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class BooleanExpressionTransitionCriteria implements TransitionCriteria {

	/**
	 * The expression evaluator to use.
	 */
	private Expression booleanExpression;

	/**
	 * Create a new expression based transition criteria object.
	 * @param booleanExpression the expression evaluator testing the criteria, this expression should be a condition
	 * that returns a Boolean value
	 */
	public BooleanExpressionTransitionCriteria(Expression booleanExpression) {
		Assert.notNull(booleanExpression, "The expression to test is required");
		this.booleanExpression = booleanExpression;
	}

	public boolean test(RequestContext context) {
		Object result = booleanExpression.getValue(context);
		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		} else {
			return context.getLastEvent().getId().equals(String.valueOf(result));
		}
	}

	public String toString() {
		return booleanExpression.toString();
	}
}