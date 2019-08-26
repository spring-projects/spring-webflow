package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;

public class FlowRegistryBeanDefinitionParserTests extends AbstractFlowRegistryConfigurationTests {

	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-registry.xml");
	}

	@Test
	public void testDefaultFlowBuilderServices() {
		Map<String, FlowBuilderServices> flowBuilderServicesBeans = context.getBeansOfType(FlowBuilderServices.class);
		assertTrue(flowBuilderServicesBeans.size() > 0);
		for (FlowBuilderServices builderServices : flowBuilderServicesBeans.values()) {
			assertNotNull(builderServices);
			assertTrue(builderServices.getExpressionParser() instanceof SpringELExpressionParser);
			assertTrue(builderServices.getViewFactoryCreator() instanceof MvcViewFactoryCreator);
			assertTrue(builderServices.getConversionService() instanceof DefaultConversionService);
			assertFalse(builderServices.getDevelopment());
		}
	}

}
