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
package org.springframework.webflow.engine.builder;

import org.springframework.util.Assert;
import org.springframework.webflow.engine.Flow;

/**
 * A director for assembling flows, delegating to a {@link FlowBuilder} to construct a flow. This class encapsulates the
 * algorithm for using a FlowBuilder to assemble a Flow properly. It acts as the director in the classic GoF builder
 * pattern.
 * <p>
 * Flow assemblers may be used in a standalone, programmatic fashion as follows:
 * 
 * <pre>
 *     FlowBuilder builder = ...;
 *     FlowBuilder context = ...;
 *     Flow flow = new FlowAssembler(builder, builderContext).assembleFlow();
 * </pre>
 * 
 * @see org.springframework.webflow.engine.builder.FlowBuilder
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class FlowAssembler {

	/**
	 * The flow builder strategy used to construct the flow from its component parts.
	 */
	private FlowBuilder flowBuilder;

	/**
	 * Context needed to initialize the builder so it can perform a build operation.
	 */
	private FlowBuilderContext flowBuilderContext;

	/**
	 * Create a new flow assembler that will direct Flow assembly using the specified builder strategy.
	 * @param flowBuilder the builder the factory will use to build flows
	 * @param flowBuilderContext context to influence the build process
	 */
	public FlowAssembler(FlowBuilder flowBuilder, FlowBuilderContext flowBuilderContext) {
		Assert.notNull(flowBuilder, "A flow builder is required for flow assembly");
		Assert.notNull(flowBuilderContext, "A flow builder context is required for flow assembly");
		this.flowBuilder = flowBuilder;
		this.flowBuilderContext = flowBuilderContext;
	}

	/**
	 * Returns the flow builder strategy used to construct the flow from its component parts.
	 */
	public FlowBuilder getFlowBuilder() {
		return flowBuilder;
	}

	/**
	 * Returns the flow builder context.
	 * @return flow builder context
	 */
	public FlowBuilderContext getFlowBuilderContext() {
		return flowBuilderContext;
	}

	/**
	 * Assembles the flow, directing the construction process by delegating to the configured FlowBuilder. Every call to
	 * this method will assemble the Flow instance.
	 * <p>
	 * This will drive the flow construction process as described in the {@link FlowBuilder} JavaDoc, starting with
	 * builder initialization using {@link FlowBuilder#init(FlowBuilderContext)} and finishing by cleaning up the
	 * builder with a call to {@link FlowBuilder#dispose()}.
	 * @return the constructed flow
	 * @throws FlowBuilderException when flow assembly fails
	 */
	public Flow assembleFlow() throws FlowBuilderException {
		try {
			flowBuilder.init(flowBuilderContext);
			directAssembly();
			return flowBuilder.getFlow();
		} finally {
			flowBuilder.dispose();
		}
	}

	/**
	 * Build all parts of the flow by directing flow assembly by the flow builder.
	 * @throws FlowBuilderException when flow assembly fails
	 */
	protected void directAssembly() throws FlowBuilderException {
		flowBuilder.buildVariables();
		flowBuilder.buildInputMapper();
		flowBuilder.buildStartActions();
		flowBuilder.buildStates();
		flowBuilder.buildGlobalTransitions();
		flowBuilder.buildEndActions();
		flowBuilder.buildOutputMapper();
		flowBuilder.buildExceptionHandlers();
	}
}