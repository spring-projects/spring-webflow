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

	AccessDecisionManager accessDecisionManager;

	/**
	 * Check security authorization when flow session starts
	 */
	public void sessionCreating(RequestContext context, FlowDefinition definition) {
		SecurityRule rule = (SecurityRule) definition.getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		if (rule != null) {
			decide(rule, definition);
		}
	}

	/**
	 * Check security authorization when entering state
	 */
	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
		SecurityRule rule = (SecurityRule) state.getAttributes().get(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		if (rule != null) {
			decide(rule, state);
		}
	}

	/**
	 * Check security authorization on transition
	 */
	public void transitionExecuting(RequestContext context, TransitionDefinition transition) {
		SecurityRule rule = (SecurityRule) transition.getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
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
	public void decide(SecurityRule rule, Object object) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ConfigAttributeDefinition config = new ConfigAttributeDefinition(getConfigAttributes(rule));
		if (accessDecisionManager == null) {
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
			accessDecisionManager = abstractAccessDecisionManager;
		}
		accessDecisionManager.decide(authentication, object, config);
	}

	/**
	 * Convert SecurityRule into a form understood by Spring Security
	 * @param rule the rule to convert
	 * @return list of ConfigAttributes for Spring Security
	 */
	protected List getConfigAttributes(SecurityRule rule) {
		List configAttributes = new ArrayList();
		Iterator requiredAuthorityIt = rule.getRequiredAuthorities().iterator();
		while (requiredAuthorityIt.hasNext()) {
			configAttributes.add(new SecurityConfig((String) requiredAuthorityIt.next()));
		}
		return configAttributes;
	}

	/**
	 * Get decision manager
	 * @return the decision manager
	 */
	public AccessDecisionManager getAccessDecisionManager() {
		return accessDecisionManager;
	}

	/**
	 * Set decision manager
	 * @param accessDecisionManager the decision manager to user
	 */
	public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
		this.accessDecisionManager = accessDecisionManager;
	}

}
