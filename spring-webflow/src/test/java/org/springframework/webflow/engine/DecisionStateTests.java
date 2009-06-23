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
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class DecisionStateTests extends TestCase {

	public void testIfDecision() {
		Flow flow = new Flow("flow");
		DecisionState state = new DecisionState(flow, "decisionState");
		state.getTransitionSet().add(new Transition(new MockTransitionCriteria("foo"), to("target")));
		new EndState(flow, "target");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentEvent(new Event(this, "foo"));
		state.enter(context);
		assertFalse(context.getFlowExecutionContext().isActive());
	}

	public void testElseDecision() {
		Flow flow = new Flow("flow");
		DecisionState state = new DecisionState(flow, "decisionState");
		state.getTransitionSet().add(new Transition(new MockTransitionCriteria("foo"), to("invalid")));
		state.getTransitionSet().add(new Transition(to("target")));
		new EndState(flow, "target");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentEvent(new Event(this, "bogus"));
		state.enter(context);
		assertFalse(context.getFlowExecutionContext().isActive());
	}

	public void testCannotDecide() {
		Flow flow = new Flow("flow");
		DecisionState state = new DecisionState(flow, "decisionState");
		state.getTransitionSet().add(new Transition(new MockTransitionCriteria("foo"), to("invalid")));
		state.getTransitionSet().add(new Transition(new MockTransitionCriteria("bar"), to("invalid")));
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentEvent(new Event(this, "bogus"));
		try {
			state.enter(context);
			fail("Expected no matching");
		} catch (NoMatchingTransitionException e) {

		}
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}
