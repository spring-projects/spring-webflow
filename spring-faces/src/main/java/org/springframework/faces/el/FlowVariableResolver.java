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

/**
 * Custom variable resolver that resolves the current FlowExecution object for binding expressions prefixed with
 * {@link #FLOW_SCOPE_VARIABLE}. For instance "flowScope.myBean.myProperty". Designed to be used in conjunction with
 * {@link FlowPropertyResolver} only.
 * 
 * This class is the original flow execution variable resolver implementation introduced in Spring Web Flow's JSF
 * support available since 1.0. In general, prefer use of {@link DelegatingFlowVariableResolver} or
 * {@link FlowExecutionVariableResolver} to this implementation as they are both considerably more flexible.
 * 
 * This resolver should only be used with the {@link FlowPropertyResolver} which can only resolve flow-scoped variables.
 * May be deprecated in a future release of Spring Web Flow.
 * 
 * @author Colin Sampaleanu
 */
public class FlowVariableResolver extends VariableResolver {

	/**
	 * Name of the exposed flow scope variable ("flowScope").
	 */
	public static final String FLOW_SCOPE_VARIABLE = "flowScope";

	/**
	 * The standard variable resolver to delegate to if this one doesn't apply.
	 */
	private VariableResolver resolverDelegate;

	/**
	 * Create a new FlowVariableResolver, using the given original VariableResolver.
	 * <p>
	 * A JSF implementation will automatically pass its original resolver into the constructor of a configured resolver,
	 * provided that there is a corresponding constructor argument.
	 * 
	 * @param resolverDelegate the original VariableResolver
	 */
	public FlowVariableResolver(VariableResolver resolverDelegate) {
		this.resolverDelegate = resolverDelegate;
	}

	/**
	 * Return the original VariableResolver that this resolver delegates to.
	 */
	protected final VariableResolver getResolverDelegate() {
		return resolverDelegate;
	}

	public Object resolveVariable(FacesContext context, String name) throws EvaluationException {
		if (FLOW_SCOPE_VARIABLE.equals(name)) {
			return FlowExecutionHolderUtils.getRequiredCurrentFlowExecution(context);
		} else {
			return resolverDelegate.resolveVariable(context, name);
		}
	}
}