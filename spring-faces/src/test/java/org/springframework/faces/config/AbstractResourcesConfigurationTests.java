package org.springframework.faces.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.faces.webflow.JsfResourceRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

public abstract class AbstractResourcesConfigurationTests {

	protected ApplicationContext context;

	@Before
	public void setUp() throws Exception {
		this.context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();

	@After
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
		assertSame(resourceHandler, handlerMapping.getHandlerMap().get("/javax.faces.resource/**"));
		assertEquals(0, handlerMapping.getOrder());
	}

}
