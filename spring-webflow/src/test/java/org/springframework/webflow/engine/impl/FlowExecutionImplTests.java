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

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.MappingBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.ViewSelector;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.AbstractFlowBuilder;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.FlowBuilderException;
import org.springframework.webflow.engine.builder.xml.TestFlowServiceLocator;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilder;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilderTests;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.engine.support.TransitionExecutingStateExceptionHandler;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.MockFlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;

/**
 * General flow execution tests.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Ben Hale
 */
public class FlowExecutionImplTests extends TestCase {

	public void testFlowExecutionListener() {
		Flow flow = new Flow("myFlow");
		DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		inputMapper.addMapping(mapping.source("name").target("flowScope.name").value());
		flow.setInputMapper(inputMapper);
		ActionState actionState = new ActionState(flow, "actionState");
		actionState.getActionList().add(new TestAction());
		actionState.getTransitionSet().add(new Transition(onEvent("success"), toState("viewState")));

		ViewState viewState = new ViewState(flow, "viewState");
		viewState.setViewSelector(selectView("myView"));
		viewState.getTransitionSet().add(new Transition(onEvent("submit"), toState("subFlowState")));

		Flow subFlow = new Flow("mySubFlow");
		ViewState state1 = new ViewState(subFlow, "subFlowViewState");
		state1.setViewSelector(selectView("mySubFlowViewName"));
		state1.getTransitionSet().add(new Transition(onEvent("submit"), toState("finish")));
		new EndState(subFlow, "finish");

		SubflowState subflowState = new SubflowState(flow, "subFlowState", subFlow);
		subflowState.getTransitionSet().add(new Transition(onEvent("finish"), toState("finish")));

		EndState endState = new EndState(flow, "finish");
		endState.getEntryActionList().add(new AbstractAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				throw new IllegalStateException("Whoops!");
			}
		});
		TransitionExecutingStateExceptionHandler handler = new TransitionExecutingStateExceptionHandler();
		handler.add(Exception.class, "error");
		endState.getExceptionHandlerSet().add(handler);

		new EndState(flow, "error");

		MockFlowExecutionListener flowExecutionListener = new MockFlowExecutionListener();
		FlowExecutionImpl flowExecution = new FlowExecutionImpl(flow,
				new FlowExecutionListener[] { flowExecutionListener }, null);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("name", "value");
		assertTrue(!flowExecutionListener.isStarted());
		flowExecution.start(input, new MockExternalContext());
		assertTrue(flowExecutionListener.isStarted());
		assertTrue(flowExecutionListener.isPaused());
		assertTrue(!flowExecutionListener.isExecuting());
		assertEquals(1, flowExecutionListener.getEventsSignaledCount());
		assertEquals(0, flowExecutionListener.getFlowNestingLevel());
		assertEquals(2, flowExecutionListener.getTransitionCount());
		assertEquals("value", flowExecution.getActiveSession().getScope().getString("name"));
		flowExecution.signalEvent("submit", new MockExternalContext());
		assertTrue(!flowExecutionListener.isExecuting());
		assertEquals(2, flowExecutionListener.getEventsSignaledCount());
		assertEquals(1, flowExecutionListener.getFlowNestingLevel());
		assertEquals(4, flowExecutionListener.getTransitionCount());
		flowExecution.signalEvent("submit", new MockExternalContext());
		assertTrue(!flowExecutionListener.isExecuting());
		assertEquals(0, flowExecutionListener.getFlowNestingLevel());
		assertEquals(4, flowExecutionListener.getEventsSignaledCount());
		assertEquals(7, flowExecutionListener.getTransitionCount());
		assertEquals(1, flowExecutionListener.getExceptionsThrown());
		assertTrue(!flowExecution.isActive());
	}

	public void testLoopInFlow() throws Exception {
		AbstractFlowBuilder builder = new AbstractFlowBuilder() {
			public void buildStates() throws FlowBuilderException {
				addViewState("viewState", "viewName", new Transition[] { transition(on(submit()), to("viewState")),
						transition(on(finish()), to("endState")) });
				addEndState("endState");
			}
		};
		Flow flow = new FlowAssembler("flow", builder).assembleFlow();
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		ApplicationView view = (ApplicationView)flowExecution.start(null, new MockExternalContext());
		assertNotNull(view);
		assertEquals("viewName", view.getViewName());
		for (int i = 0; i < 10; i++) {
			view = (ApplicationView)flowExecution.signalEvent("submit", new MockExternalContext());
			assertEquals("viewName", view.getViewName());
		}
		assertTrue(flowExecution.isActive());
		flowExecution.signalEvent("finish", new MockExternalContext());
		assertFalse(flowExecution.isActive());
	}

	public void testLoopInFlowWithSubFlow() throws Exception {
		AbstractFlowBuilder childBuilder = new AbstractFlowBuilder() {
			public void buildStates() throws FlowBuilderException {
				addActionState("doOtherStuff", new AbstractAction() {
					private int executionCount = 0;

					protected Event doExecute(RequestContext context) throws Exception {
						executionCount++;
						if (executionCount < 2) {
							return success();
						}
						return error();
					}
				},
						new Transition[] { transition(on(success()), to(finish())),
								transition(on(error()), to("stopTest")) });
				addEndState(finish());
				addEndState("stopTest");
			}
		};
		final Flow childFlow = new FlowAssembler("flow", childBuilder).assembleFlow();
		AbstractFlowBuilder parentBuilder = new AbstractFlowBuilder() {
			public void buildStates() throws FlowBuilderException {
				addActionState("doStuff", new AbstractAction() {
					protected Event doExecute(RequestContext context) throws Exception {
						return success();
					}
				}, transition(on(success()), to("startSubFlow")));
				addSubflowState("startSubFlow", childFlow, null, new Transition[] {
						transition(on(finish()), to("startSubFlow")), transition(on("stopTest"), to("stopTest")) });
				addEndState("stopTest");
			}
		};
		Flow parentFlow = new FlowAssembler("parentFlow", parentBuilder).assembleFlow();

		FlowExecution flowExecution = new FlowExecutionImpl(parentFlow);
		flowExecution.start(null, new MockExternalContext());
		assertFalse(flowExecution.isActive());
	}

	public void testExtensiveFlowNavigationScenario1() {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("testFlow1.xml", XmlFlowBuilderTests.class),
				new TestFlowServiceLocator());
		FlowAssembler assembler = new FlowAssembler("testFlow1", builder);
		FlowExecution execution = new FlowExecutionImpl(assembler.assembleFlow());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertEquals("viewState1", execution.getActiveSession().getState().getId());
		assertNotNull(execution.getActiveSession().getScope().get("items"));
		execution.signalEvent("event1", context);
		assertTrue(!execution.isActive());
	}

	public void testExtensiveFlowNavigationScenario2() {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("testFlow1.xml", XmlFlowBuilderTests.class),
				new TestFlowServiceLocator());
		LocalAttributeMap attributes = new LocalAttributeMap();
		attributes.put("scenario2", Boolean.TRUE);
		FlowAssembler assembler = new FlowAssembler("testFlow1", attributes, builder);
		FlowExecution execution = new FlowExecutionImpl(assembler.assembleFlow());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		assertEquals("viewState2", execution.getActiveSession().getState().getId());
		assertNotNull(execution.getActiveSession().getScope().get("items"));
		execution.signalEvent("event2", context);
		assertTrue(!execution.isActive());
	}

	public void testFlashScope() {
		FlowExecution execution = new FlowExecutionImpl(new FlashScopeFlow());
		MockExternalContext context = new MockExternalContext();
		execution.start(null, context);
		execution.refresh(context);
		execution.refresh(context);
		execution.signalEvent("view", context);
	}

	public static TransitionCriteria onEvent(String event) {
		return new EventIdTransitionCriteria(event);
	}

	protected TargetStateResolver toState(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
	
	public static ViewSelector selectView(String viewName) {
		return new ApplicationViewSelector(new StaticExpression(viewName));
	}

	private class FlashScopeFlow extends Flow {

		public FlashScopeFlow() {
			super("flashScopeFlow");

			ActionState state1 = new ActionState(this, "action");
			state1.getActionList().add(new Action() {
				public Event execute(RequestContext context) throws Exception {
					context.getFlashScope().put("flashScopedValue", "flashScopedValue");
					return new Event(this, "success");
				}
			});
			state1.getTransitionSet().add(new Transition(toState("view")));

			ViewState state2 = new ViewState(this, "view");
			state2.getEntryActionList().add(new Action() {
				public Event execute(RequestContext context) throws Exception {
					assertTrue(context.getFlashScope().contains("flashScopedValue"));
					return new Event(this, "success");
				}
			});
			state2.getTransitionSet().add(new Transition(toState("end")));

			EndState state3 = new EndState(this, "end");
			state3.getEntryActionList().add(new Action() {
				public Event execute(RequestContext context) throws Exception {
					assertFalse(context.getFlashScope().contains("flashScopedValue"));
					return new Event(this, "success");
				}
			});
		}
	}
}