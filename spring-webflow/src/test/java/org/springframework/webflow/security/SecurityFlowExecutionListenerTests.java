package org.springframework.webflow.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
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

	public void testSessionCreatingWithSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		SecurityRule rule = getSecurityRuleAnyAuthorized();
		flow.getAttributes().put(SecurityRule.SECURITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.sessionCreating(context, flow);
	}

	public void testStateEnteringNoSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory());
		listener.stateEntering(context, state);
	}

	public void testStateEnteringWithSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory());
		SecurityRule rule = getSecurityRuleAllAuthorized();
		state.getAttributes().put(SecurityRule.SECURITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.stateEntering(context, state);
	}

	public void testTransitionExecutingNoSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Transition transition = new Transition(new DefaultTargetStateResolver("target"));
		listener.transitionExecuting(context, transition);
	}

	public void testTransitionExecutingWithSecurity() {
		SecurityFlowExecutionListener listener = new SecurityFlowExecutionListener();
		RequestContext context = new MockRequestContext();
		Transition transition = new Transition(new DefaultTargetStateResolver("target"));
		SecurityRule rule = getSecurityRuleAnyAuthorized();
		transition.getAttributes().put(SecurityRule.SECURITY_ATTRIBUTE_NAME, rule);
		configureSecurityContext();
		listener.transitionExecuting(context, transition);
	}

	public void testDecideAnyAuthorized() {
		configureSecurityContext();
		new SecurityFlowExecutionListener().decide(getSecurityRuleAnyAuthorized(), this);
	}

	public void testDecideAnyDenied() {
		configureSecurityContext();
		try {
			new SecurityFlowExecutionListener().decide(getSecurityRuleAnyDenied(), this);
			fail("expected AccessDeniedExpetion");
		} catch (AccessDeniedException e) {
			// we want this
		}
	}

	public void testDecideAllAuthorized() {
		configureSecurityContext();
		new SecurityFlowExecutionListener().decide(getSecurityRuleAllAuthorized(), this);
	}

	public void testDecideAllDenied() {
		configureSecurityContext();
		try {
			new SecurityFlowExecutionListener().decide(getSecurityRuleAllDenied(), this);
			fail("expected AccessDeniedExpetion");
		} catch (AccessDeniedException e) {
			// we want this
		}
	}

	private void configureSecurityContext() {
		SecurityContext sc = new SecurityContextImpl();
		sc.setAuthentication(getAuthentication());
		SecurityContextHolder.setContext(sc);
	}

	private SecurityRule getSecurityRuleAnyAuthorized() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection<String> attributes = new HashSet<String>();
		attributes.add("ROLE_1");
		attributes.add("ROLE_A");
		rule.setAttributes(attributes);
		return rule;
	}

	private SecurityRule getSecurityRuleAnyDenied() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection<String> attributes = new HashSet<String>();
		attributes.add("ROLE_A");
		attributes.add("ROLE_B");
		rule.setAttributes(attributes);
		return rule;
	}

	private SecurityRule getSecurityRuleAllAuthorized() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ALL);
		Collection<String> attributes = new HashSet<String>();
		attributes.add("ROLE_1");
		attributes.add("ROLE_3");
		rule.setAttributes(attributes);
		return rule;
	}

	private SecurityRule getSecurityRuleAllDenied() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ALL);
		Collection<String> attributes = new HashSet<String>();
		attributes.add("ROLE_1");
		attributes.add("ROLE_A");
		rule.setAttributes(attributes);
		return rule;
	}

	private Authentication getAuthentication() {
		List<GrantedAuthority> authorities = Arrays.<GrantedAuthority> asList(new SimpleGrantedAuthority("ROLE_1"),
				new SimpleGrantedAuthority("ROLE_2"), new SimpleGrantedAuthority("ROLE_3"));
		return new UsernamePasswordAuthenticationToken("test", "", authorities);
	}
}
