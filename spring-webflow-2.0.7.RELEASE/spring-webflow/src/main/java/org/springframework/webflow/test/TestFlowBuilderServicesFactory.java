package org.springframework.webflow.test;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;

/**
 * Factory that encapsulates configuration of default flow builder services for a test environment.
 * @author Keith Donald
 */
public class TestFlowBuilderServicesFactory {
	private TestFlowBuilderServicesFactory() {
	}

	public static FlowBuilderServices getServices() {
		FlowBuilderServices services = new FlowBuilderServices();
		services.setViewFactoryCreator(new MockViewFactoryCreator());
		services.setConversionService(new DefaultConversionService());
		services.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		services.setApplicationContext(createTestApplicationContext());
		return services;
	}

	private static StaticApplicationContext createTestApplicationContext() {
		StaticApplicationContext context = new StaticApplicationContext();
		context.refresh();
		return context;
	}
}