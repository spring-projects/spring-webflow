/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.StubViewFactory;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.MockFlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowExecutionKeyFactory;

/**
 * General flow execution tests.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Ben Hale
 * @author Jeremy Grelle
 */
public class FlowExecutionImplTests {


	@Test
	public void testStartAndEnd() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertTrue(execution.hasStarted());
		assertFalse(execution.isActive());
		assertTrue(execution.hasEnded());
		try {
			execution.getActiveSession();
			fail("should have failed");
		} catch (IllegalStateException e) {

		}
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

	@Test
	public void testStartAndEndSavedMessages() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener() {
			public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap<?> input) {
				super.sessionStarting(context, session, input);
				context.getMessageContext().addMessage(new MessageBuilder().source("foo").defaultText("bar").build());
			}
		};
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertTrue(execution.hasStarted());
		assertFalse(execution.isActive());
		assertTrue(execution.hasEnded());
		assertNotNull(execution.getFlashScope().get("messagesMemento"));
	}

	@Test
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
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertTrue(execution.isActive());
		assertEquals(1, mockListener.getPausedCount());
	}

	@Test
	public void testStartWithNullInputMap() {
		Flow flow = new Flow("flow");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				// no op
			}
		};
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener() {
			public void sessionStarting(RequestContext context, FlowSession session, MutableAttributeMap<?> input) {
				super.sessionStarting(context, session, input);
				assertNotNull(input);
			}
		};
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertTrue(execution.isActive());
		assertEquals(1, mockListener.getPausedCount());
	}

	@Test
	public void testStartExceptionThrownBeforeFirstSessionCreated() {
		Flow flow = new Flow("flow");
		flow.getExceptionHandlerSet().add(new FlowExecutionExceptionHandler() {
			public boolean canHandle(FlowExecutionException exception) {
				return true;
			}

			public void handle(FlowExecutionException exception, RequestControlContext context) {
				throw new UnsupportedOperationException("Should not be called");
			}

		});
		new EndState(flow, "end");
		FlowExecutionListener mockListener = new FlowExecutionListener() {
			public void sessionCreating(RequestContext context, FlowDefinition definition) {
				assertFalse(context.getFlowExecutionContext().isActive());
				throw new IllegalStateException("Oops");
			}
		};
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		try {
			execution.start(null, context);
			fail("Should have failed");
		} catch (FlowExecutionException e) {
			assertEquals(flow.getId(), e.getFlowId());
			assertNull(e.getStateId());
			assertTrue(e.getCause() instanceof IllegalStateException);
			e.printStackTrace();
			assertTrue(e.getCause().getMessage().equals("Oops"));
		}
	}

	@Test
	public void testStartExceptionThrownByState() {
		Flow flow = new Flow("flow");
		State state = new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw new IllegalStateException("Oops");
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
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

	@Test
	public void testStartFlowExecutionExceptionThrownByState() {
		Flow flow = new Flow("flow");
		final FlowExecutionException e = new FlowExecutionException("flow", "state", "Oops");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw e;
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		try {
			execution.start(null, context);
			fail("Should have failed");
		} catch (FlowExecutionException ex) {
			assertSame(e, ex);
		}
	}

	@Test
	public void testStartExceptionThrownByStateHandledByFlowExceptionHandler() {
		Flow flow = new Flow("flow");
		StubFlowExecutionExceptionHandler exceptionHandler = new StubFlowExecutionExceptionHandler();
		flow.getExceptionHandlerSet().add(exceptionHandler);
		final FlowExecutionException e = new FlowExecutionException("flow", "state", "Oops");
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw e;
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertTrue(exceptionHandler.getHandled());
	}

	@Test
	public void testStartExceptionThrownByStateHandledByStateExceptionHandler() {
		Flow flow = new Flow("flow");
		flow.getExceptionHandlerSet().add(new StubFlowExecutionExceptionHandler());
		final FlowExecutionException e = new FlowExecutionException("flow", "state", "Oops");
		State s = new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw e;
			}
		};
		StubFlowExecutionExceptionHandler exceptionHandler = new StubFlowExecutionExceptionHandler();
		s.getExceptionHandlerSet().add(exceptionHandler);
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertTrue(exceptionHandler.getHandled());
	}

	@Test
	public void testExceptionHandledByNestedExceptionHandler() {
		Flow flow = new Flow("flow");
		ExceptionThrowingExceptionHandler exceptionHandler = new ExceptionThrowingExceptionHandler(true);
		flow.getExceptionHandlerSet().add(exceptionHandler);
		new State(flow, "state") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
				throw new FlowExecutionException("flow", "state", "Oops");
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		assertFalse(execution.hasStarted());
		execution.start(null, context);
		assertEquals(2, exceptionHandler.getHandleCount());
	}

	@Test
	public void testStartCannotCallTwice() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		try {
			execution.start(null, context);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	@Test
	public void testResume() {
		Flow flow = new Flow("flow");
		new ViewState(flow, "view", new StubViewFactory());
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		context = new MockExternalContext();
		execution.resume(context);
		assertEquals(1, mockListener.getResumingCount());
		assertEquals(2, mockListener.getPausedCount());
	}

	@Test
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

	@Test
	public void testResumeAfterEnding() {
		Flow flow = new Flow("flow");
		new EndState(flow, "end");
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		try {
			execution.resume(context);
			fail("Should've failed");
		} catch (IllegalStateException e) {

		}
	}

	@Test
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

	@Test
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

	@Test
	public void testExecuteTransition() {
		Flow flow = new Flow("flow");
		ViewState state = new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				context.execute(getRequiredTransition(context));
			}
		};
		state.getTransitionSet().add(new Transition(new DefaultTargetStateResolver("finish")));
		new EndState(flow, "finish");
		MockFlowExecutionListener mockListener = new MockFlowExecutionListener();
		FlowExecutionListener[] listeners = new FlowExecutionListener[] { mockListener };
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setListeners(listeners);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertEquals(0, mockListener.getTransitionExecutingCount());
		execution.resume(context);
		assertTrue(execution.hasEnded());
		assertEquals(1, mockListener.getTransitionExecutingCount());
	}

	@Test
	public void testRequestContextManagedOnStartAndResume() {
		Flow flow = new Flow("flow");
		new ViewState(flow, "view", new StubViewFactory()) {
			public void resume(RequestControlContext context) {
				assertSame(context, RequestContextHolder.getRequestContext());
			}
		};
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		execution.setKeyFactory(new MockFlowExecutionKeyFactory());

		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertNull(RequestContextHolder.getRequestContext(), "RequestContext was not released");

		context = new MockExternalContext();
		execution.resume(context);
		assertNull(RequestContextHolder.getRequestContext(), "RequestContext was not released");

	}

	private static class StubFlowExecutionExceptionHandler implements FlowExecutionExceptionHandler {

		private boolean handled;

		public boolean getHandled() {
			return handled;
		}

		public boolean canHandle(FlowExecutionException exception) {
			return true;
		}

		public void handle(FlowExecutionException exception, RequestControlContext context) {
			handled = true;
		}
	}

	private static class ExceptionThrowingExceptionHandler implements FlowExecutionExceptionHandler {

		private boolean throwOnlyOnce = true;
		private int handleCount;

		public ExceptionThrowingExceptionHandler(boolean throwOnlyOnce) {
			this.throwOnlyOnce = throwOnlyOnce;
		}

		public int getHandleCount() {
			return handleCount;
		}

		public boolean canHandle(FlowExecutionException exception) {
			return true;
		}

		public void handle(FlowExecutionException exception, RequestControlContext context) {
			this.handleCount++;
			if (throwOnlyOnce && "nested exception".equals(exception.getMessage())) {
				// No more exceptions
			} else {
				throw new FlowExecutionException(exception.getFlowId(), exception.getStateId(), "nested exception");
			}
		}

	}

}
