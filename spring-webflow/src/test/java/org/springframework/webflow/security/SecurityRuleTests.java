package org.springframework.webflow.security;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SecurityRuleTests extends TestCase {

	public void testConvertAttributesToCommaSeparatedString() {
		Collection attributes = new ArrayList();
		attributes.add("ROLE_1");
		attributes.add("ROLE_2");
		Assert.assertEquals("ROLE_1, ROLE_2", SecurityRule.securityAttributesToCommaDelimitedList(attributes));
	}

	public void testConvertAttributesFromCommaSeparatedString() {
		Collection attributes = SecurityRule.commaDelimitedListToSecurityAttributes(" ,,ROLE_1, ROLE_2");
		Assert.assertEquals(2, attributes.size());
		Assert.assertTrue(attributes.contains("ROLE_1"));
		Assert.assertTrue(attributes.contains("ROLE_2"));
	}

	public void testDefaultComparisonType() {
		SecurityRule rule = new SecurityRule();
		Assert.assertTrue(rule.getComparisonType() == SecurityRule.COMPARISON_ANY);
	}

}
