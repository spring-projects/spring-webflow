package org.springframework.webflow.upgrade;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class WebFlowUpgraderTests {
	@Test
	public void testConvertFlow1() {
		WebFlowUpgrader upgrader = new WebFlowUpgrader();
		String result = upgrader.convert(new ClassPathResource("flow1.xml", getClass()));
		System.out.println(result);
	}
}
