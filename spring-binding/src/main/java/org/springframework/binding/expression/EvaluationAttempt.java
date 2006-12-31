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
package org.springframework.binding.expression;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

/**
 * A simple holder for information about an evaluation attempt.
 * 
 * @author Keith Donald
 */
public class EvaluationAttempt implements Serializable {

	/**
	 * The expression that attempted to evaluate.
	 */
	private Expression expression;

	/**
	 * The target object being evaluated.
	 */
	private Object target;

	/**
	 * The evaluation context.
	 */
	private EvaluationContext context;

	/**
	 * Create an evaluation attempt.
	 * @param expression the expression that failed to evaluate
	 * @param target the target of the expression
	 * @param context the context attributes that might have affected evaluation behavior
	 */
	public EvaluationAttempt(Expression expression, Object target, EvaluationContext context) {
		this.expression = expression;
		this.target = target;
		this.context = context;
	}

	/**
	 * Returns the expression that attempted to evaluate.
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Returns the target object upon which evaluation was attempted.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Returns context attributes that may have influenced the evaluation process.
	 */
	public EvaluationContext getContext() {
		return context;
	}

	public String toString() {
		return createToString(new ToStringCreator(this)).toString();
	}

	protected ToStringCreator createToString(ToStringCreator creator) {
		return creator.append("expression", expression).append("target", target).append("context",
				context);
	}
}