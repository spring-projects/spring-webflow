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

import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.test.MockRequestControlContext;

public class TransitionTests extends TestCase {

	public void testSimpleTransition() {
		Transition t = new Transition(to("target"));
		Flow flow = new Flow("flow");
		ViewState source = new ViewState(flow, "source");
		TestAction action = new TestAction();
		source.getExitActionList().add(action);
		ViewState target = new ViewState(flow, "target");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		t.execute(source, context);
		assertTrue(t.matches(context));
		assertEquals(t, context.getLastTransition());
		assertEquals(context.getCurrentState(), target);
		assertEquals(1, action.getExecutionCount());
	}

	public void testTransitionCriteriaDoesNotMatch() {
		Transition t = new Transition(new EventIdTransitionCriteria("bogus"), to("target"));
		MockRequestControlContext context = new MockRequestControlContext(new Flow("flow"));
		assertFalse(t.matches(context));
	}

	public void testTransitionCannotExecute() {
		Transition t = new Transition(to("target"));
		t.setExecutionCriteria(new EventIdTransitionCriteria("bogus"));
		Flow flow = new Flow("flow");
		ViewState source = new ViewState(flow, "source");
		TestAction action = new TestAction();
		source.getExitActionList().add(action);
		new ViewState(flow, "target");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		t.execute(source, context);
		assertTrue(t.matches(context));
		assertEquals(null, context.getLastTransition());
		assertEquals(context.getCurrentState(), source);
		assertEquals(0, action.getExecutionCount());
	}
	
	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}