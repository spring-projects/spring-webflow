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
package org.springframework.webflow.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.engine.support.ActionTransitionCriteria;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.MockTransitionCriteria;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests that ViewState logic is correct.
 * @author Keith Donald
 */
public class ViewStateTests {

	@Test
	public void testEnterViewStateRenderResponse() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateRenderNotAllowed() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getMockExternalContext().setResponseAllowed(false);
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getExternalContext().isResponseComplete());
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateResponseAlreadyComplete() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getExternalContext().recordResponseComplete();
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateRedirectResponseAlreadyComplete() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getExternalContext().requestFlowExecutionRedirect();
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateWithVariables() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.addVariable(new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}));
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertEquals("bar", context.getViewScope().getString("foo"));
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
	}

	@Test
	public void testEnterViewStateWithLocalRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(true);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateWithNoLocalRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(false);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateRedirectInPopup() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(true);
		state.setPopup(true);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlashScope().put("foo", "bar");
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getMockExternalContext().getRedirectInPopup());
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testEnterViewStateWithAlwaysRedirectOnPause() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlashScope().put("foo", "bar");
		context.setAlwaysRedirectOnPause(true);
		state.enter(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testResumeViewStateForRefresh() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testResumeViewStateForRefreshResponseCompleteRecorded() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		new EndState(flow, "finish");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.getFlowScope().remove("renderCalled");
		context.getFlashScope().put("foo", "bar");
		context.getExternalContext().recordResponseComplete();
		state.resume(context);
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testResumeViewStateRestoreVariables() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.addVariable(new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return new TestBean();
			}

			public void restoreReferences(Object value, RequestContext context) {
				((TestBean) value).datum1 = "Restored";
			}
		}));
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		state.resume(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertEquals("Restored", ((TestBean) context.getViewScope().get("foo")).datum1);
	}

	@Test
	public void testResumeViewStateForEventWithTransitionFlowEnded() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		EndState end = new EndState(flow, "finish");
		TestAction testAction = new TestAction();
		end.setFinalResponseAction(testAction);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getFlowExecutionContext().isActive());
		assertTrue(testAction.isExecuted());
	}

	@Test
	public void testResumeViewStateForEventWithTransitionStateExited() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("next")));
		ViewState next = new ViewState(flow, "next", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setAlwaysRedirectOnPause(true);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertTrue(context.getExternalContext().isResponseComplete());
		assertTrue(context.getFlowExecutionContext().isActive());
		assertSame(next, context.getCurrentState());
		assertTrue(context.getFlowScope().contains("saveStateCalled"));
	}

	@Test
	public void testResumeViewStateForEventWithTransitionStateExitedNoRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("next")));
		ViewState next = new ViewState(flow, "next", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setAlwaysRedirectOnPause(false);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertTrue(context.getExternalContext().isResponseComplete());
		assertTrue(context.getFlowExecutionContext().isActive());
		assertSame(next, context.getCurrentState());
		assertFalse(context.getFlowScope().contains("saveStateCalled"));
	}

	@Test
	public void testResumeViewStateForEventStateNotExitedNonAjax() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		TestAction action = new TestAction();
		t.setExecutionCriteria(new ActionTransitionCriteria(action));
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.getFlowScope().remove("renderCalled");
		context.putRequestParameter("_eventId", "submit");
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals(1, action.getExecutionCount());
		assertTrue(context.getExternalContext().isResponseComplete());
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
		assertFalse(context.getFlashScope().contains(View.USER_EVENT_STATE_ATTRIBUTE));
	}

	@Test
	public void testResumeViewStateForEventStateNotExitedNonAjaxResponseNotAllowed() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.getFlowScope().remove("renderCalled");
		context.putRequestParameter("_eventId", "submit");
		context.getMockExternalContext().setResponseAllowed(false);
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertEquals(StubViewFactory.USER_EVENT_STATE, context.getFlashScope().get(View.USER_EVENT_STATE_ATTRIBUTE));
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testResumeViewStateForEventStateNotExitedNonAjaxRedirectEnabled() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		TestAction action = new TestAction();
		t.setExecutionCriteria(new ActionTransitionCriteria(action));
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setAlwaysRedirectOnPause(true);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.setAlwaysRedirectOnPause(true);
		context.putRequestParameter("_eventId", "submit");
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals(1, action.getExecutionCount());
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertEquals(StubViewFactory.USER_EVENT_STATE, context.getFlashScope().get(View.USER_EVENT_STATE_ATTRIBUTE));
		assertTrue(context.getFlashScope().contains("foo"));
	}

	@Test
	public void testResumeViewStateForEventStateNotExitedAjax() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		TestAction action = new TestAction();
		t.setExecutionCriteria(new ActionTransitionCriteria(action));
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getMockExternalContext().setAjaxRequest(true);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		context.getMockExternalContext().setAjaxRequest(true);
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals(1, action.getExecutionCount());
		assertTrue(context.getExternalContext().isResponseComplete());
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
		assertFalse(context.getFlashScope().contains(View.USER_EVENT_STATE_ATTRIBUTE));
	}

	@Test
	public void testResumeViewStateForEventStateNoExitActionRecordedResponseComplete() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				super.doExecute(context);
				context.getExternalContext().recordResponseComplete();
				return success();
			}
		};
		t.setExecutionCriteria(new ActionTransitionCriteria(action));
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		context.getFlowScope().remove("renderCalled");
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		context.getFlashScope().put("Foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals(1, action.getExecutionCount());
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertFalse(context.getFlashScope().contains("foo"));
		assertFalse(context.getFlashScope().contains(View.USER_EVENT_STATE_ATTRIBUTE));
	}

	@Test
	public void testResumeViewStateForEventStateNoExitActionRecordedExecutionRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		TestAction action = new TestAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				super.doExecute(context);
				context.getExternalContext().requestFlowExecutionRedirect();
				return success();
			}
		};
		t.setExecutionCriteria(new ActionTransitionCriteria(action));
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertTrue(context.getFlowScope().contains("renderCalled"), "Render not called");
		context.getFlowScope().remove("renderCalled");
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		context.getFlashScope().put("foo", "bar");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals(1, action.getExecutionCount());
		assertTrue(context.getExternalContext().isResponseComplete());
		assertFalse(context.getFlowScope().contains("renderCalled"), "Render called");
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
		assertTrue(context.getFlashScope().contains("foo"));
		assertEquals(StubViewFactory.USER_EVENT_STATE, context.getFlashScope().get(View.USER_EVENT_STATE_ATTRIBUTE));
	}

	@Test
	public void testRedirectInSameStateOverridesAlwaysRedirectOnPause() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.setAlwaysRedirectOnPause(false);
		context.setRedirectInSameState(true);
		context.getFlowScope().remove("renderCalled");
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertTrue(context.getMockExternalContext().getFlowExecutionRedirectRequested());
	}

	@Test
	public void testEmbeddedModeOverridesRedirectInSameState() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		Transition t = new Transition(on("submit"), null);
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getMockExternalContext().setAjaxRequest(true);
		context.setEmbeddedMode();
		context.setAlwaysRedirectOnPause(true);
		context.setRedirectInSameState(true);
		state.enter(context);
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
	}

	@Test
	public void testViewStateRedirectOverridesEmbeddedMode() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(false);
		Transition t = new Transition(on("submit"), null);
		state.getTransitionSet().add(t);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context.getMockExternalContext().setAjaxRequest(true);
		context.setEmbeddedMode();
		context.setAlwaysRedirectOnPause(true);
		context.setRedirectInSameState(true);
		assertFalse(context.getMockExternalContext().getFlowExecutionRedirectRequested());
	}

	@Test
	public void testResumeViewStateForEventDestroyVariables() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.addVariable(new ViewVariable("foo", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return "bar";
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}));
		state.getTransitionSet().add(new Transition(on("submit"), to("next")));
		new ViewState(flow, "next", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertTrue(context.getViewScope().contains("foo"));
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertTrue(context.getFlowExecutionContext().isActive());
		assertEquals("next", context.getCurrentState().getId());
		assertFalse(context.getViewScope().contains("foo"));
	}

	protected TransitionCriteria on(String event) {
		return new MockTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}

}
