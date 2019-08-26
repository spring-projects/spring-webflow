package org.springframework.webflow.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;


public class SecurityRuleTests {

	@Test
	public void testConvertAttributesToCommaSeparatedString() {
		Collection<String> attributes = new ArrayList<>();
		attributes.add("ROLE_1");
		attributes.add("ROLE_2");
		assertEquals("ROLE_1, ROLE_2", SecurityRule.securityAttributesToCommaDelimitedList(attributes));
	}

	@Test
	public void testConvertAttributesFromCommaSeparatedString() {
		Collection<String> attributes = SecurityRule.commaDelimitedListToSecurityAttributes(" ,,ROLE_1, ROLE_2");
		assertEquals(2, attributes.size());
		assertTrue(attributes.contains("ROLE_1"));
		assertTrue(attributes.contains("ROLE_2"));
	}

	@Test
	public void testDefaultComparisonType() {
		SecurityRule rule = new SecurityRule();
		assertTrue(rule.getComparisonType() == SecurityRule.COMPARISON_ANY);
	}

}
