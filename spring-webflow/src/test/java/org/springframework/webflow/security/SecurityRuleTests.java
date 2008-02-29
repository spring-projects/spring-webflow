package org.springframework.webflow.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SecurityRuleTests extends TestCase {

	public void testAuthorizedAll() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ALL);
		Collection requiredAuthorities = new HashSet();
		requiredAuthorities.add("ROLE_USER");
		requiredAuthorities.add("ROLE_SUPERVISOR");
		rule.setRequiredAuthorities(requiredAuthorities);
		Assert.assertTrue(rule.isAuthorized(getPrincipalAuthorities()));
	}

	public void testAuthorizedAllFail() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ALL);
		Collection requiredAuthorities = new HashSet();
		requiredAuthorities.add("ROLE_USER");
		requiredAuthorities.add("ROLE_ANONYMOUS");
		rule.setRequiredAuthorities(requiredAuthorities);
		Assert.assertFalse(rule.isAuthorized(getPrincipalAuthorities()));
	}

	public void testAuthorizedAny() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection requiredAuthorities = new HashSet();
		requiredAuthorities.add("ROLE_USER");
		requiredAuthorities.add("ROLE_ANONYMOUS");
		rule.setRequiredAuthorities(requiredAuthorities);
		Assert.assertTrue(rule.isAuthorized(getPrincipalAuthorities()));
	}

	public void testAuthorizedAnyFail() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ANY);
		Collection requiredAuthorities = new HashSet();
		requiredAuthorities.add("ROLE_NONE");
		requiredAuthorities.add("ROLE_ANONYMOUS");
		rule.setRequiredAuthorities(requiredAuthorities);
		Assert.assertFalse(rule.isAuthorized(getPrincipalAuthorities()));
	}

	public void testNonGrantedAuthorities() {
		SecurityRule rule = new SecurityRule();
		rule.setComparisonType(SecurityRule.COMPARISON_ALL);
		Collection requiredAuthorities = new HashSet();
		requiredAuthorities.add("ROLE_USER");
		requiredAuthorities.add("ROLE_ANONYMOUS");
		rule.setRequiredAuthorities(requiredAuthorities);
		Collection nonGrantedAuthorities = rule.getNonGrantedAuthorities(getPrincipalAuthorities());
		Assert.assertEquals(1, nonGrantedAuthorities.size());
		Assert.assertTrue(nonGrantedAuthorities.contains("ROLE_ANONYMOUS"));
	}

	public void testConvertAuthoritiesToCommaSeparatedString() {
		Collection authorities = new ArrayList();
		authorities.add("ROLE_USER");
		authorities.add("ROLE_ANONYMOUS");
		Assert.assertEquals("ROLE_USER, ROLE_ANONYMOUS", SecurityRule
				.convertAuthoritiesToCommaSeparatedString(authorities));
	}

	public void testConvertAuthoritiesFromCommaSeparatedString() {
		Collection authorities = SecurityRule
				.convertAuthoritiesFromCommaSeparatedString(" ,,ROLE_USER, ROLE_ANONYMOUS");
		Assert.assertEquals(2, authorities.size());
		Assert.assertTrue(authorities.contains("ROLE_USER"));
		Assert.assertTrue(authorities.contains("ROLE_ANONYMOUS"));
	}

	private Collection getPrincipalAuthorities() {
		Collection principalAuthorities = new HashSet();
		principalAuthorities.add("ROLE_USER");
		principalAuthorities.add("ROLE_SUPERVISOR");
		return principalAuthorities;
	}
}
