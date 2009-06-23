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

import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.MockTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests ActionState behavior
 * @author Keith Donald
 */
public class ActionStateTests extends TestCase {

	private Flow flow;
	private ActionState state;
	private MockRequestControlContext context;

	public void setUp() {
		flow = new Flow("myFlow");
		state = new ActionState(flow, "actionState");
		new EndState(flow, "finish");
		context = new MockRequestControlContext(flow);
	}

	public void testExecuteSingleAction() {
		state.getActionList().add(new TestAction());
		state.getTransitionSet().add(new Transition(on("success"), to("finish")));
		state.enter(context);
		assertEquals(1, ((TestAction) state.getActionList().get(0)).getExecutionCount());
	}

	public void testExecuteNothing() {
		try {
			state.enter(context);
			fail("Should've failed");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testExecuteActionCannotHandleResultEvent() {
		state.getActionList().add(new TestAction());
		try {
			state.enter(context);
			fail("Should've failed");
		} catch (NoMatchingTransitionException e) {
			assertEquals(1, ((TestAction) state.getActionList().get(0)).getExecutionCount());
		}
	}

	public void testExecuteActionChain() {
		state.getActionList().add(new TestAction("not mapped result"));
		state.getActionList().add(new TestAction(null));
		state.getActionList().add(new TestAction(""));
		state.getActionList().add(new TestAction("success"));
		state.getTransitionSet().add(new Transition(on("success"), to("finish")));
		state.enter(context);
		Action[] actions = state.getActionList().toArray();
		for (int i = 0; i < actions.length; i++) {
			TestAction action = (TestAction) actions[i];
			assertEquals(1, action.getExecutionCount());
		}
	}

	protected TransitionCriteria on(String event) {
		return new MockTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}
