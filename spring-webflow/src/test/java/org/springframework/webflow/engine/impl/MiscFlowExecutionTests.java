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
import org.springframework.binding.mapping.RequiredMappingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilder;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowServiceLocator;

/**
 * Miscellaneous flow execution tests.
 */
public class MiscFlowExecutionTests extends TestCase {

	public void testRequestScopePutInEntryAction() {
		Flow parentFlow = new Flow("parent");
		Flow flow = new Flow("test");
		new SubflowState(parentFlow, "parentState", flow);

		ViewState state = new ViewState(flow, "view");
		state.setViewSelector(new ApplicationViewSelector(new StaticExpression("myView")));
		final Object order = new Object();
		state.getEntryActionList().add(new AbstractAction() {
			protected Event doExecute(RequestContext context) {
				context.getRequestScope().put("order", order);
				return success();
			}
		});
		FlowExecution execution = new FlowExecutionImpl(parentFlow);
		ApplicationView response = (ApplicationView)execution.start(null, new MockExternalContext());
		assertNotNull(response.getModel().get("order"));
		assertEquals(order, response.getModel().get("order"));
	}

	public void testRequiredMapping() {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("required-mapping.xml", getClass()));
		Flow flow = new FlowAssembler("myFlow", builder).assembleFlow();
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("id", "23");
		ApplicationView view = (ApplicationView)execution.start(input, new MockExternalContext());
		assertEquals(new Long(23), view.getModel().get("id"));
	}
	
	public void testRequiredMappingException() {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("required-mapping.xml", getClass()));
		Flow flow = new FlowAssembler("myFlow", builder).assembleFlow();
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		try {
			execution.start(null, new MockExternalContext());
		} catch (RequiredMappingException e) {
			
		}
	}

	public void testInfiniteLoop() {
		MockFlowServiceLocator serviceLocator = new MockFlowServiceLocator();
		serviceLocator.registerBean("action", new InfiniteLoopTestAction());
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("infinite-loop.xml", getClass()));
		builder.setFlowServiceLocator(serviceLocator);
		Flow flow = new FlowAssembler("myFlow", builder).assembleFlow();
		FlowExecutionImpl execution = new FlowExecutionImpl(flow);
		try {
			execution.start(null, new MockExternalContext());
		}
		catch (StackOverflowError e) {
			// expected
		}
	}
}
