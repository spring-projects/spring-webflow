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
package org.springframework.webflow.action;

import org.springframework.binding.expression.Expression;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.ActionExecutor;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action that evaluates an expression and optionally exposes its result.
 * <p>
 * Delegates to a {@link ResultEventFactory} to determine how to map the evaluation result to an action outcome
 * {@link Event}.
 * 
 * @see Expression
 * @see ActionResultExposer
 * @see ResultEventFactory
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class EvaluateAction extends AbstractAction {

	/**
	 * The expression to evaluate when this action is invoked. Required.
	 */
	private Expression expression;

	/**
	 * The helper that will expose the expression evaluation result. Optional.
	 */
	private ActionResultExposer evaluationResultExposer;

	/**
	 * The selector for the factory that will create the action result event callers can respond to.
	 */
	private ResultEventFactory resultEventFactory;

	/**
	 * Create a new evaluate action.
	 * @param expression the expression to evaluate (required)
	 * @param evaluationResultExposer the strategy for how the expression result will be exposed to the flow (optional)
	 */
	public EvaluateAction(Expression expression, ActionResultExposer evaluationResultExposer) {
		init(expression, evaluationResultExposer, null);
	}

	/**
	 * Create a new evaluate action.
	 * @param expression the expression to evaluate (required)
	 * @param evaluationResultExposer the strategy for how the expression result will be exposed to the flow (optional)
	 * @param resultEventFactory the factory that will map the evaluation result to a Web Flow event (optional)
	 */
	public EvaluateAction(Expression expression, ActionResultExposer evaluationResultExposer,
			ResultEventFactory resultEventFactory) {
		init(expression, evaluationResultExposer, resultEventFactory);
	}

	protected Event doExecute(RequestContext context) throws Exception {
		Object result = expression.getValue(context);
		if (result instanceof Action) {
			return ActionExecutor.execute((Action) result, context);
		} else {
			if (evaluationResultExposer != null) {
				evaluationResultExposer.exposeResult(result, context);
			}
			return resultEventFactory.createResultEvent(this, result, context);
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("expression", expression).append("resultExposer",
				evaluationResultExposer).toString();
	}

	// internal helpers

	private void init(Expression expression, ActionResultExposer evaluationResultExposer,
			ResultEventFactory resultEventFactory) {
		Assert.notNull(expression, "The expression this action should evaluate is required");
		this.expression = expression;
		this.evaluationResultExposer = evaluationResultExposer;
		this.resultEventFactory = resultEventFactory != null ? resultEventFactory : new DefaultResultEventFactory();
	}

	/**
	 * Default implementation that uses the ResultEventFactorySelector helper.
	 * @author Keith Donald
	 */
	private class DefaultResultEventFactory implements ResultEventFactory {

		private ResultEventFactorySelector selector = new ResultEventFactorySelector();

		public Event createResultEvent(Object source, Object resultObject, RequestContext context) {
			return selector.forResult(resultObject).createResultEvent(source, resultObject, context);
		}
	}

}