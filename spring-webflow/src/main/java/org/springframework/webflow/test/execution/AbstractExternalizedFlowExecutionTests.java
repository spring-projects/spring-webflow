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
package org.springframework.webflow.test.execution;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockFlowBuilderContext;

/**
 * Base class for flow integration tests that verify an externalized flow definition executes as expected. Supports
 * caching of the flow definition built from an externalized resource to speed up test execution.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public abstract class AbstractExternalizedFlowExecutionTests extends AbstractFlowExecutionTests {

	/**
	 * The cached flow definition.
	 */
	private static Flow cachedFlowDefinition;

	/**
	 * The flag indicating if the flow definition built from an externalized resource as part of this test should be
	 * cached.
	 */
	private boolean cacheFlowDefinition;

	/**
	 * A helper for constructing paths to flow definition resources in the filesystem, classpath, or other location.
	 */
	private FlowDefinitionResourceFactory resourceFactory;

	/**
	 * Private flow builder context object.
	 */
	private MockFlowBuilderContext flowBuilderContext;

	/**
	 * Constructs a default externalized flow execution test.
	 * @see #setName(String)
	 */
	public AbstractExternalizedFlowExecutionTests() {
		init();
	}

	/**
	 * Constructs an externalized flow execution test with given name.
	 * @param name the name of the test
	 */
	public AbstractExternalizedFlowExecutionTests(String name) {
		super(name);
		init();
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
	 * Returns the factory used to create pointers to externalized flow definition resources.
	 * @return the resource factory
	 */
	protected FlowDefinitionResourceFactory getResourceFactory() {
		return resourceFactory;
	}

	/**
	 * Returns the {@link ResourceLoader} used by the {@link FlowDefinitionResourceFactory} to load flow resources from
	 * a path. Subclasses may override to customize the resource loader used.
	 * @see #getResourceFactory()
	 * @return the resource loader
	 */
	protected ResourceLoader createResourceLoader() {
		return new DefaultResourceLoader();
	}

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
	 * Returns the flow definition being tested as a {@link Flow} implementation. Useful if you need to do specific
	 * assertions against the configuration of the implementation.
	 */
	protected final Flow getFlow() {
		return (Flow) getFlowDefinition();
	}

	/**
	 * Factory method to assemble a flow definition from a resource. Called by {@link #getFlowDefinition()} to create
	 * the "main" flow to test. May also be called by subclasses to create subflow definitions whose executions should
	 * also be exercised by this test.
	 * @return the built flow definition, ready for execution
	 */
	protected final Flow buildFlow() {
		FlowDefinitionResource resource = getResource(getResourceFactory());
		flowBuilderContext = new MockFlowBuilderContext(resource.getId(), resource.getAttributes());
		configureFlowBuilderContext(flowBuilderContext);
		FlowBuilder builder = createFlowBuilder(resource);
		FlowAssembler assembler = new FlowAssembler(builder, flowBuilderContext);
		return assembler.assembleFlow();
	}

	/**
	 * Subclasses may override this hook to customize the builder context for the flow being tested. Useful for
	 * registering mock subflows or other {@link FlowBuilderServices flow builder services}. By default, this method
	 * does nothing.
	 * @param builderContext the mock flow builder context to configure
	 */
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {

	}

	/**
	 * Returns a reference to the flow definition registry used by the flow being tested to load subflows. Allows late
	 * registration of dependent subflows on a per test-case basis. This is an alternative to registering such subflows
	 * upfront in {@link #configureFlowBuilderContext(MockFlowBuilderContext)}.
	 * @return the flow definition registry
	 */
	protected FlowDefinitionRegistry getFlowDefinitionRegistry() {
		return (FlowDefinitionRegistry) flowBuilderContext.getFlowDefinitionLocator();
	}

	/**
	 * Get the resource defining the flow to be tested.
	 * @param resourceFactory a helper for constructing the resource to be tested
	 * @return the flow definition resource
	 */
	protected abstract FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory);

	/**
	 * Create the flow builder to build the flow at the specified resource location.
	 * @param resource the resource location of the flow definition
	 * @return the flow builder that can build the flow definition
	 */
	protected abstract FlowBuilder createFlowBuilder(FlowDefinitionResource resource);

	// internal helpers

	private void init() {
		resourceFactory = new FlowDefinitionResourceFactory(createResourceLoader());
	}

	private FlowExecutionImplFactory getFlowExecutionImplFactory() {
		return (FlowExecutionImplFactory) getFlowExecutionFactory();
	}

}