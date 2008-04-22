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
package org.springframework.webflow.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.AccessDecisionManager;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.SecurityConfig;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.vote.AbstractAccessDecisionManager;
import org.springframework.security.vote.AffirmativeBased;
import org.springframework.security.vote.RoleVoter;
import org.springframework.security.vote.UnanimousBased;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.EnterStateVetoException;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;

/**
 * Flow security integration with Spring Security
 * 
 * @author Scott Andrews
 */
public class SecurityFlowExecutionListener extends FlowExecutionListenerAdapter {

	private AccessDecisionManager accessDecisionManager;

	/**
	 * Get the access decision manager that makes flow authorization decisions.
	 * @return the decision manager
	 */
	public AccessDecisionManager getAccessDecisionManager() {
		return accessDecisionManager;
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
		ConfigAttributeDefinition config = new ConfigAttributeDefinition(getConfigAttributes(rule));
		if (accessDecisionManager != null) {
			accessDecisionManager.decide(authentication, object, config);
		} else {
			AbstractAccessDecisionManager abstractAccessDecisionManager;
			List voters = new ArrayList();
			voters.add(new RoleVoter());
			if (rule.getComparisonType() == SecurityRule.COMPARISON_ANY) {
				abstractAccessDecisionManager = new AffirmativeBased();
			} else if (rule.getComparisonType() == SecurityRule.COMPARISON_ALL) {
				abstractAccessDecisionManager = new UnanimousBased();
			} else {
				throw new IllegalStateException("Unknown SecurityRule match type: " + rule.getComparisonType());
			}
			abstractAccessDecisionManager.setDecisionVoters(voters);
			abstractAccessDecisionManager.decide(authentication, object, config);
		}
	}

	/**
	 * Convert SecurityRule into a form understood by Spring Security
	 * @param rule the rule to convert
	 * @return list of ConfigAttributes for Spring Security
	 */
	protected List getConfigAttributes(SecurityRule rule) {
		List configAttributes = new ArrayList();
		Iterator attributeIt = rule.getAttributes().iterator();
		while (attributeIt.hasNext()) {
			configAttributes.add(new SecurityConfig((String) attributeIt.next()));
		}
		return configAttributes;
	}
}
