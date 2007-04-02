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
package org.springframework.webflow.executor.jsf;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom variable resolver that searches the current flow execution for variables to resolve.
 * The search algorithm looks in flash scope first, then flow scope, then conversation scope.
 * If no variable is found, this resolver delegates to the next resolver in the chain.
 * 
 * Suitable for use along side other variable resolvers to support EL binding expressions like
 * {#bean.property} where "bean" could be a property in any supported scope.
 * 
 * @author Keith Donald
 */
public class DelegatingFlowVariableResolver extends VariableResolver {

	/**
	 * The standard variable resolver to delegate to if this one doesn't apply.
	 */
	private VariableResolver resolverDelegate;

	/**
	 * Create a new FlowExecutionVariableResolver, using the given original VariableResolver.
	 * <p>
	 * A JSF implementation will automatically pass its original resolver into the constructor of a configured resolver,
	 * provided that there is a corresponding constructor argument.
	 * 
	 * @param resolverDelegate the original VariableResolver
	 */
	public DelegatingFlowVariableResolver(VariableResolver resolverDelegate) {
		this.resolverDelegate = resolverDelegate;
	}

	/**
	 * Return the original VariableResolver that this resolver delegates to.
	 */
	protected final VariableResolver getResolverDelegate() {
		return resolverDelegate;
	}

	/**
	 * Check flash/flow/conversation scope. If the variable is not found, delegate to next variable resolver in the
	 * chain.
	 */
	public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
		FlowExecution execution = FlowExecutionHolderUtils.getRequiredCurrentFlowExecution(context);
		// try flash/flow/conversation
		if (execution.getActiveSession().getFlashMap().contains(name)) {
			return execution.getActiveSession().getFlashMap().get(name);
		}
		else if (execution.getActiveSession().getScope().contains(name)) {
			return execution.getActiveSession().getScope().get(name);
		}
		else if (execution.getConversationScope().contains(name)) {
			return execution.getConversationScope().get(name);
		}
		else {
			// neither found a value, delegate to next resolver in the chain
			return resolverDelegate.resolveVariable(context, name);
		}
	}
}