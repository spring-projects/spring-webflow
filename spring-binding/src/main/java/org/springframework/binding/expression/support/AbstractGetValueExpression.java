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

/**
 * Trivial helper for concrete expression types that do not support setting their values. Simply throws an unsupported
 * operation exception if {@link #setValue(Object, Object)} is called.
 * 
 * Subclasses must implement {@link #getValue(Object)}.
 * 
 * @author Keith Donald
 */
public abstract class AbstractGetValueExpression implements Expression {

	public abstract Object getValue(Object context) throws EvaluationException;

	public void setValue(Object context, Object value) throws EvaluationException {
		throw new UnsupportedOperationException("Setting this expression's value is not supported");
	}

	public Class getValueType(Object context) {
		return null;
	}

	public String getExpressionString() {
		return null;
	}

}
