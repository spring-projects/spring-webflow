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
package org.springframework.webflow.engine.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowExecutionKey;

/**
 * Test case for {@link FlowExecutionImplFactory}.
 */
public class FlowExecutionImplFactoryTests {

	private FlowExecutionImplFactory factory = new FlowExecutionImplFactory();

	private Flow flowDefinition;

	private boolean starting;

	private boolean getKeyCalled;

	private boolean updateSnapshotCalled;

	private boolean removeSnapshotCalled;

	private boolean removeAllSnapshotsCalled;

	@BeforeEach
	public void setUp() {
		flowDefinition = new Flow("flow");
		new EndState(flowDefinition, "end");
	}

	@Test
	public void testCreate() {
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertSame(flowDefinition, execution.getDefinition());
		assertFalse(execution.hasStarted());
		assertFalse(execution.isActive());
	}

	@Test
	public void testCreateNullArgument() {
		try {
			factory.createFlowExecution(null);
			fail("Should've failed");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testCreateWithExecutionAttributes() {
		MutableAttributeMap<Object> attributes = new LocalAttributeMap<>();
		attributes.put("foo", "bar");
		factory.setExecutionAttributes(attributes);
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertEquals(attributes, execution.getAttributes());
		assertSame(attributes.asMap(), execution.getAttributes().asMap(), "Flow execution attributes are global");
	}

	@Test
	public void testCreateWithExecutionListener() {
		FlowExecutionListener listener1 = new FlowExecutionListener() {
			public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap<?> input) {
				starting = true;
			}
		};
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener1));
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertFalse(execution.isActive());
		execution.start(null, new MockExternalContext());
		assertTrue(starting);
	}

	@Test
	public void testCreateWithExecutionKeyFactory() {
		State state = new State(flowDefinition, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				context.assignFlowExecutionKey();
				context.updateCurrentFlowExecutionSnapshot();
				context.removeCurrentFlowExecutionSnapshot();
				context.removeAllFlowExecutionSnapshots();
			}
		};
		flowDefinition.setStartState(state);
		factory.setExecutionKeyFactory(new MockFlowExecutionKeyFactory());
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		execution.start(null, new MockExternalContext());
		assertTrue(getKeyCalled);
		assertTrue(removeAllSnapshotsCalled);
		assertTrue(removeSnapshotCalled);
		assertTrue(updateSnapshotCalled);
		assertNull(execution.getKey());
	}

	@Test
	public void testRestoreExecutionState() {
		FlowExecutionImpl flowExecution = (FlowExecutionImpl) factory.createFlowExecution(flowDefinition);
		LocalAttributeMap<Object> executionAttributes = new LocalAttributeMap<>();
		factory.setExecutionAttributes(executionAttributes);
		FlowExecutionListener listener = new FlowExecutionListener() {
		};
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener));
		MockFlowExecutionKeyFactory keyFactory = new MockFlowExecutionKeyFactory();
		factory.setExecutionKeyFactory(keyFactory);
		FlowExecutionKey flowExecutionKey = new MockFlowExecutionKey("e1s1");
		LocalAttributeMap<Object> conversationScope = new LocalAttributeMap<>();
		SimpleFlowDefinitionLocator locator = new SimpleFlowDefinitionLocator();
		FlowSessionImpl session1 = new FlowSessionImpl();
		session1.setFlowId("flow");
		session1.setStateId("end");
		FlowSessionImpl session2 = new FlowSessionImpl();
		session2.setFlowId("child");
		session2.setStateId("state");
		flowExecution.getFlowSessions().add(session1);
		flowExecution.getFlowSessions().add(session2);
		factory.restoreFlowExecution(flowExecution, flowDefinition, flowExecutionKey, conversationScope, locator);
		assertSame(flowExecution.getAttributes().asMap(), executionAttributes.asMap(),
				"Flow execution attributes are global");
		assertEquals(1, flowExecution.getListeners().length);
		assertSame(listener, flowExecution.getListeners()[0]);
		assertSame(flowExecutionKey, flowExecution.getKey());
		assertSame(keyFactory, flowExecution.getKeyFactory());
		assertSame(conversationScope, flowExecution.getConversationScope());
		assertSame(flowExecution.getFlowSessions().get(0).getDefinition(), flowDefinition);
		assertSame(flowExecution.getFlowSessions().get(0).getDefinition().getState("end"),
				flowDefinition.getState("end"));
		assertSame(flowExecution.getFlowSessions().get(1).getDefinition(), locator.child);
		assertSame(flowExecution.getFlowSessions().get(1).getDefinition().getState("state"),
				locator.child.getState("state"));
	}

	private class MockFlowExecutionKeyFactory implements FlowExecutionKeyFactory {
		public FlowExecutionKey getKey(FlowExecution execution) {
			getKeyCalled = true;
			return null;
		}

		public void removeAllFlowExecutionSnapshots(FlowExecution execution) {
			removeAllSnapshotsCalled = true;
		}

		public void removeFlowExecutionSnapshot(FlowExecution execution) {
			removeSnapshotCalled = true;
		}

		public void updateFlowExecutionSnapshot(FlowExecution execution) {
			updateSnapshotCalled = true;
		}
	}

	private class SimpleFlowDefinitionLocator implements FlowDefinitionLocator {
		Flow child = new Flow("child");

		public SimpleFlowDefinitionLocator() {
			new EndState(child, "state");
		}

		public FlowDefinition getFlowDefinition(String flowId) throws NoSuchFlowDefinitionException,
				FlowDefinitionConstructionException {
			if (flowId.equals(child.getId())) {
				return child;
			} else {
				throw new IllegalArgumentException(flowId.toString());
			}
		}
	}
}
