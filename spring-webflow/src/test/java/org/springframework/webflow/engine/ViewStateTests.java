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

import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests that ViewState logic is correct.
 * @author Keith Donald
 */
public class ViewStateTests extends TestCase {

	public void testEnterViewStateDefaultBehavior() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertTrue("Render not called", context.getFlowScope().contains("renderCalled"));
		assertFalse(context.getFlowExecutionRedirectSent());
	}

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
		assertEquals("bar", context.getFlowScope().getString("foo"));
		assertTrue("Render not called", context.getFlowScope().contains("renderCalled"));
		assertFalse(context.getFlowExecutionRedirectSent());
	}

	public void testEnterViewStateWithLocalRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(Boolean.TRUE);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertFalse("Render called", context.getFlowScope().contains("renderCalled"));
		assertTrue(context.getMockExternalContext().flowExecutionRedirectRequested());
	}

	public void testEnterViewStateWithNoLocalRedirect() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setRedirect(Boolean.FALSE);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		assertTrue("Render called", context.getFlowScope().contains("renderCalled"));
		assertFalse(context.getMockExternalContext().flowExecutionRedirectRequested());
	}

	public void testEnterViewStateWithAlwaysRedirectOnPause() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setAlwaysRedirectOnPause(true);
		state.enter(context);
		assertFalse("Render called", context.getFlowScope().contains("renderCalled"));
		assertTrue(context.getMockExternalContext().flowExecutionRedirectRequested());
	}

	public void testEnterViewStateWithPopup() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.setPopup(true);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setAlwaysRedirectOnPause(true);
		state.enter(context);
		assertFalse("Render called", context.getFlowScope().contains("renderCalled"));
		assertTrue(context.getMockExternalContext().flowExecutionRedirectRequested());
	}

	public void testResumeViewStateForRefresh() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		new EndState(flow, "finish");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		state.resume(context);
		assertTrue("Render not called", context.getFlowScope().contains("renderCalled"));
		assertFalse(context.getFlowExecutionRedirectSent());
	}

	public void testResumeViewStateForEvent() {
		Flow flow = new Flow("myFlow");
		StubViewFactory viewFactory = new StubViewFactory();
		ViewState state = new ViewState(flow, "viewState", viewFactory);
		state.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		new EndState(flow, "finish");
		MockRequestControlContext context = new MockRequestControlContext(flow);
		state.enter(context);
		context = new MockRequestControlContext(context.getFlowExecutionContext());
		context.putRequestParameter("_eventId", "submit");
		state.resume(context);
		assertFalse(context.getFlowExecutionContext().isActive());
	}

	protected TransitionCriteria on(String event) {
		return new EventIdTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}

}