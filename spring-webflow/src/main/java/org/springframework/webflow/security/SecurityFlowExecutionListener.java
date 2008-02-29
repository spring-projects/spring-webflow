package org.springframework.webflow.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
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

	/**
	 * Check security authorization when flow session starts
	 */
	public void sessionCreating(RequestContext context, FlowDefinition definition) {
		SecurityRule rule = (SecurityRule) definition.getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		if (rule != null) {
			Collection principalAuthorities = getPrincipalAuthorities();
			if (!rule.isAuthorized(principalAuthorities)) {
				throw new AccessDeniedException("Required authority not found: "
						+ SecurityRule.convertAuthoritiesToCommaSeparatedString(rule
								.getNonGrantedAuthorities(principalAuthorities)));
			}
		}
	}

	/**
	 * Check security authorization when entering state
	 */
	public void stateEntering(RequestContext context, StateDefinition state) throws EnterStateVetoException {
		SecurityRule rule = (SecurityRule) state.getAttributes().get(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		if (rule != null) {
			Collection principalAuthorities = getPrincipalAuthorities();
			if (!rule.isAuthorized(principalAuthorities)) {
				throw new AccessDeniedException("Required authority not found: "
						+ SecurityRule.convertAuthoritiesToCommaSeparatedString(rule
								.getNonGrantedAuthorities(principalAuthorities)));
			}
		}
	}

	/**
	 * Check security authorization on transition
	 */
	public void transitionExecuting(RequestContext context, TransitionDefinition transition) {
		SecurityRule rule = (SecurityRule) transition.getAttributes().get(
				SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME);
		if (rule != null) {
			Collection principalAuthorities = getPrincipalAuthorities();
			if (!rule.isAuthorized(principalAuthorities)) {
				throw new AccessDeniedException("Required authority not found: "
						+ SecurityRule.convertAuthoritiesToCommaSeparatedString(rule
								.getNonGrantedAuthorities(principalAuthorities)));
			}
		}
	}

	/**
	 * Get Spring Security authorities for the principal
	 * @return granted authorities for the principal
	 */
	protected Collection getPrincipalAuthorities() {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ((null == currentUser) || (null == currentUser.getAuthorities())
				|| (currentUser.getAuthorities().length < 1)) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList(currentUser.getAuthorities());
	}

}
