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

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowModelFlowBuilder;
import org.springframework.webflow.engine.model.builder.FlowModelBuilder;
import org.springframework.webflow.engine.model.builder.xml.XmlFlowModelBuilder;
import org.springframework.webflow.engine.model.registry.DefaultFlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelHolder;
import org.springframework.webflow.engine.model.registry.FlowModelRegistry;
import org.springframework.webflow.engine.model.registry.FlowModelRegistryImpl;

/**
 * Base class for flow integration tests that verify an XML flow definition executes as expected.
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class SearchFlowExecutionTests extends AbstractXmlFlowExecutionTests {
 * 
 * 	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
 * 		return resourceFactory.createClassPathResource(&quot;search-flow.xml&quot;, getClass());
 * 	}
 * 
 * 	public void testStartFlow() {
 * 		ExternalContext context = new MockExternalContext();
 * 		startFlow(context);
 * 		assertCurrentStateEquals(&quot;enterSearchCriteria&quot;);
 * 	}
 * 
 * 	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
 * 		builderContext.registerBean(&quot;searchService&quot;, new TestSearchService());
 * 	}
 * 
 * }
 * </pre>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public abstract class AbstractXmlFlowExecutionTests extends AbstractExternalizedFlowExecutionTests {

	private FlowModelRegistry flowModelRegistry;

	/**
	 * Constructs a default XML flow execution test.
	 * @see #setName(String)
	 */
	public AbstractXmlFlowExecutionTests() {
		super();
		flowModelRegistry = new FlowModelRegistryImpl();
	}

	/**
	 * Constructs an XML flow execution test with given name.
	 * @param name the name of the test
	 */
	public AbstractXmlFlowExecutionTests(String name) {
		super(name);
		flowModelRegistry = new FlowModelRegistryImpl();
	}

	protected FlowBuilder createFlowBuilder(FlowDefinitionResource resource) {
		FlowModelBuilder modelBuilder = new XmlFlowModelBuilder(resource.getPath(), flowModelRegistry);
		FlowModelHolder modelHolder = new DefaultFlowModelHolder(modelBuilder, resource.getId());
		flowModelRegistry.registerFlowModel(modelHolder);
		return new FlowModelFlowBuilder(modelHolder, resource.getPath()) {
			protected void registerFlowBeans(ConfigurableBeanFactory flowBeanFactory) {
				registerMockFlowBeans(flowBeanFactory);
			}
		};
	}

	/**
	 * Template method subclasses may override to register mock implementations of services used locally by the flow
	 * being tested. By default, this method does nothing.
	 * @param flowBeanFactory the local flow bean factory, you may register mock services with it using
	 * {@link ConfigurableBeanFactory#registerSingleton(String, Object)}
	 */
	protected void registerMockFlowBeans(ConfigurableBeanFactory flowBeanFactory) {
	}
}