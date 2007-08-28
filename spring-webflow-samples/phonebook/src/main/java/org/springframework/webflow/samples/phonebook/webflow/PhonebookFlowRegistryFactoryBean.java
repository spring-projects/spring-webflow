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
package org.springframework.webflow.samples.phonebook.webflow;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;

/**
 * Demonstrates how to populate a flow registry programmatically.
 * 
 * @author Keith Donald
 * @author Ben Hale
 */
public class PhonebookFlowRegistryFactoryBean implements FactoryBean, InitializingBean {

	private FlowDefinitionRegistry registry;

	public void afterPropertiesSet() throws Exception {
		this.registry = new FlowDefinitionRegistryImpl();
		registerFlowDefinition(registry, "detail-flow", new PersonDetailFlowBuilder());
		registerFlowDefinition(registry, "search-flow", new SearchPersonFlowBuilder());
	}

	public Object getObject() throws Exception {
		return registry;
	}

	public Class getObjectType() {
		return FlowDefinitionRegistry.class;
	}

	public boolean isSingleton() {
		return true;
	}

	private void registerFlowDefinition(FlowDefinitionRegistry registry, String flowId, FlowBuilder flowBuilder) {
		FlowAssembler assembler = new FlowAssembler(flowId, flowBuilder);
		FlowDefinitionHolder holder = new StaticFlowDefinitionHolder(assembler.assembleFlow());
		registry.registerFlowDefinition(holder);
	}
}
