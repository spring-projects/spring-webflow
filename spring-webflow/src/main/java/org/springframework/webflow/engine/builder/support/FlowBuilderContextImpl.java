/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.builder.support;

import java.util.Set;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.GenericConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
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

	public FlowDefinitionLocator getFlowDefinitionLocator() {
		return flowDefinitionLocator;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		return flowBuilderServices.getViewFactoryCreator();
	}

	public ExpressionParser getExpressionParser() {
		return flowBuilderServices.getExpressionParser();
	}

	public ApplicationContext getApplicationContext() {
		return flowBuilderServices.getApplicationContext();
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
		service.setParent(new ParentConversionServiceProxy());
		return service;
	}

	/**
	 * A little proxy that refreshes the externally configured conversion service reference on each invocation.
	 */
	private class ParentConversionServiceProxy implements ConversionService {
		public Object executeConversion(Object source, Class targetClass) throws ConversionException {
			return getFlowBuilderServices().getConversionService().executeConversion(source, targetClass);
		}

		public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
				throws ConversionExecutionException {
			return getFlowBuilderServices().getConversionService().getConversionExecutor(sourceClass, targetClass);
		}

		public Set getConversionExecutors(Class sourceClass) {
			return getFlowBuilderServices().getConversionService().getConversionExecutors(sourceClass);
		}

		public ConversionExecutor getConversionExecutor(String id, Class sourceClass, Class targetClass)
				throws ConversionExecutorNotFoundException {
			return getFlowBuilderServices().getConversionService().getConversionExecutor(id, sourceClass, targetClass);
		}

		public Class getClassForAlias(String name) {
			return getFlowBuilderServices().getConversionService().getClassForAlias(name);
		}

	}

}