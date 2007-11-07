package org.springframework.webflow.test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.core.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

class FlowBuilderSystemDefaults {
	private FlowBuilderServices defaultServices;

	public FlowBuilderSystemDefaults() {
		defaultServices = new FlowBuilderServices();
		defaultServices.setFlowArtifactFactory(new FlowArtifactFactory());
		defaultServices.setBeanInvokingActionFactory(new BeanInvokingActionFactory());
		defaultServices.setViewFactoryCreator(new MockViewFactoryCreator());
		defaultServices.setConversionService(new DefaultConversionService());
		defaultServices.setExpressionParser(DefaultExpressionParserFactory.getExpressionParser());
		defaultServices.setResourceLoader(new DefaultResourceLoader());
		defaultServices.setBeanFactory(new StaticListableBeanFactory());
	}

	public static FlowBuilderServices get() {
		return new FlowBuilderSystemDefaults().createBuilderServices();
	}

	public FlowBuilderServices createBuilderServices() {
		FlowBuilderServices builderServices = new FlowBuilderServices();
		applyDefaults(builderServices);
		return builderServices;
	}

	private void applyDefaults(FlowBuilderServices services) {
		services.setFlowArtifactFactory(defaultServices.getFlowArtifactFactory());
		services.setBeanInvokingActionFactory(defaultServices.getBeanInvokingActionFactory());
		services.setViewFactoryCreator(defaultServices.getViewFactoryCreator());
		services.setConversionService(defaultServices.getConversionService());
		services.setExpressionParser(defaultServices.getExpressionParser());
		services.setResourceLoader(defaultServices.getResourceLoader());
		services.setBeanFactory(defaultServices.getBeanFactory());
	}
}