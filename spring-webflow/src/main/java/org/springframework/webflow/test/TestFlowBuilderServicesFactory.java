package org.springframework.webflow.test;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

/**
 * Factory that encapsulates configuration of default flow builder services for a test environment.
 * @author Keith Donald
 */
public class TestFlowBuilderServicesFactory {
	private TestFlowBuilderServicesFactory() {
	}

	public static FlowBuilderServices getServices() {
		FlowBuilderServices services = new FlowBuilderServices() {
			// The SpEL parser must use the currently configured conversion service.
			public void setConversionService(ConversionService conversionService) {
				super.setConversionService(conversionService);
				setExpressionParser(new WebFlowSpringELExpressionParser(new SpelExpressionParser(), conversionService));
			}
		};
		services.setViewFactoryCreator(new MockViewFactoryCreator());
		services.setConversionService(new DefaultConversionService());
		services.setApplicationContext(createTestApplicationContext());
		return services;
	}

	private static StaticApplicationContext createTestApplicationContext() {
		StaticApplicationContext context = new StaticApplicationContext();
		context.refresh();
		return context;
	}

}