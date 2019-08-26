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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.TestException;
import org.springframework.webflow.action.TestMultiAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.MockTransitionCriteria;
import org.springframework.webflow.engine.support.TransitionExecutingFlowExecutionExceptionHandler;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.TestAction;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Unit test for the Flow class.
 * 
 * @author Keith Donald
 */
public class FlowTests {

	private Flow flow = createSimpleFlow();

	private Flow createSimpleFlow() {
		flow = new Flow("myFlow");
		ViewState state1 = new ViewState(flow, "myState1", new StubViewFactory());
		state1.getTransitionSet().add(new Transition(on("submit"), to("myState2")));
		new EndState(flow, "myState2");
		flow.getGlobalTransitionSet().add(new Transition(on("globalEvent"), to("myState2")));
		return flow;
	}

	@Test
	public void testAddStates() {
		Flow flow = new Flow("myFlow");
		new EndState(flow, "myState1");
		new EndState(flow, "myState2");
		assertEquals("myState1", flow.getStartState().getId(), "Wrong start state:");
		assertEquals(2, flow.getStateCount(), "State count wrong:");
		assertTrue(flow.containsState("myState1"));
		assertTrue(flow.containsState("myState2"));
		State state = flow.getStateInstance("myState1");
		assertEquals(flow.getId(), state.getFlow().getId(), "Wrong flow:");
		assertEquals("myState1", flow.getState("myState1").getId(), "Wrong state:");
		assertEquals("myState2", flow.getState("myState2").getId(), "Wrong state:");
	}

	@Test
	public void testAddDuplicateState() {
		Flow flow = new Flow("myFlow");
		new EndState(flow, "myState1");
		try {
			new EndState(flow, "myState1");
			fail("Duplicate state added");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testAddSameStateTwice() {
		Flow flow = new Flow("myFlow");
		EndState state = new EndState(flow, "myState1");
		try {
			flow.add(state);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {

		}
		assertEquals(1, flow.getStateCount(), "State count wrong:");
	}

	@Test
	public void testAddStateAlreadyInOtherFlow() {
		Flow otherFlow = new Flow("myOtherFlow");
		State state = new EndState(otherFlow, "myState1");
		Flow flow = new Flow("myFlow");
		try {
			flow.add(state);
			fail("Added state part of another flow");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testGetStateNoStartState() {
		Flow flow = new Flow("myFlow");
		try {
			flow.getStartState();
			fail("Retrieved start state when no such state");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	@Test
	public void testGetStateNoSuchState() {
		try {
			flow.getState("myState3");
			fail("Returned a state that doesn't exist");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testGetTransitionableState() {
		assertEquals("myState1", flow.getTransitionableState("myState1").getId(), "Wrong state:");
		assertEquals("myState1", flow.getState("myState1").getId(), "Wrong state:");
	}

	@Test
	public void testGetStateNoSuchTransitionableState() {
		try {
			flow.getTransitionableState("myState2");
			fail("End states aren't transtionable");
		} catch (ClassCastException e) {
			// expected
		}
		try {
			flow.getTransitionableState("doesNotExist");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testGetPossibleOutcomes() {
		Flow flow = new Flow("myFlow");
		new EndState(flow, "myState1");
		new EndState(flow, "myState2");
		assertEquals("myState1", flow.getPossibleOutcomes()[0]);
		assertEquals("myState2", flow.getPossibleOutcomes()[1]);
	}

	@Test
	public void testAddActions() {
		flow.getStartActionList().add(new TestMultiAction());
		flow.getStartActionList().add(new TestMultiAction());
		flow.getEndActionList().add(new TestMultiAction());
		assertEquals(2, flow.getStartActionList().size());
		assertEquals(1, flow.getEndActionList().size());
	}

	@Test
	public void testAddGlobalTransition() {
		Transition t = new Transition(to("myState2"));
		flow.getGlobalTransitionSet().add(t);
		assertSame(t, flow.getGlobalTransitionSet().toArray()[1]);
	}

	@Test
	public void testStart() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		flow.start(context, new LocalAttributeMap<>());
		assertEquals("myState1", context.getCurrentState().getId(), "Wrong start state");
	}

	@Test
	public void testStartWithoutStartState() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		try {
			Flow empty = new Flow("empty");
			empty.start(context, null);
			fail("should have failed");
		} catch (IllegalStateException e) {

		}
	}

	@Test
	public void testStartWithAction() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		TestAction action = new TestAction();
		flow.getStartActionList().add(action);
		flow.start(context, new LocalAttributeMap<>());
		assertEquals("myState1", context.getCurrentState().getId(), "Wrong start state");
		assertEquals(1, action.getExecutionCount());
	}

	@Test
	public void testStartWithVariables() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		flow.addVariable(new FlowVariable("var1", new VariableValueFactory() {
			public Object createInitialValue(RequestContext context) {
				return new ArrayList<>();
			}

			public void restoreReferences(Object value, RequestContext context) {
			}
		}));
		flow.start(context, new LocalAttributeMap<>());
		context.getFlowScope().getRequired("var1", ArrayList.class);
	}

	@Test
	public void testStartWithMapper() {
		DefaultMapper attributeMapper = new DefaultMapper();
		ExpressionParser parser = new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		Expression x = parser.parseExpression("attr", new FluentParserContext().evaluate(AttributeMap.class));
		Expression y = parser.parseExpression("flowScope.attr",
				new FluentParserContext().evaluate(RequestContext.class));
		attributeMapper.addMapping(new DefaultMapping(x, y));
		flow.setInputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap<Object> sessionInput = new LocalAttributeMap<>();
		sessionInput.put("attr", "foo");
		flow.start(context, sessionInput);
		assertEquals("foo", context.getFlowScope().get("attr"));
	}

	@Test
	public void testStartWithMapperButNoInput() {
		DefaultMapper attributeMapper = new DefaultMapper();
		ExpressionParser parser = new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		Expression x = parser.parseExpression("attr", new FluentParserContext().evaluate(AttributeMap.class));
		Expression y = parser.parseExpression("flowScope.attr",
				new FluentParserContext().evaluate(RequestContext.class));
		attributeMapper.addMapping(new DefaultMapping(x, y));
		flow.setInputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap<Object> sessionInput = new LocalAttributeMap<>();
		flow.start(context, sessionInput);
		assertTrue(context.getFlowScope().contains("attr"));
		assertNull(context.getFlowScope().get("attr"));
	}

	@Test
	public void testOnEventNullCurrentState() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		Event event = new Event(this, "foo");
		try {
			context.setCurrentEvent(event);
			flow.handleEvent(context);
		} catch (IllegalStateException e) {

		}
	}

	@Test
	public void testOnEventInvalidCurrentState() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState2"));
		Event event = new Event(this, "submit");
		context.setCurrentEvent(event);
		try {
			context.setCurrentEvent(event);
			flow.handleEvent(context);
		} catch (IllegalStateException e) {

		}
	}

	@Test
	public void testOnEvent() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "submit");
		context.setCurrentEvent(event);
		assertTrue(context.getFlowExecutionContext().isActive());
		context.setCurrentEvent(event);
		flow.handleEvent(context);
		assertTrue(!context.getFlowExecutionContext().isActive());
	}

	@Test
	public void testOnEventGlobalTransition() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "globalEvent");
		context.setCurrentEvent(event);
		assertTrue(context.getFlowExecutionContext().isActive());
		context.setCurrentEvent(event);
		flow.handleEvent(context);
		assertTrue(!context.getFlowExecutionContext().isActive());
	}

	@Test
	public void testOnEventNoTransition() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		Event event = new Event(this, "bogus");
		context.setCurrentEvent(event);
		try {
			context.setCurrentEvent(event);
			flow.handleEvent(context);
		} catch (NoMatchingTransitionException e) {

		}
	}

