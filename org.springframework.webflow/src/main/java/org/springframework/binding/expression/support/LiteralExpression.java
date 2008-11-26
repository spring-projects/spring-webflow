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
import org.springframework.util.Assert;

public class LiteralExpression implements Expression {

	/**
	 * The string literal.
	 */
	private String literal;

	/**
	 * Create a literal expression for the given literal.
	 * @param literal the literal
	 */
	public LiteralExpression(String literal) {
		Assert.notNull(literal, "The literal is required");
		this.literal = literal;
	}

	public int hashCode() {
		return literal.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof LiteralExpression)) {
			return false;
		}
		LiteralExpression other = (LiteralExpression) o;
		return literal.equals(other.literal);
	}

	public Object getValue(Object context) throws EvaluationException {
		return literal;
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		throw new UnsupportedOperationException(
				"Cannot set a literal expression value.  Are you attempting to set a property expression?  "
						+ "If so, should the expression string be enclosed in eval delimiters?");
	}

	public Class getValueType(Object context) {
		return String.class;
	}

	public String getExpressionString() {
		return null;
	}

	public String toString() {
		return literal;
	}
}
