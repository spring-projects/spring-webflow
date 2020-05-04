/*
 * Copyright 2004-2020 the original author or authors.
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;
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
 */
public class SecurityFlowExecutionListener implements FlowExecutionListener {

	private static final boolean SPRING_SECURITY_3_PRESENT = ClassUtils.hasConstructor(AffirmativeBased.class);

	private AccessDecisionManager accessDecisionManager;

	/**
	 * Get the access decision manager that makes flow authorization decisions.
	 * @return the decision manager
	 */
	public AccessDecisionManager getAccessDecisionManager() {
		return this.accessDecisionManager;
	}

	/**
	 * Set the access decision manager that makes flow authorization decisions.
	 * @param accessDecisionManager the decision manager to user
	 */
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
	protected void decide(SecurityRule rule, Object object) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<ConfigAttribute> configAttributes = getConfigAttributes(rule);
		if (accessDecisionManager != null) {
			accessDecisionManager.decide(authentication, object, configAttributes);
		} else {
			AccessDecisionManager manager = (SPRING_SECURITY_3_PRESENT ?
					createManagerWithSpringSecurity3(rule) : createManager(rule));
			manager.decide(authentication, object, configAttributes);
		}
	}

	private AbstractAccessDecisionManager createManager(SecurityRule rule) {
		List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
		voters.add(new RoleVoter());
		if (rule.getComparisonType() == SecurityRule.COMPARISON_ANY) {
			return new AffirmativeBased(voters);
		} else if (rule.getComparisonType() == SecurityRule.COMPARISON_ALL) {
			return new UnanimousBased(voters);
		} else {
			throw new IllegalStateException("Unknown SecurityRule match type: " + rule.getComparisonType());
		}
	}

	private AbstractAccessDecisionManager createManagerWithSpringSecurity3(SecurityRule rule) {
		List<AccessDecisionVoter> voters = new ArrayList<>();
		voters.add(new RoleVoter());
		Class<?> managerType;
		if (rule.getComparisonType() == SecurityRule.COMPARISON_ANY) {
			managerType = AffirmativeBased.class;
		} else if (rule.getComparisonType() == SecurityRule.COMPARISON_ALL) {
			managerType = UnanimousBased.class;
		} else {
			throw new IllegalStateException("Unknown SecurityRule match type: " + rule.getComparisonType());
		}
		try {
			Constructor<?> constructor = managerType.getConstructor();
			AbstractAccessDecisionManager manager = (AbstractAccessDecisionManager) constructor.newInstance();
			new DirectFieldAccessor(manager).setPropertyValue("decisionVoters", voters);
			return manager;
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Failed to initialize AccessDecisionManager", ex);
		}
	}

	/**
	 * Convert SecurityRule into a form understood by Spring Security
	 * @param rule the rule to convert
	 * @return list of ConfigAttributes for Spring Security
	 */
	protected Collection<ConfigAttribute> getConfigAttributes(SecurityRule rule) {
		List<ConfigAttribute> configAttributes = new ArrayList<>();
		for (String attribute : rule.getAttributes()) {
			configAttributes.add(new SecurityConfig(attribute));
		}
		return configAttributes;
	}
}
