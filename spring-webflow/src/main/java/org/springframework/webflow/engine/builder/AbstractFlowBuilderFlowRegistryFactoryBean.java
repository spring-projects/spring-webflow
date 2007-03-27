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
package org.springframework.webflow.engine.builder;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.AbstractFlowBuildingFlowRegistryFactoryBean;
import org.springframework.webflow.engine.builder.FlowAssembler;

/**
 * Base class for factory beans that create flow definition registries containing
 * flows built using Java based {@link AbstractFlowBuilder flow builders}.
 * <p>
 * Subclasses only need to define the {@link #doPopulate(FlowDefinitionRegistry)}
 * method and use the
 * {@link #registerFlowDefinition(FlowDefinitionRegistry, String, AbstractFlowBuilder)}
 * convenience methods provided by this class to register all relevant flows:
 * 
 * <pre class="code">
 * public class MyFlowRegistryFactoryBean extends AbstractFlowBuilderFlowRegistryFactoryBean {
 * 	protected void doPopulate(FlowDefinitionRegistry registry) {
 * 		registerFlowDefinition(registry, "my-flow", new MyFlowBuilder());
 * 		registerFlowDefinition(registry, "my-other-flow", new MyOtherFlowBuilder());
 * 	}
 * }
 * </pre>
 * 
 * @see AbstractFlowBuilder
 * 
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowBuilderFlowRegistryFactoryBean extends
		AbstractFlowBuildingFlowRegistryFactoryBean {

	/**
	 * Register the flow built by given flow builder in specified flow
	 * definition registry.
	 * <p>
	 * Note that this method will set the
	 * {@link #getFlowServiceLocator() flow service locator} of this class
	 * on given flow builder.
	 * @param registry the registry to register the flow in
	 * @param flowId the flow id to assign
	 * @param flowBuilder the builder used to build the flow
	 */
	protected void registerFlowDefinition(
			FlowDefinitionRegistry registry, String flowId, AbstractFlowBuilder flowBuilder) {
		registerFlowDefinition(registry, flowId, null, flowBuilder);
	}
	
	/**
	 * Register the flow built by given flow builder in specified flow
	 * definition registry.
	 * <p>
	 * Note that this method will set the
	 * {@link #getFlowServiceLocator() flow service locator} of this class
	 * on given flow builder.
	 * @param registry the registry to register the flow in
	 * @param flowId the flow id to assign
	 * @param flowAttributes externally assigned flow attributes that can affect
	 * flow construction
	 * @param flowBuilder the builder used to build the flow
	 */
	protected void registerFlowDefinition(
			FlowDefinitionRegistry registry, String flowId, AttributeMap flowAttributes, AbstractFlowBuilder flowBuilder) {
		flowBuilder.setFlowServiceLocator(getFlowServiceLocator());
		Flow flow = new FlowAssembler(flowId, flowAttributes, flowBuilder).assembleFlow();
		FlowDefinitionHolder flowHolder = new StaticFlowDefinitionHolder(flow);
		registry.registerFlowDefinition(flowHolder);
	}
}
