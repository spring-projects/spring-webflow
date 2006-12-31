/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.definition.registry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * A base class for factory beans that create populated flow definition registries.
 * Subclasses should override the {@link #doPopulate(FlowDefinitionRegistry)} method
 * to perform the registry population logic, typically delegating to a
 * {@link FlowDefinitionRegistrar} strategy to perform the population.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFlowDefinitionRegistryFactoryBean implements FactoryBean, InitializingBean {

	/**
	 * The registry to register flow definitions in.
	 */
	private FlowDefinitionRegistry registry = createFlowDefinitionRegistry();

	/**
	 * Sets the parent registry of the registry constructed by this factory
	 * bean.
	 * <p>
	 * A child registry will delegate to its parent if it cannot fulfill a
	 * request to locate a flow definition itself.
	 * @param parent the parent flow definition registry
	 */
	public void setParent(FlowDefinitionRegistry parent) {
		registry.setParent(parent);
	}

	// implementing InitializingBean

	public final void afterPropertiesSet() throws Exception {
		init();
		doPopulate(registry);
	}

	// implementing FactoryBean
	
	public Class getObjectType() {
		return FlowDefinitionRegistry.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public Object getObject() throws Exception {
		// the registry is populated by the time this is called
		return getRegistry();
	}

	/**
	 * Returns the flow definition registry constructed by the factory bean.
	 */
	public FlowDefinitionRegistry getRegistry() {
		return registry;
	}

	// subclassing hooks
	
	/**
	 * Create the flow definition registry to be populated in
	 * {@link #doPopulate(FlowDefinitionRegistry)}. Subclasses can override
	 * this method if they want to use a custom flow definition registry
	 * implementation.
	 */
	protected FlowDefinitionRegistry createFlowDefinitionRegistry() {
		return new FlowDefinitionRegistryImpl();
	}
	
	/**
	 * Template method subclasses may override to perform factory bean initialization 
	 * logic before registry population. Will be called before
	 * {@link #doPopulate(FlowDefinitionRegistry)}. The default implementation
	 * is empty.
	 */
	protected void init() {
	}
	
	/**
	 * Template method subclasses must override to perform registry population.
	 * @param registry the flow definition registry to populate
	 */
	protected abstract void doPopulate(FlowDefinitionRegistry registry);

}