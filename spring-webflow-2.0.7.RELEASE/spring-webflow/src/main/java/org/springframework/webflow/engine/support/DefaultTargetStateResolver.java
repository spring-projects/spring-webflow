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
package org.springframework.webflow.engine.support;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.util.Assert;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.execution.RequestContext;

/**
 * A transition target state resolver that evaluates an expression to resolve the target state. The default
 * implementation.
 * 
 * @author Keith Donald
 */
public class DefaultTargetStateResolver implements TargetStateResolver {

	/**
	 * The expression for the target state identifier.
	 */
	private Expression targetStateIdExpression;

	/**
	 * Creates a new target state resolver that always returns the same target state id.
	 * @param targetStateId a static target target state
	 */
	public DefaultTargetStateResolver(String targetStateId) {
		this(new StaticExpression(targetStateId));
	}

	/**
	 * Creates a new target state resolver.
	 * @param targetStateIdExpression the target state expression
	 */
	public DefaultTargetStateResolver(Expression targetStateIdExpression) {
		Assert.notNull(targetStateIdExpression, "The target state id expression is required");
		this.targetStateIdExpression = targetStateIdExpression;
	}

	public State resolveTargetState(Transition transition, State sourceState, RequestContext context) {
		String targetStateId = (String) targetStateIdExpression.getValue(context);
		if (targetStateId != null) {
			return ((Flow) context.getActiveFlow()).getStateInstance(targetStateId);
		} else {
			return null;
		}
	}

	public String toString() {
		return targetStateIdExpression.toString();
	}
}