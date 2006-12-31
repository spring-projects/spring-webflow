/*
 * Copyright 2002-2007 the original author or authors.
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

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowServiceLocator;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockFlowServiceLocator;

/**
 * Base class for flow integration tests that verify an externalized flow
 * definition executes as expected. Supports caching of the flow definition
 * built from an externalized resource to speed up test execution.
 * 
 * @author Keith Donald
 */
public abstract class AbstractExternalizedFlowExecutionTests extends AbstractFlowExecutionTests {

	/**
	 * The cached flow definition.
	 */
	private static FlowDefinition cachedFlowDefinition;

	/**
	 * The flag indicating if the flow definition built from an externalized
	 * resource as part of this test should be cached.
	 */
	private boolean cacheFlowDefinition = false;
	
	/**
	 * Internal helper that return the flow execution factory used by the
	 * test cast to a {@link FlowExecutionImplFactory}.
	 */
	private FlowExecutionImplFactory getFlowExecutionImplFactory() {
		return (FlowExecutionImplFactory)getFlowExecutionFactory();
	}
	
	/**
	 * Returns if flow definition caching is turned on.
	 */
	protected boolean isCacheFlowDefinition() {
		return cacheFlowDefinition;
	}

	/**
	 * Sets the flag indicating if the flow definition built from an
	 * externalized resource as part of this test should be cached.
	 * Default is false.
	 */
	protected void setCacheFlowDefinition(boolean cacheFlowDefinition) {
		this.cacheFlowDefinition = cacheFlowDefinition;
	}

	/**
	 * Sets system attributes to be associated with the flow execution the next
	 * time one is {@link #startFlow() started} by this test. Useful for
	 * assigning attributes that influence flow execution behavior.
	 * @param executionAttributes the system attributes to assign
	 */
	protected void setFlowExecutionAttributes(AttributeMap executionAttributes) {
		getFlowExecutionImplFactory().setExecutionAttributes(executionAttributes);
	}

	/**
	 * Set the listener to be attached to the flow execution the next time one
	 * is {@link #startFlow() started} by this test. Useful for attaching a
	 * listener that does test assertions during the execution of the flow.
	 * @param executionListener the listener to attach
	 */
	protected void setFlowExecutionListener(FlowExecutionListener executionListener) {
		getFlowExecutionImplFactory().setExecutionListenerLoader(
				new StaticFlowExecutionListenerLoader(executionListener));
	}

	protected final FlowDefinition getFlowDefinition() {
		if (isCacheFlowDefinition() && cachedFlowDefinition != null) {
			return cachedFlowDefinition;
		}
		FlowServiceLocator flowServiceLocator = createFlowServiceLocator();
		Flow flow = createFlow(getFlowDefinitionResource(), flowServiceLocator);
		if (isCacheFlowDefinition()) {
			cachedFlowDefinition = flow;
		}
		return flow;
	}

	/**
	 * Returns the flow artifact factory to use during flow definition
	 * construction time for accessing externally managed flow artifacts such as
	 * actions and flows to be used as subflows.
	 * <p>
	 * This implementation just creates a {@link MockFlowServiceLocator} and
	 * populates it with services by calling {@link #registerMockServices(MockFlowServiceLocator)}.  
	 * @return the flow artifact factory
	 */
	protected FlowServiceLocator createFlowServiceLocator() {
		MockFlowServiceLocator serviceLocator = new MockFlowServiceLocator();
		registerMockServices(serviceLocator);
		return serviceLocator;
	}

	/**
	 * Template method called by {@link #createFlowServiceLocator()} to allow
	 * registration of mock implementations of services needed to test the flow
	 * execution. Useful when testing flow definitions in execution in isolation
	 * from flows and middle-tier services. Subclasses may override.
	 * @param serviceRegistry the mock service registry (and locator)
	 */
	protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
	}

	/**
	 * Factory method to assemble another flow definition from a resource.
	 * Called by {@link #getFlowDefinition()} to create the "main" flow to test.
	 * May also be called by subclasses to create subflow definitions whose
	 * executions should also be exercised by this test.
	 * @param resource the flow definition resource
	 * @return the built flow definition, ready for execution
	 * @see #createFlowBuilder(Resource, FlowServiceLocator)
	 */
	protected final Flow createFlow(FlowDefinitionResource resource, FlowServiceLocator serviceLocator) {
		FlowBuilder builder = createFlowBuilder(resource.getLocation(), serviceLocator);
		FlowAssembler assembler = new FlowAssembler(resource.getId(), resource.getAttributes(), builder);
		return assembler.assembleFlow();
	}

	/**
	 * Returns the pointer to the resource that houses the definition of the
	 * flow to be tested. Subclasses must implement.
	 * <p>
	 * Example usage:
	 * <pre class="code">
	 *     protected FlowDefinitionResource getFlowDefinitionResource() {
	 * 	      return createFlowDefinitionResource(&quot;/WEB-INF/flows/order-flow.xml&quot;);
	 *     }
	 * </pre>
	 * @return the flow definition resource
	 */
	protected abstract FlowDefinitionResource getFlowDefinitionResource();

	/**
	 * Factory method to create the builder that will build the flow whose
	 * execution will be tested. Subclasses must override.
	 * @param resource the externalized flow definition resource location
	 * @param serviceLocator the flow service locator
	 * @return the flow builder that will build the flow to be tested
	 */
	protected abstract FlowBuilder createFlowBuilder(Resource resource, FlowServiceLocator serviceLocator);

	/**
	 * Convenient factory method that creates a {@link FlowDefinitionResource}
	 * from a file path. Typically called by subclasses overriding
	 * {@link #getFlowDefinitionResource()}.
	 * @param filePath the full path to the externalized flow definition file
	 * @return the flow definition resource
	 */
	protected final FlowDefinitionResource createFlowDefinitionResource(String filePath) {
		return createFlowDefinitionResource(new File(filePath));
	}

	/**
	 * Convenient factory method that creates a {@link FlowDefinitionResource}
	 * from a file in a directory. Typically called by subclasses overriding
	 * {@link #getFlowDefinitionResource()}.
	 * @param fileDirectory the directory containing the file
	 * @param fileName the short file name
	 * @return the flow definition resource pointing to the file
	 */
	protected final FlowDefinitionResource createFlowDefinitionResource(String fileDirectory, String fileName) {
		return createFlowDefinitionResource(new File(fileDirectory, fileName));
	}

	/**
	 * Convenient factory method that creates a {@link FlowDefinitionResource}
	 * from a file.
	 * @param file the file
	 * @return the flow definition resource
	 */
	protected FlowDefinitionResource createFlowDefinitionResource(File file) {
		return new FlowDefinitionResource(new FileSystemResource(file));
	}
}