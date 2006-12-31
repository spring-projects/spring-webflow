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
package org.springframework.webflow.executor.support;

import junit.framework.TestCase;

import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutorImpl;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Unit tests for {@link FlowRequestHandler}.
 */
public class FlowRequestHandlerTests extends TestCase {

	private FlowRequestHandler handler;

	private MockExternalContext context = new MockExternalContext();

	protected void setUp() throws Exception {
		FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();
		Flow flow = new Flow("flow");
		ViewState view = new ViewState(flow, "view");
		view.getTransitionSet().add(new Transition(to("end")));
		new EndState(flow, "end");
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(flow));
		FlowExecutorImpl executor = new FlowExecutorImpl(registry, new FlowExecutionImplFactory(),
				new SimpleFlowExecutionRepository(new FlowExecutionImplStateRestorer(registry),
						new SessionBindingConversationManager()));
		handler = new FlowRequestHandler(executor);
	}

	public void testLaunch() {
		context.putRequestParameter("_flowId", "flow");
		ResponseInstruction response = handler.handleFlowRequest(context);
		assertTrue(response.isNull());
		assertTrue(response.getFlowExecutionContext().isActive());
		assertEquals("flow", response.getFlowExecutionContext().getDefinition().getId());
		assertEquals("view", response.getFlowExecutionContext().getActiveSession().getState().getId());
	}

	public void testResumeOnEvent() {
		context.putRequestParameter("_flowId", "flow");
		ResponseInstruction response = handler.handleFlowRequest(context);

		String flowExecutionKey = response.getFlowExecutionKey();
		context.putRequestParameter("_flowExecutionKey", flowExecutionKey);
		context.putRequestParameter("_eventId", "submit");
		response = handler.handleFlowRequest(context);

		assertTrue(response.isNull());
		assertTrue(!response.getFlowExecutionContext().isActive());
		assertEquals("flow", response.getFlowExecutionContext().getDefinition().getId());

	}

	public void testRefreshFlowExecution() {
		context.putRequestParameter("_flowId", "flow");
		ResponseInstruction response = handler.handleFlowRequest(context);

		String flowExecutionKey = response.getFlowExecutionKey();
		context.putRequestParameter("_flowExecutionKey", flowExecutionKey);
		response = handler.handleFlowRequest(context);

		assertTrue(response.isNull());
		assertTrue(response.getFlowExecutionContext().isActive());
		assertEquals("flow", response.getFlowExecutionContext().getDefinition().getId());
		assertEquals("view", response.getFlowExecutionContext().getActiveSession().getState().getId());
	}
	
	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}
}