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
package org.springframework.binding.expression;

import org.springframework.core.style.ToStringCreator;

/**
 * Records an attempt to set an expression value.
 * 
 * @author Keith Donald
 */
public class SetValueAttempt extends EvaluationAttempt {

	/**
	 * The new value.
	 */
	private Object value;

	/**
	 * Creates a new set attempt.
	 * @param expression the settable expression
	 * @param context the target of the expression
	 * @param value the value that was attempted to be set
	 */
	public SetValueAttempt(Expression expression, Object context, Object value) {
		super(expression, context);
		this.value = value;
	}

	/**
	 * Returns the value that was attempted to be set.
	 */
	public Object getValue() {
		return value;
	}

	protected ToStringCreator createToString(ToStringCreator creator) {
		return super.createToString(creator).append("value", value);
	}
}