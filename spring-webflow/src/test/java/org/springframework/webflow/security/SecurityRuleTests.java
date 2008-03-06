package org.springframework.webflow.security;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SecurityRuleTests extends TestCase {

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

	public void testDefaultComparisonType() {
		SecurityRule rule = new SecurityRule();
		Assert.assertTrue(rule.getComparisonType() == SecurityRule.COMPARISON_ANY);
	}

}
