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
package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class StateTests extends TestCase {

	private Flow flow;

	private State state;

	private boolean entered;

	public void setUp() {
		flow = new Flow("flow");
		state = new State(flow, "myState") {
			protected ViewSelection doEnter(RequestControlContext context) throws FlowExecutionException {
				entered = true;
				return ViewSelection.NULL_VIEW;
			}
		};
	}

	public void testStateEnter() {
		assertEquals("myState", state.getId());
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertEquals(state, context.getCurrentState());
		assertTrue(entered);
	}

	public void testStateEnterWithEntryAction() {
		TestAction action = new TestAction();
		state.getEntryActionList().add(action);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertEquals(state, context.getCurrentState());
		assertTrue(action.isExecuted());
		assertTrue(entered);
		assertEquals(1, action.getExecutionCount());
	}
}