	@Test
	public void testResume() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		flow.resume(context);
		assertTrue(context.getFlowScope().getBoolean("renderCalled"));
	}

	@Test
	public void testEnd() {
		TestAction action = new TestAction();
		flow.getEndActionList().add(action);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		LocalAttributeMap<Object> sessionOutput = new LocalAttributeMap<>();
		flow.end(context, "finish", sessionOutput);
		assertEquals(1, action.getExecutionCount());
	}

	@Test
	public void testEndWithOutputMapper() {
		DefaultMapper attributeMapper = new DefaultMapper();
		ExpressionParser parser = new WebFlowSpringELExpressionParser(new SpelExpressionParser());
		Expression x = parser.parseExpression("flowScope.attr",
				new FluentParserContext().evaluate(RequestContext.class));
		Expression y = parser.parseExpression("attr", new FluentParserContext().evaluate(MutableAttributeMap.class));
		attributeMapper.addMapping(new DefaultMapping(x, y));
		flow.setOutputMapper(attributeMapper);
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.getFlowScope().put("attr", "foo");
		LocalAttributeMap<Object> sessionOutput = new LocalAttributeMap<>();
		flow.end(context, "finish", sessionOutput);
		assertEquals("foo", sessionOutput.get("attr"));
	}

	@Test
	public void testHandleException() {
		flow.getExceptionHandlerSet().add(
				new TransitionExecutingFlowExecutionExceptionHandler().add(TestException.class, "myState2"));
		MockRequestControlContext context = new MockRequestControlContext(flow);
		context.setCurrentState(flow.getStateInstance("myState1"));
		FlowExecutionException e = new FlowExecutionException(flow.getId(), flow.getStartState().getId(), "Oops!",
				new TestException());
		flow.handleException(e, context);
		assertFalse(context.getFlowExecutionContext().isActive());
	}

	@Test
	public void testHandleExceptionNoMatch() {
		MockRequestControlContext context = new MockRequestControlContext(flow);
		FlowExecutionException e = new FlowExecutionException(flow.getId(), flow.getStartState().getId(), "Oops!",
				new TestException());
		try {
			flow.handleException(e, context);
		} catch (FlowExecutionException ex) {
			// expected
		}
	}

	@Test
	public void testDestroy() {
		GenericApplicationContext context = new GenericApplicationContext();
		context.refresh();
		flow.setApplicationContext(context);
		assertTrue(context.isActive());
		flow.destroy();
		assertFalse(context.isActive());
	}

	public TransitionCriteria on(String eventId) {
		return new MockTransitionCriteria(eventId);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}
