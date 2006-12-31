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
package org.springframework.webflow.engine;

import junit.framework.TestCase;

import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class ActionStateTests extends TestCase {

	public void testActionStateSingleAction() {
		Flow flow = new Flow("myFlow");
		ActionState state = new ActionState(flow, "actionState");
		state.getActionList().add(new TestAction());
		state.getTransitionSet().add(new Transition(on("success"), to("finish")));
		new EndState(flow, "finish");
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		flowExecution.start(null, new MockExternalContext());
		assertEquals(1, ((TestAction)state.getActionList().get(0)).getExecutionCount());
	}

	public void testActionAttributesChain() {
		Flow flow = new Flow("myFlow");
		ActionState state = new ActionState(flow, "actionState");
		state.getActionList().add(new TestAction("not mapped result"));
		state.getActionList().add(new TestAction(null));
		state.getActionList().add(new TestAction(""));
		state.getActionList().add(new TestAction("success"));
		state.getTransitionSet().add(new Transition(on("success"), to("finish")));
		new EndState(flow, "finish");
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		flowExecution.start(null, new MockExternalContext());
		Action[] actions = state.getActionList().toArray();
		for (int i = 0; i < actions.length; i++) {
			TestAction action = (TestAction)actions[i];
			assertEquals(1, action.getExecutionCount());
		}
	}

	public void testActionAttributesChainNoMatchingTransition() {
		Flow flow = new Flow("myFlow");
		ActionState state = new ActionState(flow, "actionState");
		state.getActionList().add(new TestAction("not mapped result"));
		state.getActionList().add(new TestAction(null));
		state.getActionList().add(new TestAction(""));
		state.getActionList().add(new TestAction("yet another not mapped result"));
		state.getTransitionSet().add(new Transition(on("success"), to("finish")));
		new EndState(flow, "finish");
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		try {
			flowExecution.start(null, new MockExternalContext());
			fail("Should not have matched to another state transition");
		}
		catch (NoMatchingTransitionException e) {
			// expected
		}
	}

	public void testActionAttributesChainNamedActions() {
		Flow flow = new Flow("myFlow");
		ActionState state = new ActionState(flow, "actionState");
		state.getActionList().add(new AnnotatedAction(new TestAction("not mapped result")));
		state.getActionList().add(new AnnotatedAction(new TestAction(null)));
		AnnotatedAction action3 = new AnnotatedAction(new TestAction(""));
		action3.setName("action3");
		state.getActionList().add(action3);
		AnnotatedAction action4 = new AnnotatedAction(new TestAction("success"));
		action4.setName("action4");
		state.getActionList().add(action4);
		state.getTransitionSet().add(new Transition(on("action4.success"), to("finish")));
		new EndState(flow, "finish");
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		flowExecution.start(null, new MockExternalContext());
		assertTrue(!flowExecution.isActive());
		Action[] actions = state.getActionList().toArray();
		for (int i = 0; i < actions.length; i++) {
			AnnotatedAction action = (AnnotatedAction)actions[i];
			assertEquals(1, ((TestAction)(action.getTargetAction())).getExecutionCount());
		}
	}

	protected TransitionCriteria on(String event) {
		return new EventIdTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}
