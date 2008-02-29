package org.springframework.webflow.security;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

public class SecurityFlowExecutionListenerTests extends TestCase {

	public void testSessionCreatingNoSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		FlowDefinition definition = new Flow("flow");
		listener.sessionCreating(context, definition);
	}

	public void testSessionCreatingAuthorized() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		SecurityRule rule = getSecurityRuleAuthorized();
		((LocalAttributeMap) flow.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.sessionCreating(context, flow);
	}

	public void testSessionCreatingDenied() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		SecurityRule rule = getSecurityRuleDenied();
		((LocalAttributeMap) flow.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		try {
			listener.sessionCreating(context, flow);
			fail("expected AccessDeniedException");
		} catch (AccessDeniedException e) {
			// success
		}
	}

	public void testStateEnteringNoSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory());
		listener.stateEntering(context, state);
	}

	public void testStateEnteringAuthorized() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory());
		SecurityRule rule = getSecurityRuleAuthorized();
		((LocalAttributeMap) state.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.stateEntering(context, state);
	}

	public void testStateEnteringDenied() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory());
		SecurityRule rule = getSecurityRuleDenied();
		((LocalAttributeMap) state.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		try {
			listener.stateEntering(context, state);
			fail("expected AccessDeniedException");
		} catch (AccessDeniedException e) {
			// success
		}
	}

	public void testTransitionExecutingNoSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Transition transition = new Transition(new DefaultTargetStateResolver("target"));
		listener.transitionExecuting(context, transition);
	}

	public void testTransitionExecutingAuthorized() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Transition transition = new Transition(new DefaultTargetStateResolver("target"));
		SecurityRule rule = getSecurityRuleAuthorized();
		((LocalAttributeMap) transition.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.transitionExecuting(context, transition);
	}

	public void testTransitionExecutingDenied() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Transition transition = new Transition(new DefaultTargetStateResolver("target"));
		SecurityRule rule = getSecurityRuleDenied();
		((LocalAttributeMap) transition.getAttributes()).put(SecurityRule.SECURITY_AUTHORITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		try {
			listener.transitionExecuting(context, transition);
			fail("expected AccessDeniedException");
		} catch (AccessDeniedException e) {
			// success
		}
	}

	private SecurityRule getSecurityRuleAuthorized() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection authorities = new HashSet();
		authorities.add("ROLE_USER");
		rule.setRequiredAuthorities(authorities);
		return rule;
	}

	private SecurityRule getSecurityRuleDenied() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection authorities = new HashSet();
		authorities.add("ROLE_ANONYMOUS");
		rule.setRequiredAuthorities(authorities);
		return rule;
	}

	private void configureSecurityContext() {
		GrantedAuthority[] authorities = { new GrantedAuthorityImpl("ROLE_USER") };
		Authentication authentication = new TestingAuthenticationToken("test", "", authorities);
		SecurityContext sc = new SecurityContextImpl();
		sc.setAuthentication(authentication);
		SecurityContextHolder.setContext(sc);
	}
}
