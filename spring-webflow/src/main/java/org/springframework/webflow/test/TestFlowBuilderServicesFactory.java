package org.springframework.webflow.test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
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
		services.setConversionService(DefaultConversionService.getSharedInstance());
		services.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		services.setResourceLoader(new DefaultResourceLoader());
		services.setBeanFactory(new StaticListableBeanFactory());
		return services;
	}
}