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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
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
@SuppressWarnings("deprecation")
public class SecurityFlowExecutionListener implements FlowExecutionListener {

	private Function<SecurityRule, AuthorizationManager<Object>> authorizationManagerInitializer =
			SecurityRule::getAuthorizationManager;

	private AccessDecisionManager accessDecisionManager;

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

	/**
	 * Get the access decision manager that makes flow authorization decisions.
	 * @return the decision manager
	 * @deprecated in favor of using an {@code AuthorizationManager} by setting
	 * {@link #setAuthorizationManagerInitializer(Function)} instead
	 */
	@Deprecated(since = "3.0.1", forRemoval = true)
	public AccessDecisionManager getAccessDecisionManager() {
		return this.accessDecisionManager;
	}

	/**
	 * Set the access decision manager that makes flow authorization decisions.
	 * @param accessDecisionManager the decision manager to user
	 * @deprecated in favor of using an {@code AuthorizationManager} by setting
	 * {@link #setAuthorizationManagerInitializer(Function)} instead
	 */
	@Deprecated(since = "3.0.1", forRemoval = true)
	public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
		this.accessDecisionManager = accessDecisionManager;
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
	 * Performs a Spring Security authorization decision. Decision will use the provided AccessDecisionManager. If no
	 * AccessDecisionManager is provided a role based manager will be selected according to the comparison type of the
	 * rule.
	 * @param rule the rule to base the decision
	 * @param object the execution listener phase
	 */
	@SuppressWarnings("deprecation")
	protected void decide(SecurityRule rule, Object object) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		AccessDecisionManager decisionManager =
				(this.accessDecisionManager != null ? this.accessDecisionManager : createAccessDecisionManager(rule));

		if (decisionManager != null) {
			accessDecisionManager.decide(authentication, object, getConfigAttributes(rule));
		} else {
			AuthorizationManager<Object> manager = this.authorizationManagerInitializer.apply(rule);
			manager.verify(() -> authentication, object);
		}
	}

	/**
	 * Return an {@link AccessDecisionManager} for the SecurityRule.
	 * <p>By default, returns {@code null} in which case an
	 * {@link AuthorizationManager} is used instead of {@code AccessDecisionManager}.
	 * @param rule the rule to check
	 * @return the manager to use, or {@code null}
	 * @deprecated in favor of using an {@code AuthorizationManager} by setting
	 * {@link #setAuthorizationManagerInitializer(Function)} instead
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated(since = "3.0.1", forRemoval = true)
	protected AccessDecisionManager createAccessDecisionManager(SecurityRule rule) {
		return null;
	}

	/**
	 * Convert SecurityRule into a form understood by Spring Security
	 * @param rule the rule to convert
	 * @return list of ConfigAttributes for Spring Security
	 * @deprecated in favor of using an {@code AuthorizationManager} by setting
	 * {@link #setAuthorizationManagerInitializer(Function)} instead
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated(since = "3.0.1", forRemoval = true)
	protected Collection<ConfigAttribute> getConfigAttributes(SecurityRule rule) {
		List<ConfigAttribute> configAttributes = new ArrayList<>();
		for (String attribute : rule.getAttributes()) {
			configAttributes.add(new SecurityConfig(attribute));
		}
		return configAttributes;
	}
}
