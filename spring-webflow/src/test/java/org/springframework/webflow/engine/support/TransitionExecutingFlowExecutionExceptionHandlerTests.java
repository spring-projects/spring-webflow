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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.webflow.TestException;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionableState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.support.AbstractFlowBuilder;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class TransitionExecutingFlowExecutionExceptionHandlerTests extends TestCase {

	Flow flow;

	TransitionableState state;

	protected void setUp() {
		flow = new Flow("myFlow");
		state = new TransitionableState(flow, "state1") {
			protected void doEnter(RequestControlContext context) {
				throw new FlowExecutionException(getFlow().getId(), getId(), "Oops!", new TestException());
			}
		};
		state.getTransitionSet().add(new Transition(toState("end")));
	}

	public void testTransitionExecutorHandlesExceptionExactMatch() {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		handler.add(TestException.class, "state");
		FlowExecutionException e = new FlowExecutionException(state.getOwner().getId(), state.getId(), "Oops",
				new TestException());
		assertTrue("Doesn't handle state exception", handler.canHandle(e));

		e = new FlowExecutionException(state.getOwner().getId(), state.getId(), "Oops", new Exception());
		assertFalse("Shouldn't handle exception", handler.canHandle(e));
	}

	public void testTransitionExecutorHandlesExceptionSuperclassMatch() {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		handler.add(Exception.class, "state");
		FlowExecutionException e = new FlowExecutionException(state.getOwner().getId(), state.getId(), "Oops",
				new TestException());
		assertTrue("Doesn't handle state exception", handler.canHandle(e));
		e = new FlowExecutionException(state.getOwner().getId(), state.getId(), "Oops", new RuntimeException());
		assertTrue("Doesn't handle state exception", handler.canHandle(e));
	}

	public void testFlowStateExceptionHandlingTransition() {
		new EndState(flow, "end");
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		handler.add(TestException.class, "end");
		flow.getExceptionHandlerSet().add(handler);
		FlowExecutionListener listener = new FlowExecutionListenerAdapter() {
			public void sessionEnding(RequestContext context, FlowSession session, MutableAttributeMap output) {
				assertTrue(context.getFlashScope().contains("flowExecutionException"));
				assertTrue(context.getFlashScope().contains("rootCauseException"));
				assertTrue(context.getFlashScope().get("rootCauseException") instanceof TestException);
			}
		};
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener));
		FlowExecution execution = factory.createFlowExecution(flow);
		execution.start(null, new MockExternalContext());
		assertTrue("Should have ended", !execution.isActive());
	}

	public void testStateExceptionHandlingTransitionNoSuchState() {
		TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
		handler.add(TestException.class, "end");
		flow.getExceptionHandlerSet().add(handler);
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(flow);
		try {
			execution.start(null, new MockExternalContext());
			fail("Should have failed no such state");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testStateExceptionHandlingRethrow() {
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(flow);
		try {
			execution.start(null, new MockExternalContext());
			fail("Should have rethrown");
		} catch (FlowExecutionException e) {
			// expected
		}
	}

	public void testStateExceptionHandlingExceptionInEndState() {
		FlowBuilder builder = new AbstractFlowBuilder() {
			public void buildStates() throws FlowBuilderException {
				State state = new EndState(getFlow(), "end");
				state.getEntryActionList().add(new AbstractAction() {
					protected Event doExecute(RequestContext context) throws Exception {
						throw new NullPointerException("failing");
					}
				});
				new TransitionableState(getFlow(), "showError") {
					protected void doEnter(RequestControlContext context) throws FlowExecutionException {
					}
				};
			}

			public void buildExceptionHandlers() throws FlowBuilderException {
				getFlow().getExceptionHandlerSet().add(
						new TransitionExecutingFlowExecutionExceptionHandler().add(Exception.class, "showError"));
			}

			public Flow createFlow() throws FlowBuilderException {
				return Flow.create(getContext().getFlowId(), getContext().getFlowAttributes());
			}
		};
		Flow flow = new FlowAssembler(builder, new MockFlowBuilderContext("flow")).assembleFlow();
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(flow);
		execution.start(null, new MockExternalContext());
		assertTrue(execution.isActive());
	}

	protected TargetStateResolver toState(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}