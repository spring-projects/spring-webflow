/*
 * Copyright 2004-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.security;

import java.util.function.Function;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.EnterStateVetoException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;

/**
 * Flow security integration with Spring Security
 * 
 * @author Scott Andrews
 * @author Rossen Stoyanchev
 */
public class SecurityFlowExecutionListener implements FlowExecutionListener {

	private Function<SecurityRule, AuthorizationManager<Object>> authorizationManagerInitializer =
			SecurityRule::getAuthorizationManager;

	/**
	 * Provide a function that determines the {@link AuthorizationManager} to use
	 * for a given {@link SecurityRule}.
	 * <p>By default, {@link SecurityRule#getAuthorizationManager()} is used.
	 * @param initializer the function to use
	 * @since 3.0.1
	 */
	public void setAuthorizationManagerInitializer(Function<SecurityRule, AuthorizationManager<Object>> initializer) {
		Assert.notNull(initializer, "'initializer' is required");
		this.authorizationManagerInitializer = initializer;
	}

	public void sessionCreating(RequestContext context, FlowDefinition definition) {
		SecurityRule rule = (SecurityRule) definition.getAttributes().get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		if (rule != null) {
			decide(rule, definition);
		}
	}

	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
		SecurityRule rule = (SecurityRule) state.getAttributes().get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		if (rule != null) {
			decide(rule, state);
		}
	}

	public void transitionExecuting(RequestContext context, TransitionDefinition transition) {
		SecurityRule rule = (SecurityRule) transition.getAttributes().get(SecurityRule.SECURITY_ATTRIBUTE_NAME);
		if (rule != null) {
			decide(rule, transition);
		}
	}

	/**
	 * Performs a Spring Security authorization decision. Decision will use a role based manager
	 * selected according to the comparison type of the rule.
	 * @param rule the rule to base the decision
	 * @param object the execution listener phase
	 */
	protected void decide(SecurityRule rule, Object object) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		AuthorizationManager<Object> manager = this.authorizationManagerInitializer.apply(rule);
		manager.verify(() -> authentication, object);
	}

}
