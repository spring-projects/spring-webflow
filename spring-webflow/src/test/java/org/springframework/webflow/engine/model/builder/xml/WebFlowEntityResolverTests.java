package org.springframework.webflow.engine.model.builder.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

public class WebFlowEntityResolverTests {

	private static final String PUBLIC_ID = "http://www.springframework.org/schema/webflow";

	@Test
	public void testResolve24() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"https://www.springframework.org/schema/webflow/spring-webflow-2.4.xsd");
		assertNotNull(source);
	}

	@Test
	public void testResolve20() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"https://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd");
		assertNotNull(source);
	}

	@Test
	public void testResolveLatest() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"https://www.springframework.org/schema/webflow/spring-webflow.xsd");
		assertNotNull(source);
	}

}
