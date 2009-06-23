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
package org.springframework.webflow.engine.model.registry;

/**
 * A container of flow models. Extends {@link FlowModelLocator} for accessing registered Flow models for conversion to
 * flow definitions.
 * <p>
 * Flow model registries can be configured with a "parent" registry to provide a hook into a larger flow model registry
 * hierarchy.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public interface FlowModelRegistry extends FlowModelLocator {

	/**
	 * Sets this registry's parent registry. When asked by a client to locate a flow model this registry will query it's
	 * parent if it cannot fulfill the lookup request itself.
	 * @param parent the parent flow model registry, may be null
	 */
	public void setParent(FlowModelRegistry parent);

	/**
	 * Register a flow model in this registry. Registers a "holder", not the Flow model itself. This allows the actual
	 * Flow model to be loaded lazily only when needed, and also rebuilt at runtime when its underlying resource changes
	 * without re-deploy.
	 * @param id the id to register the flow model under
	 * @param modelHolder a holder holding the flow model to register
	 */
	public void registerFlowModel(String id, FlowModelHolder modelHolder);

}