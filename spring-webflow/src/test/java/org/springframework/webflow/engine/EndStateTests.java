/*
 * Copyright 2002-2006 the original author or authors.
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
import org.springframework.webflow.core.DefaultExpressionParserFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class EndStateTests extends TestCase {

	public void testEndStateTerminateFlow() {
		Flow flow = new Flow("myFlow");
		EndState state = new EndState(flow, "finish");
		state.setViewSelector(view("myViewName"));
		FlowExecution flowExecution = new FlowExecutionImpl(flow);
		ApplicationView view = (ApplicationView)flowExecution.start(null, new MockExternalContext());
		assertFalse(flowExecution.isActive());
		assertEquals("myViewName", view.getViewName());
	}

	public void testEndStateTerminateFlowWithOutput() {
		Flow flow = new Flow("myFlow");
		DefaultAttributeMapper inputMapper = new DefaultAttributeMapper();
		MappingBuilder mapping = new MappingBuilder(DefaultExpressionParserFactory.getExpressionParser());
		inputMapper.addMapping(mapping.source("attr1").target("flowScope.attr1").value());
		flow.setInputMapper(inputMapper);

		EndState state = new EndState(flow, "finish");
		DefaultAttributeMapper outputMapper = new DefaultAttributeMapper();
		outputMapper.addMapping(mapping.source("flowScope.attr1").target("attr1").value());
		outputMapper.addMapping(mapping.source("flowScope.attr2").target("attr2").value());
		state.setOutputMapper(outputMapper);

		FlowExecutionListener outputVerifier = new FlowExecutionListenerAdapter() {
			public void sessionEnded(RequestContext context, FlowSession session, AttributeMap output) {
				assertEquals("value1", output.get("attr1"));
				assertNull(output.get("attr2"));
			}
		};
		FlowExecution flowExecution = new FlowExecutionImpl(flow, new FlowExecutionListener[] { outputVerifier }, null);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("attr1", "value1");
		ViewSelection view = flowExecution.start(input, new MockExternalContext());
		assertFalse(flowExecution.isActive());
		assertEquals(ViewSelection.NULL_VIEW, view);
	}

	protected static TransitionCriteria on(String event) {
		return new EventIdTransitionCriteria(event);
	}

	protected static String to(String stateId) {
		return stateId;
	}

	public static ViewSelector view(String viewName) {
		return new ApplicationViewSelector(new StaticExpression(viewName));
	}
}