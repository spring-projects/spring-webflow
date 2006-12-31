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

import org.springframework.webflow.definition.FlowDefinition;

/**
 * A container of flow definitions. Extends the {@link FlowDefinitionRegistryMBean}
 * management interface exposing registry monitoring and management operations.
 * Also extends {@link FlowDefinitionLocator} for accessing registered Flow
 * definitions for execution at runtime.
 * <p>
 * Flow definition registries can be configured with a "parent" registry to provide a hook
 * into a larger flow definition registry hierarchy.
 * 
 * @author Keith Donald
 */
public interface FlowDefinitionRegistry extends FlowDefinitionLocator, FlowDefinitionRegistryMBean {

	/**
	 * Sets this registry's parent registry. When asked by a client to locate a
	 * flow definition this registry will query it's parent if it cannot
	 * fullfill the lookup request itself.
	 * @param parent the parent flow definition registry, may be null
	 */
	public void setParent(FlowDefinitionRegistry parent);

	/**
	 * Return all flow definitions registered in this registry. Note that this
	 * will trigger flow assemply for all registered flow definitions (which may
	 * be expensive).
	 * @return the flow definitions
	 * @throws FlowDefinitionConstructionException if there is a problem constructing
	 * one of the registered flow definitions
	 */
	public FlowDefinition[] getFlowDefinitions() throws FlowDefinitionConstructionException;

	/**
	 * Register a flow definition in this registry. Registers a "holder", not
	 * the Flow definition itself. This allows the actual Flow definition to be
	 * loaded lazily only when needed, and also rebuilt at runtime when its
	 * underlying resource changes without redeploy.
	 * @param flowHolder a holder holding the flow definition to register
	 */
	public void registerFlowDefinition(FlowDefinitionHolder flowHolder);

}