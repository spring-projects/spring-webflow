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
package org.springframework.webflow.definition.registry;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;

/**
 * Unit tests for {@link FlowDefinitionRegistryImpl}.
 */
public class FlowDefinitionRegistryImplTests extends TestCase {

	private FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();

	private FlowDefinition fooFlow;

	private FlowDefinition barFlow;

	protected void setUp() {
		fooFlow = new FooFlow();
		barFlow = new BarFlow();
	}

	public void testNoSuchFlowDefinition() {
		try {
			registry.getFlowDefinition("bogus");
			fail("Should've bombed with NoSuchFlow");
		} catch (NoSuchFlowDefinitionException e) {

		}
	}

	public void testRegisterFlow() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertEquals(fooFlow, registry.getFlowDefinition("foo"));
	}

	public void testRegisterFlowSameIds() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		FooFlow newFlow = new FooFlow();
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(newFlow));
		assertSame(newFlow, registry.getFlowDefinition("foo"));
	}

	public void testRegisterMultipleFlows() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(barFlow));
		assertEquals(fooFlow, registry.getFlowDefinition("foo"));
		assertEquals(barFlow, registry.getFlowDefinition("bar"));
	}

	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowDefinitionRegistryImpl child = new FlowDefinitionRegistryImpl();
		child.setParent(registry);
		FooFlow fooFlow = new FooFlow();
		child.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertSame(fooFlow, child.getFlowDefinition("foo"));
		assertEquals(barFlow, child.getFlowDefinition("bar"));
	}

	private static class FooFlow implements FlowDefinition {
		private String id = "foo";

		public AttributeMap getAttributes() {
			return null;
		}

		public String getCaption() {
			return null;
		}

		public String getDescription() {
			return null;
		}

		public String getId() {
			return id;
		}

		public StateDefinition getStartState() {
			return null;
		}

		public StateDefinition getState(String id) throws IllegalArgumentException {
			return null;
		}

		public BeanFactory getBeanFactory() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ResourceLoader getResourceLoader() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}
	}

	private static class BarFlow implements FlowDefinition {
		private String id = "bar";

		public AttributeMap getAttributes() {
			return null;
		}

		public String getCaption() {
			return null;
		}

		public String getDescription() {
			return null;
		}

		public String getId() {
			return id;
		}

		public StateDefinition getStartState() {
			return null;
		}

		public StateDefinition getState(String id) throws IllegalArgumentException {
			return null;
		}

		public BeanFactory getBeanFactory() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public ResourceLoader getResourceLoader() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}
	}
}