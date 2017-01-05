package org.springframework.faces.config;

import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.faces.webflow.JsfResourceRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

public abstract class AbstractResourcesConfigurationTests extends TestCase {

	protected ApplicationContext context;

	public void setUp() throws Exception {
		this.context = initApplicationContext();
	}

	protected abstract ApplicationContext initApplicationContext();

	protected void tearDown() throws Exception {
	}

	public void testConfigureDefaults() {
		Map<String, ?> map = this.context.getBeansOfType(HttpRequestHandlerAdapter.class);
		assertEquals(1, map.values().size());

		Object resourceHandler = this.context.getBean(ResourcesBeanDefinitionParser.SERVLET_RESOURCE_HANDLER_BEAN_NAME);
		assertNotNull(resourceHandler);
		assertTrue(resourceHandler instanceof JsfResourceRequestHandler);

		map = this.context.getBeansOfType(SimpleUrlHandlerMapping.class);
		assertEquals(1, map.values().size());
		SimpleUrlHandlerMapping handlerMapping = (SimpleUrlHandlerMapping) map.values().iterator().next();
		assertSame(resourceHandler, handlerMapping.getHandlerMap().get("/javax.faces.resource/**"));
		assertEquals(0, handlerMapping.getOrder());
	}

}
