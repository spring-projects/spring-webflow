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
package org.springframework.binding.expression.support;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;

/**
 * Evaluates an array of expressions to build a concatenated string.
 * 
 * @author Keith Donald
 */
public class CompositeStringExpression implements Expression {

	/**
	 * The expression array.
	 */
	private Expression[] expressions;

	/**
	 * Creates a new composite string expression.
	 * @param expressions the ordered set of expressions that when evaluated will have their results stringed together
	 * to build the composite string
	 */
	public CompositeStringExpression(Expression[] expressions) {
		this.expressions = expressions;
	}

	public Object getValue(Object context) throws EvaluationException {
		StringBuffer buffer = new StringBuffer(128);
		for (int i = 0; i < expressions.length; i++) {
			buffer.append(expressions[i].getValue(context));
		}
		return buffer.toString();
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		throw new UnsupportedOperationException("Cannot set a composite string expression value");
	}

	public Class getValueType(Object context) {
		return String.class;
	}

	public String getExpressionString() {
		return null;
	}

	public String toString() {
		return new ToStringCreator(this).append("expressions", expressions).toString();
	}
}