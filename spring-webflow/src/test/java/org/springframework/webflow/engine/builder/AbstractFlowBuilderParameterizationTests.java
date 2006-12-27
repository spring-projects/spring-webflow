/*
 * Copyright 2002-2006 the original author or authors.
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

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Test parameterization of flow built using an AbstractFlowBuilder when
 * registering the flows with a FlowDefinitionRegistry.
 * 
 * @author Erwin Vervaet
 */
public class AbstractFlowBuilderParameterizationTests extends TestCase {
	
	private FlowDefinitionRegistry registry;
	
	protected void setUp() throws Exception {
		TestFlowRegistryFactoryBean registryFactory = new TestFlowRegistryFactoryBean();
		StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
		beanFactory.addBean("testAction", new TestAction());
		registryFactory.setBeanFactory(beanFactory);
		registryFactory.afterPropertiesSet();
		registry = registryFactory.getRegistry();
	}
	
	public void testFlowParameterization() {
		assertEquals(2, registry.getFlowDefinitionCount());
		assertTrue(registry.containsFlowDefinition("flowA"));
		Flow flowA = (Flow)registry.getFlowDefinition("flowA");
		assertEquals(2, flowA.getAttributes().size());
		assertEquals("A", flowA.getAttributes().get("name"));
		assertEquals("someValue", flowA.getAttributes().get("someKey"));
		assertNull(flowA.getAttributes().get("someOtherKey"));
		
		assertTrue(registry.containsFlowDefinition("flowB"));
		Flow flowB = (Flow)registry.getFlowDefinition("flowB");
		assertEquals(2, flowB.getAttributes().size());
		assertEquals("B", flowB.getAttributes().get("name"));
		assertEquals("someOtherValue", flowB.getAttributes().get("someOtherKey"));
		assertNull(flowB.getAttributes().get("someKey"));
	}
	
	public void testFlowParameterizationAtRuntime() {
		Flow flowA = (Flow)registry.getFlowDefinition("flowA");
		flowA.start(new MockRequestControlContext(flowA), null);
		
		Flow flowB = (Flow)registry.getFlowDefinition("flowB");
		flowB.start(new MockRequestControlContext(flowB), null);
	}
	
	public class TestFlowBuilder extends AbstractFlowBuilder {
		
		public TestFlowBuilder(FlowServiceLocator serviceLocator) {
			super(serviceLocator);
		}
		
		public void buildStates() throws FlowBuilderException {
			addActionState("test", action("testAction"), transition(on(success()), to("finish")));
			addEndState("finish");
		}
	}
	
	public class TestAction extends AbstractAction {
		
		protected Event doExecute(RequestContext context) throws Exception {
			if ("flowA".equals(context.getActiveFlow().getId())) {
				Flow flowA = (Flow)context.getActiveFlow();
				assertEquals(2, flowA.getAttributes().size());
				assertEquals("A", flowA.getAttributes().get("name"));
				assertEquals("someValue", flowA.getAttributes().get("someKey"));
				assertNull(flowA.getAttributes().get("someOtherKey"));
			}
			else if ("flowB".equals(context.getActiveFlow().getId())) {
				Flow flowB = (Flow)context.getActiveFlow();
				assertEquals(2, flowB.getAttributes().size());
				assertEquals("B", flowB.getAttributes().get("name"));
				assertEquals("someOtherValue", flowB.getAttributes().get("someOtherKey"));
				assertNull(flowB.getAttributes().get("someKey"));
			}
			else {
				throw new IllegalStateException();
			}
			return success();
		}
	}
	
	public class TestFlowRegistryFactoryBean extends AbstractFlowBuildingFlowRegistryFactoryBean {

		protected void doPopulate(FlowDefinitionRegistry registry) {
			MutableAttributeMap attributes = new LocalAttributeMap();
			attributes.put("name", "A");
			attributes.put("someKey", "someValue");
			registerFlow(registry, "flowA", attributes, new TestFlowBuilder(getFlowServiceLocator()));
			
			attributes = new LocalAttributeMap();
			attributes.put("name", "B");
			attributes.put("someOtherKey", "someOtherValue");
			registerFlow(registry, "flowB", attributes, new TestFlowBuilder(getFlowServiceLocator()));
		}
		
		private void registerFlow(FlowDefinitionRegistry registry, String flowId, AttributeMap attributes, FlowBuilder builder) {
			FlowDefinition flow = new FlowAssembler(flowId, attributes, builder).assembleFlow();
			registry.registerFlowDefinition(new StaticFlowDefinitionHolder(flow));
		}
	}

}
