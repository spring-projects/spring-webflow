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
 * A simple holder for information about an expression evaluation attempt.
 * 
 * @author Keith Donald
 */
public class EvaluationAttempt {

	private Expression expression;
	private Object context;

	/**
	 * Create an evaluation attempt.
	 * @param expression the expression that failed to evaluate
	 * @param context the context of the expression evaluation
	 */
	public EvaluationAttempt(Expression expression, Object context) {
		this.expression = expression;
		this.context = context;
	}

	/**
	 * Returns the expression that attempted an evaluation.
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Returns the context object in which expression evaluation was attempted.
	 */
	public Object getContext() {
		return context;
	}

	public String toString() {
		return createToString(new ToStringCreator(this)).toString();
	}

	protected ToStringCreator createToString(ToStringCreator creator) {
		return creator.append("expression", expression).append("context", context);
	}
}