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
package org.springframework.webflow.config;

import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.model.registry.FlowModelRegistry;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;

/**
 * Flow registry implementation created by FlowRegistryFactoryBean.
 * @author Keith Donald
 */
class DefaultFlowRegistry extends FlowDefinitionRegistryImpl {

	private FlowModelRegistry flowModelRegistry = new FlowModelRegistryImpl();

	public FlowModelRegistry getFlowModelRegistry() {
		return flowModelRegistry;
	}

	public void setParent(FlowDefinitionRegistry parent) {
		super.setParent(parent);
		if (parent instanceof DefaultFlowRegistry) {
			DefaultFlowRegistry parentFlowRegistry = (DefaultFlowRegistry) parent;
			// link so a flow in the child registry that extends from a flow in the parent registry can find its parent
			flowModelRegistry.setParent(parentFlowRegistry.getFlowModelRegistry());
		}
	}
}