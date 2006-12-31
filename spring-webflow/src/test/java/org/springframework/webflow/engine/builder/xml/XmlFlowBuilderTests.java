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
package org.springframework.webflow.engine.builder.xml;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.RequiredMapping;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractBeanInvokingAction;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.DecisionState;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.TransitionExecutingStateExceptionHandler;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Test case for XML flow builder.
 * 
 * @see org.springframework.webflow.engine.builder.xml.XmlFlowBuilder
 * 
 * @author Erwin Vervaet
 */
public class XmlFlowBuilderTests extends TestCase {

	private Flow flow;

	private MockRequestContext context;

	protected void setUp() throws Exception {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("testFlow1.xml", XmlFlowBuilderTests.class),
				new TestFlowServiceLocator());
		flow = new FlowAssembler("testFlow1", builder).assembleFlow();
		context = new MockRequestContext();
	}

	private Event createEvent(String id) {
		return new Event(this, id);
	}

	public void testBuildResult() {
		assertNotNull(flow);
		assertEquals("testFlow1", flow.getId());
		assertEquals("actionState1", flow.getStartState().getId());
		assertEquals(14, flow.getStateIds().length);

		assertEquals(5, flow.getVariables().length);
		assertEquals(1, flow.getStartActionList().size());
		assertEquals(1, flow.getEndActionList().size());
		assertEquals(1, flow.getExceptionHandlerSet().size());
		assertTrue(flow.getExceptionHandlerSet().toArray()[0] instanceof TransitionExecutingStateExceptionHandler);

		ActionState actionState1 = (ActionState)flow.getState("actionState1");
		assertNotNull(actionState1);
		assertEquals(2, actionState1.getActionList().size());
		assertEquals(null, actionState1.getActionList().getAnnotated(0).getCaption());
		assertEquals(Boolean.TRUE, actionState1.getAttributeMap().getBoolean("propBoolean"));
		assertEquals("aString", actionState1.getAttributeMap().getString("propString"));
		assertEquals("action2Name", actionState1.getActionList().getAnnotated(1).getName());
		assertEquals(3, actionState1.getTransitionSet().size());
		context.setLastEvent(createEvent("event1"));
		assertTrue(actionState1.getTransitionSet().hasMatchingTransition(context));
		Transition transition = actionState1.getRequiredTransition(context);
		assertEquals("viewState1", getTargetStateId(transition));
		assertEquals(new BigDecimal("12345"), transition.getAttributeMap().get("propBigDecimal"));
		context.setLastEvent(createEvent("action2Name.event2"));
		assertTrue(actionState1.getTransitionSet().hasMatchingTransition(context));
		transition = actionState1.getRequiredTransition(context);
		assertEquals("viewState2", getTargetStateId(transition));
		assertEquals("prop1Value", actionState1.getActionList().getAnnotated(0).getAttributeMap().get("prop1"));
		assertEquals("prop2Value", actionState1.getActionList().getAnnotated(0).getAttributeMap().get("prop2"));

		ActionState actionState2 = (ActionState)flow.getState("actionState2");
		assertEquals(1, actionState2.getExceptionHandlerSet().size());

		ViewState viewState1 = (ViewState)flow.getState("viewState1");
		assertNotNull(viewState1);
		assertEquals("view1", (String)((ApplicationViewSelector)viewState1.getViewSelector()).getViewName().evaluate(
				null, null));
		assertEquals(1, viewState1.getTransitionSet().size());
		assertEquals(1, viewState1.getRenderActionList().size());
		context.setLastEvent(createEvent("event1"));
		assertTrue(viewState1.getTransitionSet().hasMatchingTransition(context));
		transition = viewState1.getRequiredTransition(context);
		assertEquals("subFlowState1", getTargetStateId(transition));

		ViewState viewState2 = (ViewState)flow.getState("viewState2");
		assertNotNull(viewState2);
		assertEquals(1, viewState2.getTransitionSet().size());
		context.setLastEvent(createEvent("event2"));
		assertTrue(viewState2.getTransitionSet().hasMatchingTransition(context));
		transition = viewState2.getRequiredTransition(context);
		assertEquals("subFlowState2", getTargetStateId(transition));

		SubflowState subFlowState1 = (SubflowState)flow.getState("subFlowState1");
		assertNotNull(subFlowState1);
		assertNotNull(subFlowState1.getSubflow());
		assertEquals("subFlow1", subFlowState1.getSubflow().getId());
		assertNotNull(subFlowState1.getAttributeMapper());
		assertEquals(1, subFlowState1.getTransitionSet().size());
		context.setLastEvent(createEvent("finish"));
		assertTrue(subFlowState1.getTransitionSet().hasMatchingTransition(context));
		transition = subFlowState1.getRequiredTransition(context);
		assertEquals("spawnInlineFlow", getTargetStateId(transition));

		SubflowState subFlowState2 = (SubflowState)flow.getState("subFlowState2");
		assertNotNull(subFlowState2);
		assertNotNull(subFlowState2.getSubflow());
		assertEquals("subFlow2", subFlowState2.getSubflow().getId());
		assertNotNull(subFlowState2.getAttributeMapper());
		ImmutableFlowAttributeMapper mapper = (ImmutableFlowAttributeMapper)subFlowState2.getAttributeMapper();
		assertEquals(3, ((DefaultAttributeMapper)mapper.getInputMapper()).getMappings().length);
		assertEquals(4, ((DefaultAttributeMapper)mapper.getOutputMapper()).getMappings().length);
		assertTrue(((DefaultAttributeMapper)mapper.getInputMapper()).getMappings()[0] instanceof RequiredMapping);

		assertEquals(1, subFlowState2.getTransitionSet().size());
		context.setLastEvent(createEvent("finish"));
		assertTrue(subFlowState2.getTransitionSet().hasMatchingTransition(context));
		transition = subFlowState2.getRequiredTransition(context);
		assertEquals("decisionState1", getTargetStateId(transition));

		DecisionState decisionState1 = (DecisionState)flow.getState("decisionState1");
		assertTrue(decisionState1.getTransitionSet().size() == 2);
		assertNotNull(decisionState1);

		DecisionState decisionState2 = (DecisionState)flow.getState("decisionState2");
		assertTrue(decisionState2.getTransitionSet().size() == 2);
		assertNotNull(decisionState2);

		ActionState actionState4 = (ActionState)flow.getState("actionState4");
		assertTrue(actionState4.getTransitionSet().size() == 2);
		assertNotNull(actionState4);
		assertNotNull(actionState4.getActionList().get(0));
		assertNull(actionState4.getActionList().getAnnotated(0).getAttributeMap().get("method"));
		assertTrue(actionState4.getActionList().getAnnotated(0).getTargetAction() instanceof AbstractBeanInvokingAction);

		ActionState actionState5 = (ActionState)flow.getState("actionState5");
		assertTrue(actionState5.getTransitionSet().size() == 2);
		assertNotNull(actionState5);
		assertNotNull(actionState5.getActionList().get(0));
		assertNull(actionState5.getActionList().getAnnotated(0).getAttributeMap().get("method"));
		assertTrue(actionState5.getActionList().getAnnotated(0).getTargetAction() instanceof AbstractBeanInvokingAction);

		EndState endState1 = (EndState)flow.getState("endState1");
		assertNotNull(endState1);
		assertEquals("endView1", (String)((ApplicationViewSelector)endState1.getViewSelector()).getViewName().evaluate(
				null, null));

		EndState endState2 = (EndState)flow.getState("endState2");
		assertNotNull(endState2);

		Flow inlineFlow = flow.getInlineFlow("inline-flow");
		assertNotNull(inlineFlow);
		assertNotNull(inlineFlow.getInputMapper());
		assertNotNull(inlineFlow.getOutputMapper());
		assertEquals(1, inlineFlow.getVariables().length);
		assertEquals(1, inlineFlow.getStartActionList().size());
		assertEquals(1, inlineFlow.getEndActionList().size());
		EndState endState3 = (EndState)inlineFlow.getState("end");
		assertNotNull(endState3);
		assertNotNull(endState3.getOutputMapper());
	}

	protected String getTargetStateId(Transition transition) {
		return transition.getTargetStateId();
	}

}