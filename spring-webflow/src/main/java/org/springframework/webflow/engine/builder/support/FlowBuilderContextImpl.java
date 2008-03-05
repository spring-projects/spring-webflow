package org.springframework.webflow.engine.builder.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.GenericConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;

/**
 * Generic implementation of a flow builder context, suitable for use by most flow assembly systems.
 * @author Keith Donald
 */
public class FlowBuilderContextImpl implements FlowBuilderContext {

	private String flowId;

	private AttributeMap flowAttributes;

	private FlowDefinitionLocator flowDefinitionLocator;

	private FlowBuilderServices flowBuilderServices;

	private ConversionService conversionService;

	/**
	 * Creates a new flow builder context.
	 * @param flowId the id to assign the flow being built
	 * @param flowAttributes attributes to assign the flow being built
	 * @param flowDefinitionLocator a locator to find dependent subflows
	 * @param flowBuilderServices a parameter object providing access to additional services needed by the flow builder
	 */
	public FlowBuilderContextImpl(String flowId, AttributeMap flowAttributes,
			FlowDefinitionLocator flowDefinitionLocator, FlowBuilderServices flowBuilderServices) {
		Assert.hasText(flowId, "The flow id is required");
		Assert.notNull(flowDefinitionLocator, "The flow definition locator is required");
		Assert.notNull(flowBuilderServices, "The flow builder services holder is required");
		this.flowId = flowId;
		this.flowAttributes = flowAttributes;
		this.flowDefinitionLocator = flowDefinitionLocator;
		this.flowBuilderServices = flowBuilderServices;
		this.conversionService = createConversionService();
	}

	public FlowBuilderServices getFlowBuilderServices() {
		return flowBuilderServices;
	}

	// implementing flow builder context

	public String getFlowId() {
		return flowId;
	}

	public AttributeMap getFlowAttributes() {
		return flowAttributes;
	}

	public FlowArtifactFactory getFlowArtifactFactory() {
		return flowBuilderServices.getFlowArtifactFactory();
	}

	public BeanInvokingActionFactory getBeanInvokingActionFactory() {
		return flowBuilderServices.getBeanInvokingActionFactory();
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		return flowBuilderServices.getViewFactoryCreator();
	}

	public ExpressionParser getExpressionParser() {
		return flowBuilderServices.getExpressionParser();
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public ResourceLoader getResourceLoader() {
		return flowBuilderServices.getResourceLoader();
	}

	public BeanFactory getBeanFactory() {
		return flowBuilderServices.getBeanFactory();
	}

	public FlowDefinitionLocator getFlowDefinitionLocator() {
		return flowDefinitionLocator;
	}

	/**
	 * Factory method that creates the conversion service the flow builder will use. Subclasses may override. The
	 * default implementation registers Web Flow-specific converters thought to be useful for most builder
	 * implementations, setting the externally-provided builder services conversion service as its parent.
	 * @return the flow builder conversion service
	 */
	protected ConversionService createConversionService() {
		GenericConversionService service = new GenericConversionService();
		service.addConverter(new TextToTransitionCriteria(this));
		service.addConverter(new TextToTargetStateResolver(this));
		service.setParent(flowBuilderServices.getConversionService());
		return service;
	}

}