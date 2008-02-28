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

import org.springframework.binding.message.DefaultMessageContextFactory;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.MockFlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.GeneratedFlowExecutionKey;
import org.springframework.webflow.test.MockExternalContext;

/**
 * General flow execution tests.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Ben Hale
 * @author Jeremy Grelle
 */
public class FlowExecutionImplTests extends TestCase {

	public void testStartAndEnd() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertTrue(execution.hasStarted());
		assertFalse(execution.isActive());
		assertEquals(1, mockListener.getRequestsSubmittedCount());
		assertEquals(1, mockListener.getRequestsProcessedCount());
		assertEquals(1, mockListener.getSessionCreatingCount());
		assertEquals(1, mockListener.getSessionStartingCount());
		assertEquals(1, mockListener.getSessionStartedCount());
		assertEquals(1, mockListener.getStateEnteringCount());
		assertEquals(1, mockListener.getStateEnteredCount());
		assertEquals(1, mockListener.getSessionEndingCount());
		assertEquals(1, mockListener.getSessionEndedCount());
		assertEquals(0, mockListener.getEventSignaledCount());
		assertEquals(0, mockListener.getTransitionExecutingCount());
		assertEquals(0, mockListener.getPausedCount());
		assertEquals(0, mockListener.getResumingCount());
		assertEquals(0, mockListener.getExceptionThrownCount());
		assertEquals(0, mockListener.getFlowNestingLevel());
	}

	public void testStartAndPause() {
		Flow flow = new Flow("flow");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				// no op
			}
		};
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertTrue(execution.isActive());
		assertEquals(1, mockListener.getPausedCount());
	}

	public void testStartExceptionThrownBeforeFirstSessionCreated() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		FlowExecutionListener mockListener = new FlowExecutionListenerAdapter() {
			public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap input) {
				throw new IllegalStateException("Oops");
			}
		};
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		try {
			execution.start(null, context);
			fail("Should have failed");
		} catch (FlowExecutionException e) {
			assertEquals(flow.getId(), e.getFlowId());
			assertNull(e.getStateId());
		}
	}

	public void testStartExceptionThrownByState() {
		Flow flow = new Flow("flow");
		State state = new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw new IllegalStateException("Oops");
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		try {
			execution.start(null, context);
			fail("Should have failed");
		} catch (FlowExecutionException e) {
			assertEquals(flow.getId(), e.getFlowId());
			assertEquals(state.getId(), e.getStateId());
		}
	}

	public void testStartFlowExecutionExceptionThrown() {
		Flow flow = new Flow("flow");
		final FlowExecutionException e = new FlowExecutionException("flow", "state", "Oops");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw e;
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		try {
			execution.start(null, context);
			fail("Should have failed");
		} catch (FlowExecutionException ex) {
			assertSame(e, ex);
		}
	}

	public void testStartCannotCallTwice() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		try {
			execution.start(null, context);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	public void testResume() {
		Flow flow = new Flow("flow");
		new ViewState(flow, "view", new StubViewFactory());
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setListeners(listeners);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		context = new MockExternalContext();
		execution.resume(context);
		assertEquals(1, mockListener.getResumingCount());
		assertEquals(2, mockListener.getPausedCount());
	}

	public void testResumeNotAViewState() {
		Flow flow = new Flow("flow");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				// no-op
			}
		};
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setListeners(listeners);
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		context = new MockExternalContext();
		try {
			execution.resume(context);
			assertEquals(1, mockListener.getResumingCount());
			fail("Should have failed");
		} catch (FlowExecutionException e) {

		}
	}

	public void testResumeAfterEnding() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		try {
			execution.resume(context);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	public void testResumeException() {
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				throw new IllegalStateException("Oops");
			}
		};
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setListeners(listeners);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		context = new MockExternalContext();
		try {
			execution.resume(context);
		} catch (FlowExecutionException e) {
			assertEquals(flow.getId(), e.getFlowId());
			assertEquals(state.getId(), e.getStateId());
			assertEquals(1, mockListener.getResumingCount());
			assertEquals(2, mockListener.getPausedCount());
		}
	}

	public void testResumeFlowExecutionException() {
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				throw new FlowExecutionException("flow", "view", "oops");
			}
		};
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setListeners(listeners);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		context = new MockExternalContext();
		try {
			execution.resume(context);
		} catch (FlowExecutionException e) {
			assertEquals(flow.getId(), e.getFlowId());
			assertEquals(state.getId(), e.getStateId());
			assertEquals(1, mockListener.getResumingCount());
			assertEquals(2, mockListener.getPausedCount());
		}
	}

	public void testExecuteTransition() {
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				context.execute(getRequiredTransition(context));
			}
		};
		state.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("finish")));
		EndState end = new EndState(flow, "finish");
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertEquals(0, mockListener.getTransitionExecutingCount());
		execution.resume(context);
		assertTrue(execution.hasEnded());
		assertEquals(1, mockListener.getTransitionExecutingCount());
	}

	public void testRequestContextManagedOnStartAndResume() {
		Flow flow = new Flow("flow");
		new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				assertSame(context, RequestContextHolder.getRequestContext());
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setMessageContextFactory(new DefaultMessageContextFactory(new StaticMessageSource()));
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());

		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertNull("RequestContext was not released", RequestContextHolder.getRequestContext());

		context = new MockExternalContext();
		execution.resume(context);
		assertNull("RequestContext was not released", RequestContextHolder.getRequestContext());

	}

	private static class MockFlowExecutionKeyFactory implements FlowExecutionKeyFactory {
		public FlowExecutionKey getKey(FlowExecution execution) {
			return new GeneratedFlowExecutionKey();
		}
	}

}