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
package org.springframework.faces.el;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.springframework.faces.webflow.FlowExecutionHolderUtils;
import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom variable resolver that resolves to a thread-bound FlowExecution object for binding expressions prefixed with a
 * {@link #FLOW_EXECUTION_VARIABLE_NAME}. For instance "flowExecution.conversationScope.myProperty".
 * 
 * This class is designed to be used with a {@link FlowExecutionPropertyResolver}.
 * 
 * This class is a more flexible alternative to the {@link FlowVariableResolver} which is expected to be used ONLY with
 * a {@link FlowPropertyResolver} to resolve flow scope variables ONLY. It is more flexible because it provides access
 * to any scope structure of a {@link FlowExecution} object.
 * 
 * @author Keith Donald
 */
public class FlowExecutionVariableResolver extends VariableResolver {

	/**
	 * Name of the flow execution variable.
	 */
	public static final String FLOW_EXECUTION_VARIABLE_NAME = "flowExecution";

	/**
	 * The standard variable resolver to delegate to if this one doesn't apply.
	 */
	private VariableResolver resolverDelegate;

	/**
	 * Creates a new flow executon variable resolver that resolves the current FlowExecution object.
	 * @param resolverDelegate the resolver to delegate to when the variable is not named "flowExecution".
	 */
	public FlowExecutionVariableResolver(VariableResolver resolverDelegate) {
		this.resolverDelegate = resolverDelegate;
	}

	/**
	 * Returns the variable resolver this resolver delegates to if necessary.
	 */
	protected final VariableResolver getResolverDelegate() {
		return resolverDelegate;
	}

	public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
		if (FLOW_EXECUTION_VARIABLE_NAME.equals(name)) {
			return FlowExecutionHolderUtils.getRequiredCurrentFlowExecution(context);
		} else {
			return resolverDelegate.resolveVariable(context, name);
		}
	}
}