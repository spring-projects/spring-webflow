/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.definition.registry.AbstractFlowDefinitionRegistryFactoryBean;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.execution.Action;

/**
 * A base class for factory beans that create populated registries of flow
 * definitions built using a {@link FlowBuilder}, typically a {@link BaseFlowBuilder}
 * subclass. This base class will setup a {@link FlowServiceLocator} for
 * use by the flow builder.
 * <p>
 * Subclasses should override the {@link #doPopulate(FlowDefinitionRegistry)}
 * template method to perform the registry population logic, typically delegating to a
 * {@link org.springframework.webflow.definition.registry.FlowDefinitionRegistrar}
 * strategy.
 * 
 * @see org.springframework.webflow.definition.registry.FlowDefinitionRegistry
 * @see org.springframework.webflow.definition.registry.FlowDefinitionRegistrar
 * 
 * @author Keith Donald
 */
public abstract class AbstractFlowBuildingFlowRegistryFactoryBean extends AbstractFlowDefinitionRegistryFactoryBean
		implements BeanFactoryAware, ResourceLoaderAware {

	/**
	 * The locator of services needed by the flows built for inclusion in the
	 * registry.
	 */
	private FlowServiceLocator flowServiceLocator;

	/**
	 * The factory encapsulating the creation of central Flow artifacts such as
	 * {@link Flow flows} and {@link State states}.
	 */
	private FlowArtifactFactory flowArtifactFactory;

	/**
	 * The factory encapsulating the creation of bean invoking actions, actions
	 * that adapt methods on objects to the {@link Action} interface.
	 */
	private BeanInvokingActionFactory beanInvokingActionFactory;

	/**
	 * The parser for parsing expression strings into evaluatable expression
	 * objects.
	 */
	private ExpressionParser expressionParser;

	/**
	 * A conversion service that can convert between types.
	 */
	private ConversionService conversionService;

	/**
	 * A resource loader that can load resources.
	 */
	private ResourceLoader resourceLoader;

	/**
	 * The Spring bean factory that manages configured flow artifacts.
	 */
	private BeanFactory beanFactory;

	/**
	 * Returns the factory encapsulating the creation of central Flow artifacts
	 * such as {@link Flow flows} and {@link State states}.
	 */
	protected FlowArtifactFactory getFlowArtifactFactory() {
		return flowArtifactFactory;
	}
	
	/**
	 * Sets the factory encapsulating the creation of central Flow artifacts
	 * such as {@link Flow flows} and {@link State states}.
	 */
	public void setFlowArtifactFactory(FlowArtifactFactory flowArtifactFactory) {
		this.flowArtifactFactory = flowArtifactFactory;
	}
	
	/**
	 * Returns the factory for creating bean invoking actions, actions that adapt
	 * methods on objects to the {@link Action} interface.
	 */
	protected BeanInvokingActionFactory getBeanInvokingActionFactory() {
		return beanInvokingActionFactory;
	}

	/**
	 * Sets the factory for creating bean invoking actions, actions that adapt
	 * methods on objects to the {@link Action} interface.
	 */
	public void setBeanInvokingActionFactory(BeanInvokingActionFactory beanInvokingActionFactory) {
		this.beanInvokingActionFactory = beanInvokingActionFactory;
	}
	
	/**
	 * Returns the expression parser responsible for parsing expression strings into
	 * evaluatable expression objects.
	 */
	protected ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	/**
	 * Set the expression parser responsible for parsing expression strings into
	 * evaluatable expression objects.
	 */
	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}
	
	/**
	 * Returns the conversion service to use to convert between types; typically
	 * from string to a rich object type.
	 */
	protected ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Set the conversion service to use to convert between types; typically
	 * from string to a rich object type.
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	// implementing ResourceLoaderAware
	
	/**
	 * Returns the injected resource loader.
	 */
	protected ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	// implementing BeanFactoryAware
	
	/**
	 * Returns the bean factory managing this bean.
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected final void init() {
		flowServiceLocator = createFlowServiceLocator();
		init(flowServiceLocator);
	}

	// subclassing hooks

	/**
	 * Factory method for creating the service locator used to locate webflow
	 * services during flow assembly. Subclasses may override to customize the
	 * instantiation and configuration of the locator returned.
	 * @return the service locator
	 */
	protected FlowServiceLocator createFlowServiceLocator() {
		DefaultFlowServiceLocator serviceLocator = new DefaultFlowServiceLocator(getRegistry(), beanFactory);
		if (flowArtifactFactory != null) {
			serviceLocator.setFlowArtifactFactory(flowArtifactFactory);
		}
		if (beanInvokingActionFactory != null) {
			serviceLocator.setBeanInvokingActionFactory(beanInvokingActionFactory);
		}
		if (expressionParser != null) {
			serviceLocator.setExpressionParser(expressionParser);
		}
		if (conversionService != null) {
			serviceLocator.setConversionService(conversionService);
		}
		if (resourceLoader != null) {
			serviceLocator.setResourceLoader(resourceLoader);
		}
		return serviceLocator;
	}

	/**
	 * Called after properties have been set on the service locator, but before
	 * registry population. Subclasses may override to perform custom initialization
	 * of the flow service locator.
	 * @param flowServiceLocator the flow service locator to use to locate externally managed
	 * services needed during flow building and assembly, typically used by a
	 * {@link org.springframework.webflow.definition.registry.FlowDefinitionRegistrar}
	 */
	protected void init(FlowServiceLocator flowServiceLocator) {
	}

	/**
	 * Returns the strategy for locating dependent artifacts when a flow is
	 * being built. May be called by subclasses during
	 * {@link #doPopulate(FlowDefinitionRegistry) registry population} to wire
	 * in the service locator needed for flow assembly.
	 */
	protected FlowServiceLocator getFlowServiceLocator() {
		return flowServiceLocator;
	}

	protected abstract void doPopulate(FlowDefinitionRegistry registry);
}