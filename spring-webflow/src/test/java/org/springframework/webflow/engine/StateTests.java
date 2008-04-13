/*
 * Copyright 2004-2008 the original author or authors.
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

	private boolean handled;

	public void setUp() {
		flow = new Flow("flow");
		state = new State(flow, "myState") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				entered = true;
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

	public void testHandledException() {
		state.getExceptionHandlerSet().add(new FlowExecutionExceptionHandler() {
			public boolean canHandle(FlowExecutionException exception) {
				return true;
			}

			public void handle(FlowExecutionException exception, RequestControlContext context) {
				handled = true;
			}

		});
		FlowExecutionException e = new FlowExecutionException(flow.getId(), state.getId(), "Whatev");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		assertTrue(state.handleException(e, context));
		assertTrue(handled);
	}

	public void testCouldNotHandleException() {
		FlowExecutionException e = new FlowExecutionException(flow.getId(), state.getId(), "Whatev");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		assertFalse(state.handleException(e, context));
	}

}
