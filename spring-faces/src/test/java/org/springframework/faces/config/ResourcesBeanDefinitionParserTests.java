package org.springframework.faces.config;

import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.faces.webflow.JsfResourceRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

public class ResourcesBeanDefinitionParserTests extends TestCase {

	private ClassPathXmlApplicationContext context;

	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("org/springframework/faces/config/resources.xml");
	}

	protected void tearDown() throws Exception {
	}

	public void testConfigureDefaults() {
		Map<String, ?> map = context.getBeansOfType(HttpRequestHandlerAdapter.class);
		assertEquals(1, map.values().size());

		Object resourceHandler = context.getBean(ResourcesBeanDefinitionParser.RESOURCE_HANDLER_BEAN_NAME);
		assertNotNull(resourceHandler);
		assertTrue(resourceHandler instanceof JsfResourceRequestHandler);

		map = context.getBeansOfType(SimpleUrlHandlerMapping.class);
		assertEquals(1, map.values().size());
		SimpleUrlHandlerMapping handlerMapping = (SimpleUrlHandlerMapping) map.values().iterator().next();
		assertEquals(ResourcesBeanDefinitionParser.RESOURCE_HANDLER_BEAN_NAME,
				handlerMapping.getUrlMap().get("/javax.faces.resource/**"));
		assertEquals(0, handlerMapping.getOrder());
	}

}
