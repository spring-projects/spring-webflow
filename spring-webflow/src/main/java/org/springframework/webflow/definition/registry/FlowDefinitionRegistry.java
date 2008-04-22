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
package org.springframework.webflow.definition.registry;

import org.springframework.webflow.definition.FlowDefinition;

/**
 * A container of flow definitions. Extends {@link FlowDefinitionLocator} for accessing registered Flow definitions for
 * execution at runtime.
 * <p>
 * Flow definition registries can be configured with a "parent" registry to provide a hook into a larger flow definition
 * registry hierarchy.
 * 
 * @author Keith Donald
 */
public interface FlowDefinitionRegistry extends FlowDefinitionLocator {

	/**
	 * Returns the number of flow definitions registered in this registry.
	 * @return the flow definition count
	 */
	public int getFlowDefinitionCount();

	/**
	 * Returns the ids of the flows registered in this registry.
	 * @return the flow definition ids
	 */
	public String[] getFlowDefinitionIds();

	/**
	 * Returns this registry'es parent registry.
	 * @return the parent flow definition registry, or null if no parent is set
	 */
	public FlowDefinitionRegistry getParent();

	/**
	 * Sets this registry's parent registry. When asked by a client to locate a flow definition this registry will query
	 * it's parent if it cannot fulfill the lookup request itself.
	 * @param parent the parent flow definition registry, may be null
	 */
	public void setParent(FlowDefinitionRegistry parent);

	/**
	 * Register a flow definition in this registry. Registers a "holder", not the Flow definition itself. This allows
	 * the actual Flow definition to be loaded lazily only when needed, and also rebuilt at runtime when its underlying
	 * resource changes without re-deploy.
	 * @param definitionHolder a holder holding the flow definition to register
	 */
	public void registerFlowDefinition(FlowDefinitionHolder definitionHolder);

	/**
	 * Register a flow definition in this registry.
	 * @param definition the actual flow definition
	 */
	public void registerFlowDefinition(FlowDefinition definition);

}