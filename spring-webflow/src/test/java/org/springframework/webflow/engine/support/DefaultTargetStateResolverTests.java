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
package org.springframework.webflow.engine.support;

import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.builder.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.execution.AbstractFlowExecutionTests;

/**
 * Unit tests for {@link DefaultTargetStateResolver}.
 * 
 * @author Erwin Vervaet
 */
public class DefaultTargetStateResolverTests extends AbstractFlowExecutionTests {
	
	private boolean fail = false;
	
	protected FlowDefinition getFlowDefinition() {
		return new FlowAssembler("testFlow", new TestFlowBuilder()).assembleFlow();
	}
	
	public void testNonNullSourceState() {
		fail = false;
		ViewSelection viewSelection = startFlow();
		assertFlowExecutionActive();
		assertCurrentStateEquals("stateA");
		assertEquals("stateAView", ((ApplicationView)viewSelection).getViewName());
		viewSelection = signalEvent("aEvent");
		assertFlowExecutionActive();
		assertCurrentStateEquals("stateB");
		assertEquals("stateBView", ((ApplicationView)viewSelection).getViewName());
		viewSelection = signalEvent("bEvent");
		assertFlowExecutionEnded();
		assertTrue(viewSelection == ViewSelection.NULL_VIEW);
	}

	public void testNullSourceState() {
		fail = true;
		ViewSelection viewSelection = startFlow();
		assertFlowExecutionEnded();
		assertTrue(viewSelection == ViewSelection.NULL_VIEW);
	}
	
	private class TestFlowBuilder extends AbstractFlowBuilder {
		
		public void buildStartActions() throws FlowBuilderException {
			getFlow().getStartActionList().add(new Action() {
				public Event execute(RequestContext context) throws Exception {
					if (fail) {
						throw new UnsupportedOperationException();
					}
					return new Event(this, "success");
				}
			});
		}
		
		public void buildExceptionHandlers() throws FlowBuilderException {
			TransitionExecutingStateExceptionHandler handler = new TransitionExecutingStateExceptionHandler();
			handler.add(UnsupportedOperationException.class, "stateC");
			getFlow().getExceptionHandlerSet().add(handler);
		}
		
		public void buildStates() throws FlowBuilderException {
			addViewState("stateA",  "stateAView", transition(on("aEvent"), to("stateB")));
			addViewState("stateB", "stateBView", transition(on("bEvent"), to("stateC")));
			addEndState("stateC");
		}
	}
}
