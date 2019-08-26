/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.definition.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;

/**
 * Unit tests for {@link FlowDefinitionRegistryImpl}.
 */
public class FlowDefinitionRegistryImplTests {

	private FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();

	private FooFlow fooFlow;

	private BarFlow barFlow;

	@BeforeEach
	public void setUp() {
		fooFlow = new FooFlow();
		barFlow = new BarFlow();
	}

	@Test
	public void testNoSuchFlowDefinition() {
		try {
			registry.getFlowDefinition("bogus");
			fail("Should've bombed with NoSuchFlow");
		} catch (NoSuchFlowDefinitionException e) {

		}
	}

	@Test
	public void testNullFlowDefinitionId() {
		try {
			registry.getFlowDefinition(null);
			fail("Should have bombed with illegal argument");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testBlankFlowDefinitionId() {
		try {
			registry.getFlowDefinition("");
			fail("Should have bombed with illegal argument");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testRegisterFlow() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertTrue(registry.containsFlowDefinition("foo"));
		assertEquals(fooFlow, registry.getFlowDefinition("foo"));
	}

	@Test
	public void testGetFlowIds() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(barFlow));
		assertEquals("bar", registry.getFlowDefinitionIds()[0]);
		assertEquals("foo", registry.getFlowDefinitionIds()[1]);
	}

	@Test
	public void testRegisterFlowSameIds() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		FooFlow newFlow = new FooFlow();
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(newFlow));
		assertSame(newFlow, registry.getFlowDefinition("foo"));
	}

	@Test
	public void testRegisterMultipleFlows() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(barFlow));
		assertTrue(registry.containsFlowDefinition("foo"));
		assertTrue(registry.containsFlowDefinition("bar"));
		assertEquals(fooFlow, registry.getFlowDefinition("foo"));
		assertEquals(barFlow, registry.getFlowDefinition("bar"));
	}

	@Test
	public void testParentHierarchy() {
		testRegisterMultipleFlows();
		FlowDefinitionRegistryImpl child = new FlowDefinitionRegistryImpl();
		child.setParent(registry);
		FooFlow fooFlow = new FooFlow();
		child.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		assertTrue(child.containsFlowDefinition("foo"));
		assertTrue(child.containsFlowDefinition("bar"));
		assertSame(fooFlow, child.getFlowDefinition("foo"));
		assertEquals(barFlow, child.getFlowDefinition("bar"));
	}

	@Test
	public void testDestroy() {
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(fooFlow));
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(barFlow));
		assertEquals(fooFlow, registry.getFlowDefinition("foo"));
		assertEquals(barFlow, registry.getFlowDefinition("bar"));
		assertFalse(fooFlow.destroyed);
		assertFalse(barFlow.destroyed);
		registry.destroy();
		assertTrue(fooFlow.destroyed);
		assertTrue(barFlow.destroyed);
	}

	private static class FooFlow implements FlowDefinition {
		private String id = "foo";

		private boolean destroyed;

		public MutableAttributeMap<Object> getAttributes() {
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

		public String[] getPossibleOutcomes() {
			return null;
		}

		public ClassLoader getClassLoader() {
			return null;
		}

		public ApplicationContext getApplicationContext() {
			return null;
		}

		public boolean inDevelopment() {
			return false;
		}

		public void destroy() {
			destroyed = true;
		}

	}

	private static class BarFlow implements FlowDefinition {
		private String id = "bar";

		private boolean destroyed;

		public MutableAttributeMap<Object> getAttributes() {
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

		public String[] getPossibleOutcomes() {
			return null;
		}

		public ClassLoader getClassLoader() {
			return null;
		}

		public ApplicationContext getApplicationContext() {
			return null;
		}

		public boolean inDevelopment() {
			return false;
		}

		public void destroy() {
			destroyed = true;
		}

	}
}
