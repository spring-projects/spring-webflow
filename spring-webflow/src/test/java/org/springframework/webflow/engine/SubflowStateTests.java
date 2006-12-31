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

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.MappingBuilder;
import org.springframework.webflow.action.AttributeMapperAction;
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockParameterMap;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class SubflowStateTests extends TestCase {

	public void testSubFlowState() {
		Flow subFlow = new Flow("mySubFlow");
		ViewState state1 = new ViewState(subFlow, "subFlowViewState");
		state1.setViewSelector(view("mySubFlowViewName"));
		state1.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		new EndState(subFlow, "finish");

		Flow flow = new Flow("myFlow");
		SubflowState state2 = new SubflowState(flow, "subFlowState", subFlow);
		state2.getTransitionSet().add(new Transition(on("finish"), to("finish")));

		EndState state3 = new EndState(flow, "finish");
		state3.setViewSelector(view("myParentFlowEndingViewName"));

		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		ApplicationView view = (ApplicationView)flowExecution.start(null, new MockExternalContext());
		assertEquals("mySubFlow", flowExecution.getActiveSession().getDefinition().getId());
		assertEquals("subFlowViewState", flowExecution.getActiveSession().getState().getId());
		assertEquals("mySubFlowViewName", view.getViewName());
		view = (ApplicationView)flowExecution.signalEvent("submit", new MockExternalContext());
		assertEquals("myParentFlowEndingViewName", view.getViewName());
		assertTrue(!flowExecution.isActive());
	}

	public void testSubFlowStateModelMapping() {
		Flow subFlow = new Flow("mySubFlow");
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();
		inputMapper.addMapping(mapping.source("childInputAttribute").target("flowScope.childInputAttribute").value());
		subFlow.setInputMapper(inputMapper);
		ViewState state1 = new ViewState(subFlow, "subFlowViewState");
		state1.setViewSelector(view("mySubFlowViewName"));
		state1.getTransitionSet().add(new Transition(on("submit"), to("finish")));
		EndState state2 = new EndState(subFlow, "finish");
		DefaultAttributeMapper outputMapper = new DefaultAttributeMapper();
		outputMapper.addMapping(mapping.source("flowScope.childInputAttribute").target("childInputAttribute").value());
		state2.setOutputMapper(outputMapper);

		Flow flow = new Flow("myFlow");
		ActionState mapperState = new ActionState(flow, "mapperState");
		DefaultAttributeMapper mapper = new DefaultAttributeMapper();
		mapper.addMapping(mapping.source("externalContext.requestParameterMap.parentInputAttribute").target(
				"flowScope.parentInputAttribute").value());
		Action mapperAction = new AttributeMapperAction(mapper);
		mapperState.getActionList().add(mapperAction);
		mapperState.getTransitionSet().add(new Transition(on("success"), to("subFlowState")));

		SubflowState subflowState = new SubflowState(flow, "subFlowState", subFlow);
		subflowState.setAttributeMapper(new TestAttributeMapper());
		subflowState.getTransitionSet().add(new Transition(on("finish"), to("finish")));

		EndState endState = new EndState(flow, "finish");
		endState.setViewSelector(view("myParentFlowEndingViewName"));

		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		MockParameterMap input = new MockParameterMap();
		input.put("parentInputAttribute", "attributeValue");
		ApplicationView view = (ApplicationView)flowExecution.start(null, new MockExternalContext(input));
		assertEquals("mySubFlow", flowExecution.getActiveSession().getDefinition().getId());
		assertEquals("subFlowViewState", flowExecution.getActiveSession().getState().getId());
		assertEquals("mySubFlowViewName", view.getViewName());
		assertEquals("attributeValue", flowExecution.getActiveSession().getScope().get("childInputAttribute"));
		view = (ApplicationView)flowExecution.signalEvent("submit", new MockExternalContext());
		assertEquals("myParentFlowEndingViewName", view.getViewName());
		assertTrue(!flowExecution.isActive());
		assertEquals("attributeValue", view.getModel().get("parentOutputAttribute"));
	}

	protected TransitionCriteria on(String event) {
		return new EventIdTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
	
	protected ViewSelector view(String viewName) {
		return new ApplicationViewSelector(new StaticExpression(viewName));
	}
}