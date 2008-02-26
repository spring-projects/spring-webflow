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
package org.springframework.webflow.engine.impl;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Test case for {@link FlowExecutionImplFactory}.
 */
public class FlowExecutionImplFactoryTests extends TestCase {

	private FlowExecutionImplFactory factory = new FlowExecutionImplFactory();

	private Flow flowDefinition;

	private boolean starting;

	private boolean getKeyCalled;

	public void setUp() {
		flowDefinition = new Flow("flow");
		new EndState(flowDefinition, "end");
	}

	public void testCreate() {
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertSame(flowDefinition, execution.getDefinition());
		assertFalse(execution.hasStarted());
		assertFalse(execution.isActive());
	}

	public void testCreateNullArgument() {
		try {
			factory.createFlowExecution(null);
			fail("Should've failed");
		} catch (IllegalArgumentException e) {

		}
	}

	public void testCreateWithExecutionAttributes() {
		MutableAttributeMap attributes = new LocalAttributeMap();
		attributes.put("foo", "bar");
		factory.setExecutionAttributes(attributes);
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertEquals(attributes, execution.getAttributes());
	}

	public void testCreateWithExecutionListener() {
		FlowExecutionListener listener1 = new FlowExecutionListenerAdapter() {
			public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input) {
				starting = true;
			}
		};
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener1));
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertFalse(execution.isActive());
		execution.start(null, new MockExternalContext());
		assertTrue(starting);
	}

	public void testCreateWithExecutionKeyFactory() {
		State state = new State(flowDefinition, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				context.assignFlowExecutionKey();
			}
		};
		flowDefinition.setStartState(state);
		factory.setExecutionKeyFactory(new FlowExecutionKeyFactory() {
			public FlowExecutionKey getKey(FlowExecution execution) {
				getKeyCalled = true;
				return null;
			}
		});
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		execution.start(null, new MockExternalContext());
		assertTrue(getKeyCalled);
		assertNull(execution.getKey());
	}
}