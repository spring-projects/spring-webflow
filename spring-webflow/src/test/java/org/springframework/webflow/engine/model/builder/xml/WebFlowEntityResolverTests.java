package org.springframework.webflow.engine.model.builder.xml;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

public class WebFlowEntityResolverTests extends TestCase {

	private static final String PUBLIC_ID = "http://www.springframework.org/schema/webflow";

	public void testResolve24() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow-2.4.xsd");
		assertNotNull(source);
	}

	public void testResolve20() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd");
		assertNotNull(source);
	}

	public void testResolveLatest() {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow.xsd");
		assertNotNull(source);
	}

}
