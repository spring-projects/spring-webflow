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
package org.springframework.webflow.test.execution;

import org.springframework.core.io.Resource;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockFlowBuilderContext;

/**
 * Base class for flow integration tests that verify an externalized flow definition executes as expected. Supports
 * caching of the flow definition built from an externalized resource to speed up test execution.
 * 
 * @author Keith Donald
 */
public abstract class AbstractExternalizedFlowExecutionTests extends AbstractFlowExecutionTests {

	/**
	 * The cached flow definition.
	 */
	private static FlowDefinition cachedFlowDefinition;

	/**
	 * The flag indicating if the flow definition built from an externalized resource as part of this test should be
	 * cached.
	 */
	private boolean cacheFlowDefinition = false;

	/**
	 * A helper for constructing paths to flow definition resources in the filesystem, classpath, or other location.
	 */
	private FlowDefinitionResourceFactory resourceFactory = new FlowDefinitionResourceFactory();

	/**
	 * Constructs a default externalized flow execution test.
	 * @see #setName(String)
	 */
	public AbstractExternalizedFlowExecutionTests() {
		super();
	}

	/**
	 * Constructs an externalized flow execution test with given name.
	 * @param name the name of the test
	 * @since 1.0.2
	 */
	public AbstractExternalizedFlowExecutionTests(String name) {
		super(name);
	}

	/**
	 * Internal helper that returns the flow execution factory used by the test cast to a
	 * {@link FlowExecutionImplFactory}.
	 */
	private FlowExecutionImplFactory getFlowExecutionImplFactory() {
		return (FlowExecutionImplFactory) getFlowExecutionFactory();
	}

	/**
	 * Returns if flow definition caching is turned on.
	 */
	protected boolean isCacheFlowDefinition() {
		return cacheFlowDefinition;
	}

	/**
	 * Sets the flag indicating if the flow definition built from an externalized resource as part of this test should
	 * be cached. Default is false.
	 */
	protected void setCacheFlowDefinition(boolean cacheFlowDefinition) {
		this.cacheFlowDefinition = cacheFlowDefinition;
	}

	/**
	 * Sets system attributes to be associated with the flow execution the next time one is started. by this test.
	 * Useful for assigning attributes that influence flow execution behavior.
	 * @param executionAttributes the system attributes to assign
	 */
	protected void setFlowExecutionAttributes(AttributeMap executionAttributes) {
		getFlowExecutionImplFactory().setExecutionAttributes(executionAttributes);
	}

	/**
	 * Set a single listener to be attached to the flow execution the next time one is started by this test. Useful for
	 * attaching a listener that does test assertions during the execution of the flow.
	 * @param executionListener the listener to attach
	 */
	protected void setFlowExecutionListener(FlowExecutionListener executionListener) {
		getFlowExecutionImplFactory().setExecutionListenerLoader(
				new StaticFlowExecutionListenerLoader(executionListener));
	}

	/**
	 * Set the listeners to be attached to the flow execution the next time one is started. by this test. Useful for
	 * attaching listeners that do test assertions during the execution of the flow.
	 * @param executionListeners the listeners to attach
	 */
	protected void setFlowExecutionListeners(FlowExecutionListener[] executionListeners) {
		getFlowExecutionImplFactory().setExecutionListenerLoader(
				new StaticFlowExecutionListenerLoader(executionListeners));
	}

	/**
	 * Returns the flow definition being tested.
	 */
	protected final FlowDefinition getFlowDefinition() {
		if (isCacheFlowDefinition() && cachedFlowDefinition != null) {
			return cachedFlowDefinition;
		}
		Flow flow = buildFlow();
		if (isCacheFlowDefinition()) {
			cachedFlowDefinition = flow;
		}
		return flow;
	}

	/**
	 * Factory method to assemble a flow definition from a resource. Called by {@link #getFlowDefinition()} to create
	 * the "main" flow to test. May also be called by subclasses to create subflow definitions whose executions should
	 * also be exercised by this test.
	 * @return the built flow definition, ready for execution
	 */
	protected final Flow buildFlow() {
		FlowDefinitionResource resource = getResource(resourceFactory);
		FlowBuilderContext builderContext = createFlowBuilderContext(resource);
		FlowBuilder builder = createFlowBuilder(resource.getPath());
		FlowAssembler assembler = new FlowAssembler(builder, builderContext);
		return assembler.assembleFlow();
	}

	/**
	 * Create the flow builder context to build the flow definition at the resource location provided.
	 * @param resource the flow definition resource
	 * @return the flow builder context
	 */
	protected FlowBuilderContext createFlowBuilderContext(FlowDefinitionResource resource) {
		MockFlowBuilderContext builderContext = new MockFlowBuilderContext(resource.getId(), resource.getAttributes());
		configure(builderContext);
		return builderContext;
	}

	/**
	 * Subclasses may override this hook to customize the builder context for the flow being tested. Useful for
	 * registering mock subflows or other builder services.
	 * @param builderContext the mock flow builder context.
	 */
	protected void configure(MockFlowBuilderContext builderContext) {

	}

	/**
	 * Get the flow definition to be tested.
	 * @param resourceFactory a helper for constructing the resource to be tested
	 * @return the flow definition resource
	 */
	protected abstract FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory);

	/**
	 * Create the flow builder to build the flow at the specified resource location.
	 * @param path the location of the flow definition
	 * @return the flow builder that can build the flow definition
	 */
	protected abstract FlowBuilder createFlowBuilder(Resource path);

}