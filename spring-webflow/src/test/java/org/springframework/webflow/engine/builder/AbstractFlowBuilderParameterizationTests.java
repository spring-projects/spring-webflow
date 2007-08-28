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

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionHolder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistrar;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Test parameterization of flow built using an AbstractFlowBuilder when registering the flows with a
 * FlowDefinitionRegistry.
 */
public class AbstractFlowBuilderParameterizationTests extends TestCase {

	private FlowDefinitionRegistry registry;

	protected void setUp() throws Exception {
		registry = new FlowDefinitionRegistryImpl();
		MockFlowServiceLocator flowServiceLocator = new MockFlowServiceLocator();
		flowServiceLocator.registerBean("testAction", new ParameterizationTestAction());
		new TestFlowRegistrar(flowServiceLocator).registerFlowDefinitions(registry);
	}

	public void testFlowParameterization() {
		assertEquals(2, registry.getFlowDefinitionCount());
		assertTrue(registry.containsFlowDefinition("flowA"));
		Flow flowA = (Flow) registry.getFlowDefinition("flowA");
		assertEquals(2, flowA.getAttributes().size());
		assertEquals("A", flowA.getAttributes().get("name"));
		assertEquals("someValue", flowA.getAttributes().get("someKey"));
		assertNull(flowA.getAttributes().get("someOtherKey"));

		assertTrue(registry.containsFlowDefinition("flowB"));
		Flow flowB = (Flow) registry.getFlowDefinition("flowB");
		assertEquals(2, flowB.getAttributes().size());
		assertEquals("B", flowB.getAttributes().get("name"));
		assertEquals("someOtherValue", flowB.getAttributes().get("someOtherKey"));
		assertNull(flowB.getAttributes().get("someKey"));
	}

	public void testFlowParameterizationAtRuntime() {
		Flow flowA = (Flow) registry.getFlowDefinition("flowA");
		ViewSelection viewSelection = flowA.start(new MockRequestControlContext(flowA), null);
		assertEquals("A", ((ApplicationView) viewSelection).getViewName());

		Flow flowB = (Flow) registry.getFlowDefinition("flowB");
		viewSelection = flowB.start(new MockRequestControlContext(flowB), null);
		assertEquals("B", ((ApplicationView) viewSelection).getViewName());
	}

	public class TestFlowBuilder extends AbstractFlowBuilder {

		public TestFlowBuilder(FlowServiceLocator flowServiceLocator) {
			setFlowServiceLocator(flowServiceLocator);
		}

		public void buildStates() throws FlowBuilderException {
			addActionState("test", action("testAction"), transition(on(success()), to("finish")));
			addEndState("finish", "${activeFlow.attributes['name']}");
		}
	}

	private class TestFlowRegistrar implements FlowDefinitionRegistrar {

		private FlowServiceLocator flowServiceLocator;

		public TestFlowRegistrar(FlowServiceLocator flowServiceLocator) {
			this.flowServiceLocator = flowServiceLocator;
		}

		public void registerFlowDefinitions(FlowDefinitionRegistry registry) {
			MutableAttributeMap attributesA = new LocalAttributeMap();
			attributesA.put("name", "A");
			attributesA.put("someKey", "someValue");
			Flow flowA = new FlowAssembler("flowA", attributesA, new TestFlowBuilder(flowServiceLocator))
					.assembleFlow();
			FlowDefinitionHolder flowHolderA = new StaticFlowDefinitionHolder(flowA);
			registry.registerFlowDefinition(flowHolderA);

			MutableAttributeMap attributesB = new LocalAttributeMap();
			attributesB.put("name", "B");
			attributesB.put("someOtherKey", "someOtherValue");
			Flow flowB = new FlowAssembler("flowB", attributesB, new TestFlowBuilder(flowServiceLocator))
					.assembleFlow();
			FlowDefinitionHolder flowHolderB = new StaticFlowDefinitionHolder(flowB);
			registry.registerFlowDefinition(flowHolderB);
		}
	}

}
