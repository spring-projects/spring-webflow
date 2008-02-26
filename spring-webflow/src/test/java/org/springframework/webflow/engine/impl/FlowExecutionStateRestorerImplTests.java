package org.springframework.webflow.engine.impl;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.MockFlowExecutionListener;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.GeneratedFlowExecutionKey;

public class FlowExecutionStateRestorerImplTests extends TestCase {
	private SimpleFlowDefinitionLocator definitionLocator;
	private FlowExecutionImplStateRestorer stateRestorer;
	private LocalAttributeMap executionAttributes = new LocalAttributeMap();
	private FlowExecutionListener listener = new MockFlowExecutionListener();
	private FlowExecutionListenerLoader executionListenerLoader = new StaticFlowExecutionListenerLoader(listener);
	GeneratedFlowExecutionKey newKey = new GeneratedFlowExecutionKey();
	private FlowExecutionKeyFactory executionKeyFactory = new FlowExecutionKeyFactory() {
		public FlowExecutionKey getKey(FlowExecution execution) {
			return newKey;
		}
	};

	protected void setUp() {
		definitionLocator = new SimpleFlowDefinitionLocator();
		stateRestorer = new FlowExecutionImplStateRestorer(definitionLocator);
		stateRestorer.setExecutionAttributes(executionAttributes);
		stateRestorer.setExecutionListenerLoader(executionListenerLoader);
	}

	public void testRestoreStateNoSessions() {
		FlowExecutionKey key = new GeneratedFlowExecutionKey();
		LocalAttributeMap conversationScope = new LocalAttributeMap();
		FlowExecutionImpl execution = new FlowExecutionImpl("parent", new LinkedList());
		stateRestorer.restoreState(execution, key, conversationScope, executionKeyFactory);
		assertSame(definitionLocator.parent, execution.getDefinition());
		assertTrue(execution.getFlowSessions().isEmpty());
		assertSame(conversationScope, execution.getConversationScope());
		assertSame(key, execution.getKey());
		assertSame(executionAttributes, execution.getAttributes());
		assertEquals(1, execution.getListeners().length);
		execution.assignKey();
		assertEquals(newKey, execution.getKey());
	}

	public void testRestoreStateFlowDefinitionIdNotSet() {
		FlowExecutionKey key = new GeneratedFlowExecutionKey();
		LocalAttributeMap conversationScope = new LocalAttributeMap();
		FlowExecutionImpl execution = new FlowExecutionImpl();
		try {
			stateRestorer.restoreState(execution, key, conversationScope, executionKeyFactory);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	public void testRestoreStateFlowSessionsNotSet() {
		FlowExecutionKey key = new GeneratedFlowExecutionKey();
		LocalAttributeMap conversationScope = new LocalAttributeMap();
		FlowExecutionImpl execution = new FlowExecutionImpl("parent", null);
		try {
			stateRestorer.restoreState(execution, key, conversationScope, executionKeyFactory);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	private class SimpleFlowDefinitionLocator implements FlowDefinitionLocator {
		Flow parent = new Flow("parent");
		Flow child = new Flow("child");

		public FlowDefinition getFlowDefinition(String flowId) throws NoSuchFlowDefinitionException,
				FlowDefinitionConstructionException {
			if (flowId.equals(parent.getId())) {
				return parent;
			} else if (flowId.equals(child.getId())) {
				return child;
			} else {
				throw new IllegalArgumentException(flowId.toString());
			}
		}
	}
}
