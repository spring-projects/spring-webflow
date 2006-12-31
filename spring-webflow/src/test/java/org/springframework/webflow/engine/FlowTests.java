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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.MappingBuilder;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.webflow.TestException;
import org.springframework.webflow.action.TestMultiAction;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.BeanFactoryFlowVariable;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.engine.support.SimpleFlowVariable;
import org.springframework.webflow.engine.support.TransitionExecutingStateExceptionHandler;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Unit test for the Flow class.
 * 
 * @author Keith Donald
 */
public class FlowTests extends TestCase {

	private Flow flow = createSimpleFlow();

	private Flow createSimpleFlow() {
		flow = new Flow("myFlow");
		ViewState state1 = new ViewState(flow, "myState1");
		state1.setViewSelector(new ApplicationViewSelector(new StaticExpression("myView")));
		state1.getTransitionSet().add(new Transition(on("submit"), to("myState2")));
		EndState state2 = new EndState(flow, "myState2");
		state2.setViewSelector(new ApplicationViewSelector(new StaticExpression("myView2")));
		flow.getGlobalTransitionSet().add(new Transition(on("globalEvent"), to("myState2")));
		return flow;
	}

	public void testAddStates() {
		Flow flow = new Flow("myFlow");
		new EndState(flow, "myState1");
		new EndState(flow, "myState2");
		assertEquals("Wrong start state:", "myState1", flow.getStartState().getId());
		assertEquals("State count wrong:", 2, flow.getStateCount());
		assertTrue(flow.containsState("myState1"));
		assertTrue(flow.containsState("myState2"));
		State state = flow.getStateInstance("myState1");
		assertEquals("Wrong flow:", "myFlow", state.getFlow().getId());
		assertEquals("Wrong state:", "myState1", flow.getState("myState1").getId());
		assertEquals("Wrong state:", "myState2", flow.getState("myState2").getId());
	}

