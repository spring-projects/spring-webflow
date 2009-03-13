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

import org.springframework.webflow.execution.ActionExecutionException;
import org.springframework.webflow.execution.ActionExecutor;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockRequestContext;

public class ActionExecutorTests extends TestCase {

	private MockRequestContext context;
	private State state;
	private Flow flow;

	protected void setUp() throws Exception {
		flow = new Flow("myFlow");
		state = new EndState(flow, "end");
		context = new MockRequestContext(flow);
	}

	public void testExecuteAction() {
		TestAction action = new TestAction();
		Event result = ActionExecutor.execute(action, context);
		assertNull(context.getCurrentState());
		assertEquals("success", result.getId());
	}

	public void testExecuteActionInState() {
		context.getMockFlowExecutionContext().getMockActiveSession().setState(state);
		TestAction action = new TestAction();
		Event result = ActionExecutor.execute(action, context);
		assertSame(state, context.getCurrentState());
		assertEquals("success", result.getId());
	}

	public void testExecuteActionWithException() {
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				throw new IllegalStateException("Oops");
			}
		};
		try {
			ActionExecutor.execute(action, context);
			fail("Should've failed");
		} catch (ActionExecutionException e) {
			assertNull(context.getCurrentState());
			assertTrue(e.getCause() instanceof IllegalStateException);
			assertEquals(flow.getId(), e.getFlowId());
			assertNull(e.getStateId());
		}
	}

	public void testExecuteActionInStateWithException() {
		context.getMockFlowExecutionContext().getMockActiveSession().setState(state);
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				throw new IllegalStateException("Oops");
			}
		};
		try {
			ActionExecutor.execute(action, context);
			fail("Should've failed");
		} catch (ActionExecutionException e) {
			assertSame(state, context.getCurrentState());
			assertTrue(e.getCause() instanceof IllegalStateException);
			assertEquals(flow.getId(), e.getFlowId());
			assertEquals(state.getId(), e.getStateId());
		}
	}
}