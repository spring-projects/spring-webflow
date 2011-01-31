package org.springframework.webflow.engine.model.builder.xml;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

public class WebFlowEntityResolverTests extends TestCase {

	private static final String PUBLIC_ID = "http://www.springframework.org/schema/webflow";

	public void testResolve23() throws Exception {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow-2.3.xsd");
		assertNotNull(source);
	}

	public void testResolve20() throws Exception {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd");
		assertNotNull(source);
	}

	public void testResolveLatest() throws Exception {
		WebFlowEntityResolver resolver = new WebFlowEntityResolver();
		InputSource source = resolver.resolveEntity(PUBLIC_ID,
				"http://www.springframework.org/schema/webflow/spring-webflow.xsd");
		assertNotNull(source);
	}

}
