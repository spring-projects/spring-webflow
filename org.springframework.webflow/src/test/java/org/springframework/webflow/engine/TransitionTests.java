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
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

public class TransitionTests extends TestCase {

	private boolean exitCalled;

	public void testExecuteTransitionFromState() {
		Flow flow = new Flow("flow");
		final TransitionableState source = new TransitionableState(flow, "state 1") {
			public void exit(RequestControlContext context) {
				exitCalled = true;
			}

			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		final TransitionableState target = new TransitionableState(flow, "state 2") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		TargetStateResolver targetResolver = new TargetStateResolver() {
			public State resolveTargetState(Transition transition, State sourceState, RequestContext context) {
				assertSame(source, sourceState);
				return target;
			}
		};
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		Transition t = new Transition(targetResolver);
		boolean stateExited = t.execute(source, context);
		assertTrue(stateExited);
		assertTrue(exitCalled);
		assertSame(target, context.getCurrentState());
	}

	public void testExecuteTransitionWithNullSourceState() {
		Flow flow = new Flow("flow");
		final TransitionableState target = new TransitionableState(flow, "state 2") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		TargetStateResolver targetResolver = new TargetStateResolver() {
			public State resolveTargetState(Transition transition, State sourceState, RequestContext context) {
				assertNull(sourceState);
				return target;
			}
		};
		MockRequestControlContext context = new MockRequestControlContext(flow);
		Transition t = new Transition(targetResolver);
		boolean stateChanged = t.execute(null, context);
		assertTrue(stateChanged);
		assertSame(target, context.getCurrentState());
	}

	public void testExecuteTransitionNullTargetState() {
		Flow flow = new Flow("flow");
		final TransitionableState source = new TransitionableState(flow, "state 1") {
			public void exit(RequestControlContext context) {
				exitCalled = true;
			}

			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		TargetStateResolver targetResolver = new TargetStateResolver() {
			public State resolveTargetState(Transition transition, State sourceState, RequestContext context) {
				return null;
			}
		};
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		Transition t = new Transition(targetResolver);
		boolean stateExited = t.execute(source, context);
		assertFalse(stateExited);
		assertFalse(exitCalled);
		assertSame(source, context.getCurrentState());
	}

	public void testExecuteTransitionNullTargetStateResolver() {
		Flow flow = new Flow("flow");
		final TransitionableState source = new TransitionableState(flow, "state 1") {
			public void exit(RequestControlContext context) {
				exitCalled = true;
			}

			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		Transition t = new Transition();
		boolean stateExited = t.execute(source, context);
		assertFalse(stateExited);
		assertFalse(exitCalled);
		assertSame(source, context.getCurrentState());
	}

	public void testTransitionExecutionRefused() {
		Flow flow = new Flow("flow");
		final TransitionableState source = new TransitionableState(flow, "state 1") {
			public void exit(RequestControlContext context) {
				exitCalled = true;
			}

			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		final TransitionableState target = new TransitionableState(flow, "state 2") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		TargetStateResolver targetResolver = new TargetStateResolver() {
			public State resolveTargetState(Transition transition, State sourceState, RequestContext context) {
				assertSame(source, sourceState);
				return target;
			}
		};
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(source);
		Transition t = new Transition(targetResolver);
		t.setExecutionCriteria(new TransitionCriteria() {
			public boolean test(RequestContext context) {
				return false;
			}
		});
		boolean stateExited = t.execute(source, context);
		assertFalse(stateExited);
		assertFalse(exitCalled);
		assertSame(source, context.getCurrentState());
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}