	public void testAddDuplicateState() {
		Flow flow = new Flow("myFlow");
		new EndState(flow, "myState1");
		try {
			new EndState(flow, "myState1");
			fail("Duplicate state added");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testAddSameStateTwice() {
		Flow flow = new Flow("myFlow");
		EndState state = new EndState(flow, "myState1");
		try {
			flow.add(state);
			fail("Should have failed");
		}
		catch (IllegalArgumentException e) {

		}
		assertEquals("State count wrong:", 1, flow.getStateCount());
	}

	public void testAddStateAlreadyInOtherFlow() {
		Flow otherFlow = new Flow("myOtherFlow");
		State state = new EndState(otherFlow, "myState1");
		Flow flow = new Flow("myFlow");
		try {
			flow.add(state);
			fail("Added state part of another flow");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testGetStateNoStartState() {
		Flow flow = new Flow("myFlow");
		try {
			flow.getStartState();
			fail("Retrieved start state when no such state");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	public void testGetStateNoSuchState() {
		try {
			flow.getState("myState3");
			fail("Returned a state that doesn't exist");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testGetTransitionableState() {
		assertEquals("Wrong state:", "myState1", flow.getTransitionableState("myState1").getId());
		assertEquals("Wrong state:", "myState1", flow.getState("myState1").getId());
	}

	public void testGetStateNoSuchTransitionableState() {
		try {
			flow.getTransitionableState("myState2");
			fail("End states aren't transtionable");
		}
		catch (ClassCastException e) {
			// expected
		}
		try {
			flow.getTransitionableState("doesNotExist");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testAddActions() {
		flow.getStartActionList().add(new TestMultiAction());
		flow.getStartActionList().add(new TestMultiAction());
		flow.getEndActionList().add(new TestMultiAction());
		assertEquals(2, flow.getStartActionList().size());
		assertEquals(1, flow.getEndActionList().size());
	}

	public void testAddInlineFlow() {
		Flow inline = new Flow("inline");
		flow.addInlineFlow(inline);
		assertSame(inline, flow.getInlineFlow("inline"));
		assertEquals(1, flow.getInlineFlowCount());
		String[] inlined = flow.getInlineFlowIds();
		assertEquals(1, inlined.length);
		assertSame(flow.getInlineFlows()[0], inline);
	}

	public void testAddGlobalTransition() {
		Transition t = new Transition(to("myState2"));
		flow.getGlobalTransitionSet().add(t);
		assertSame(t, flow.getGlobalTransitionSet().toArray()[1]);
	}

	public void testStart() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		flow.start(context, new LocalAttributeMap());
		assertEquals("Wrong start state", "myState1", context.getCurrentState().getId());
	}

	public void testStartWithAction() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		TestAction action = new TestAction();
		flow.getStartActionList().add(action);
		flow.start(context, new LocalAttributeMap());
		assertEquals("Wrong start state", "myState1", context.getCurrentState().getId());
		assertEquals(1, action.getExecutionCount());
	}

	public void testStartWithVariables() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		flow.addVariable(new SimpleFlowVariable("var1", ArrayList.class, ScopeType.FLOW));
		StaticApplicationContext beanFactory = new StaticApplicationContext();
		beanFactory.registerPrototype("bean", ArrayList.class);
		flow.addVariable(new BeanFactoryFlowVariable("var2", "bean", beanFactory, ScopeType.FLOW));
		flow.start(context, new LocalAttributeMap());
		assertEquals(2, context.getFlowScope().size());
		context.getFlowScope().getRequired("var1", ArrayList.class);
		context.getFlowScope().getRequired("var2", ArrayList.class);
	}

	public void testStartWithMapper() {
		DefaultAttributeMapper attributeMapper = new DefaultAttributeMapper();
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		attributeMapper.addMapping(mapping.source("attr").target("flowScope.attr").value());
		flow.setInputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap sessionInput = new LocalAttributeMap();
		sessionInput.put("attr", "foo");
		flow.start(context, sessionInput);
		assertEquals("foo", context.getFlowScope().get("attr"));
	}

	public void testStartWithMapperButNoInput() {
		DefaultAttributeMapper attributeMapper = new DefaultAttributeMapper();
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		attributeMapper.addMapping(mapping.source("attr").target("flowScope.attr").value());
		flow.setInputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap sessionInput = new LocalAttributeMap();
		flow.start(context, sessionInput);
		assertFalse(context.getFlowScope().contains("attr"));
	}

	public void testOnEventNullCurrentState() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		Event event = new Event(this, "foo");
		try {
			context.setLastEvent(event);
			flow.onEvent(context);
		}
		catch (IllegalStateException e) {

		}
	}

	public void testOnEventInvalidCurrentState() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState2"));
		Event event = new Event(this, "submit");
		context.setLastEvent(event);
		try {
			context.setLastEvent(event);
			flow.onEvent(context);
		}
		catch (IllegalStateException e) {

		}
	}

	public void testOnEvent() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "submit");
		context.setLastEvent(event);
		assertTrue(context.getFlowExecutionContext().isActive());
		context.setLastEvent(event);
		flow.onEvent(context);
		assertTrue(!context.getFlowExecutionContext().isActive());
	}

	public void testOnEventGlobalTransition() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "globalEvent");
		context.setLastEvent(event);
		assertTrue(context.getFlowExecutionContext().isActive());
		context.setLastEvent(event);
		flow.onEvent(context);
		assertTrue(!context.getFlowExecutionContext().isActive());
	}

	public void testOnEventNoTransition() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "bogus");
		context.setLastEvent(event);
		try {
			context.setLastEvent(event);
			flow.onEvent(context);
		}
		catch (NoMatchingTransitionException e) {

		}
	}

	public void testEnd() {
		TestAction action = new TestAction();
		flow.getEndActionList().add(action);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap sessionOutput = new LocalAttributeMap();
		flow.end(context, sessionOutput);
		assertEquals(1, action.getExecutionCount());
	}

	public void testEndWithOutputMapper() {
		DefaultAttributeMapper attributeMapper = new DefaultAttributeMapper();
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		attributeMapper.addMapping(mapping.source("flowScope.attr").target("attr").value());
		flow.setOutputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlowScope().put("attr", "foo");
		LocalAttributeMap sessionOutput = new LocalAttributeMap();
		flow.end(context, sessionOutput);
		assertEquals("foo", sessionOutput.get("attr"));
	}

	public void testHandleStateException() {
		flow.getExceptionHandlerSet().add(
				new TransitionExecutingStateExceptionHandler().add(TestException.class, "myState2"));
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		FlowExecutionException e = new FlowExecutionException(flow.getId(), flow.getStartState().getId(), "Oops!",
				new TestException());
		ApplicationView selectedView = (ApplicationView)flow.handleException(e, context);
		assertFalse(context.getFlowExecutionContext().isActive());
		assertNotNull("Should not have been null", selectedView);
		assertEquals("Wrong selected view", "myView2", selectedView.getViewName());
	}

	public void testHandleStateExceptionNoMatch() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		FlowExecutionException e = new FlowExecutionException(flow.getId(), flow.getStartState().getId(), "Oops!",
				new TestException());
		try {
			flow.handleException(e, context);
		}
		catch (FlowExecutionException ex) {
			// expected
		}
	}

	public TransitionCriteria on(String eventId) {
		return new EventIdTransitionCriteria(eventId);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}