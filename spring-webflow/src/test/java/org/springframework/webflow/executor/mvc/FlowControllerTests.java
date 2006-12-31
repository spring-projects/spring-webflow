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
package org.springframework.webflow.executor.mvc;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.StaticFlowDefinitionHolder;
import org.springframework.webflow.engine.SimpleFlow;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * Unit tests for {@link FlowController}.
 */
public class FlowControllerTests extends TestCase {
	
	private FlowController controller = new FlowController();

	public void setUp() {
		controller.setServletContext(new MockServletContext());

		FlowDefinitionRegistryImpl registry = new FlowDefinitionRegistryImpl();
		registry.registerFlowDefinition(new StaticFlowDefinitionHolder(new SimpleFlow()));
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecutionRepository repository = new SimpleFlowExecutionRepository(new FlowExecutionImplStateRestorer(
				registry), new SessionBindingConversationManager());
		controller.setFlowExecutor(new FlowExecutorImpl(registry, factory, repository));
	}

	public void testLaunch() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.addParameter("_flowId", "simpleFlow");
		ModelAndView mv = controller.handleRequestInternal(request, response);
		assertEquals("view", mv.getViewName());
	}

	public void testResume() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setContextPath("/app");
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.addParameter("_flowId", "simpleFlow");
		ModelAndView mv = controller.handleRequestInternal(request, response);
		request.addParameter("_flowExecutionKey", (String)mv.getModel().get("flowExecutionKey"));
		request.addParameter("_eventId", "submit");
		mv = controller.handleRequest(request, response);
		assertNull(mv.getViewName());
		assertTrue(mv.getView() instanceof RedirectView);
		RedirectView rv = (RedirectView)mv.getView();
		assertEquals("confirm", rv.getUrl());
		assertNull(mv.getModel().get("flowExecutionKey"));
	}
}