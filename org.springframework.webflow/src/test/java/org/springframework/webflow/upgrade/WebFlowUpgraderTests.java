package org.springframework.webflow.upgrade;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;

public class WebFlowUpgraderTests extends TestCase {
	public void testConvertFlow1() {
		WebFlowUpgrader upgrader = new WebFlowUpgrader();
		String result = upgrader.convert(new ClassPathResource("flow1.xml", getClass()));
		System.out.println(result);
	}
}
