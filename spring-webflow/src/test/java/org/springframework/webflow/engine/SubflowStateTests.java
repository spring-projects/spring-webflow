/*
 * Copyright 2004-2008 the original author or authors.
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

import java.util.Collections;

import junit.framework.TestCase;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.support.AbstractGetValueExpression;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.impl.DefaultMappingResults;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.MockTransitionCriteria;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestControlContext;

/**
 * Tests that each of the Flow state types execute as expected when entered.
 * 
 * @author Keith Donald
 */
public class SubflowStateTests extends TestCase {

	private Flow parentFlow;
	private SubflowState subflowState;
	private Flow subflow;
	private MockRequestControlContext context;

	public void setUp() {
		parentFlow = new Flow("parent");
		subflow = new Flow("child");
		subflowState = new SubflowState(parentFlow, "subflow", new AbstractGetValueExpression() {
			public Object getValue(Object context) throws EvaluationException {
				return subflow;
			}
		});
		context = new MockRequestControlContext(parentFlow);
		context.setCurrentState(subflowState);
	}

	public void testEnter() {
		new State(subflow, "whatev") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		subflowState.enter(context);
		assertEquals("child", context.getActiveFlow().getId());
	}

	public void testEnterWithInput() {
		subflowState.setAttributeMapper(new SubflowAttributeMapper() {
			public MutableAttributeMap createSubflowInput(RequestContext context) {
				return new LocalAttributeMap("foo", "bar");
			}

			public void mapSubflowOutput(AttributeMap flowOutput, RequestContext context) {
			}
		});
		subflow.setInputMapper(new Mapper() {
			public MappingResults map(Object source, Object target) {
				MutableAttributeMap map = (MutableAttributeMap) source;
				assertEquals("bar", map.get("foo"));
				return new DefaultMappingResults(source, target, Collections.EMPTY_LIST);
			}
		});
		new State(subflow, "whatev") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		subflowState.enter(context);
		assertEquals("child", context.getActiveFlow().getId());
	}

	public void testReturnWithOutput() {
		subflowState.setAttributeMapper(new SubflowAttributeMapper() {
			public MutableAttributeMap createSubflowInput(RequestContext context) {
				return new LocalAttributeMap();
			}

			public void mapSubflowOutput(AttributeMap flowOutput, RequestContext context) {
				assertEquals("bar", flowOutput.get("foo"));
			}
		});
		subflowState.getTransitionSet().add(new Transition(on("end"), to("whatev")));
		new State(parentFlow, "whatev") {
			protected void doEnter(RequestControlContext context) throws FlowExecutionException {
			}
		};
		new EndState(subflow, "end");
		subflow.setOutputMapper(new Mapper() {
			public MappingResults map(Object source, Object target) {
				MutableAttributeMap map = (MutableAttributeMap) target;
				map.put("foo", "bar");
				return new DefaultMappingResults(source, target, Collections.EMPTY_LIST);
			}
		});
		subflowState.enter(context);
		assertEquals("parent", context.getActiveFlow().getId());
	}

	protected TransitionCriteria on(String event) {
		return new MockTransitionCriteria(event);
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}

}