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
package org.springframework.webflow.definition.registry;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;

/**
 * Unit tests for {@link FlowDefinitionRegistryImpl}.
 */
public class FlowDefinitionRegistryImplTests extends TestCase {
	
	private FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();

	private FlowDefinition fooFlow;

	protected void setUp() {
		fooFlow = new FooFlow();
	}

	public void testEmptyRegistryAsserts() {
		assertEquals(0, registry.getFlowDefinitionCount());
		assertEquals(0, registry.getFlowDefinitionIds().length);
		assertEquals(0, registry.getFlowDefinitions().length);
	}

	public void testNoSuchFlowDefinition() {
		try {
			registry.getFlowDefinition("bogus");
			fail("Should've bombed with NoSuchFlow");
		}
		catch (NoSuchFlowDefinitionException e) {

		}
	}

	public void testRegisterFlow() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertEquals(1, registry.getFlowDefinitionCount());
		assertEquals("foo", registry.getFlowDefinitionIds()[0]);
		assertEquals("foo", registry.getFlowDefinitions()[0].getId());
		assertEquals("foo", registry.getFlowDefinition("foo").getId());
	}

	public void testRegisterFlowSameIds() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		FooFlow newFlow = new FooFlow();
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(newFlow));
		assertEquals(1, registry.getFlowDefinitionCount());
		assertSame(newFlow, registry.getFlowDefinition("foo"));
	}

	public void testRegisterMultipleFlows() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		FooFlow newFlow = new FooFlow();
		newFlow.id = "bar";
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(newFlow));
		assertEquals(2, registry.getFlowDefinitionCount());
		assertSame(fooFlow, registry.getFlowDefinition("foo"));
		assertSame(newFlow, registry.getFlowDefinition("bar"));
	}

	public void testRefresh() {
		testRegisterMultipleFlows();
		registry.refresh();
		assertEquals(2, registry.getFlowDefinitionCount());
		assertSame(fooFlow, registry.getFlowDefinition("foo"));
	}
	
	public void testRefreshValidFlow() {
		testRegisterMultipleFlows();
		registry.refresh("foo");
		assertEquals(2, registry.getFlowDefinitionCount());
		assertSame(fooFlow, registry.getFlowDefinition("foo"));
	}

	public void testRefreshNoSuchFlow() {
		testRegisterMultipleFlows();
		try {
			registry.refresh("bogus");
			fail("Should've bombed with NoSuchFlow");
		}
		catch (NoSuchFlowDefinitionException e) {

		}
	}
	
	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowDefinitionRegistryImpl child = new FlowDefinitionRegistryImpl();
		child.setParent(registry);
		FooFlow fooFlow = new FooFlow();
		child.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertSame(fooFlow, child.getFlowDefinition("foo"));
		assertEquals("bar", child.getFlowDefinition("bar").getId());
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
	}
}