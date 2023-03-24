package org.springframework.faces.config;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.faces.webflow.JsfResourceRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractResourcesConfigurationTests {

	protected ApplicationContext context;

	@BeforeEach
	public void setUp() throws Exception {
		this.context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testConfigureDefaults() {
		Map<String, ?> map = this.context.getBeansOfType(HttpRequestHandlerAdapter.class);
		assertEquals(1, map.values().size());

		Object resourceHandler = this.context.getBean("jsfResourceRequestHandler");
		assertNotNull(resourceHandler);
		assertTrue(resourceHandler instanceof JsfResourceRequestHandler);

		map = this.context.getBeansOfType(SimpleUrlHandlerMapping.class);
		assertEquals(1, map.values().size());
		SimpleUrlHandlerMapping handlerMapping = (SimpleUrlHandlerMapping) map.values().iterator().next();
		assertSame(resourceHandler, handlerMapping.getHandlerMap().get("/jakarta.faces.resource/**"));
		assertEquals(0, handlerMapping.getOrder());
	}

}
