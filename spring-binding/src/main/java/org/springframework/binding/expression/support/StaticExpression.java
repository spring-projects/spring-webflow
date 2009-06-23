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
import org.springframework.util.ObjectUtils;

/**
 * A simple expression evaluator that just returns a fixed result on each evaluation.
 * 
 * @author Keith Donald
 */
public final class StaticExpression implements Expression {

	private Object value;

	private String expressionString;

	/**
	 * Create a static evaluator for the given value.
	 * @param value the value
	 */
	public StaticExpression(Object value) {
		this.value = value;
	}

	public int hashCode() {
		if (value == null) {
			return 0;
		} else {
			return value.hashCode();
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof StaticExpression)) {
			return false;
		}
		StaticExpression other = (StaticExpression) o;
		return ObjectUtils.nullSafeEquals(value, other.value);
	}

	public Object getValue(Object context) throws EvaluationException {
		return value;
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		this.value = value;
	}

	public Class getValueType(Object context) {
		return value.getClass();
	}

	public String getExpressionString() {
		return expressionString;
	}

	/**
	 * Sets the static expression string.
	 * @param expressionString the static expression string
	 */
	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
	}

	public String toString() {
		return String.valueOf(value);
	}
}