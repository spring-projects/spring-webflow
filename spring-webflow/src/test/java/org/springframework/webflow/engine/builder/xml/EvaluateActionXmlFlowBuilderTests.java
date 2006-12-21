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
package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowSessionStatus;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;

public class EvaluateActionXmlFlowBuilderTests extends TestCase {
	private Flow flow;

	protected void setUp() throws Exception {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("evaluateActionFlow.xml",
				XmlFlowBuilderTests.class), new TestFlowServiceLocator());
		flow = new FlowAssembler("evaluateActionFlow", builder).assembleFlow();
	}

	public void testActionStateConfiguration() {
		assertTrue(flow.getState("actionState1") instanceof ActionState);
	}

	public void testFlowExecution() {
		FlowExecutionImplFactory factory = new FlowExecutionImplFactory();
		FlowExecution execution = factory.createFlowExecution(flow);
		ApplicationView selection = (ApplicationView)execution.start(null, new MockExternalContext());
		assertEquals(FlowSessionStatus.CREATED, execution.getActiveSession().getScope().get("sessionStatus"));
		assertNotNull(selection.getModel().get("hashCode"));
		assertEquals(new Integer(FlowSessionStatus.CREATED.hashCode()), selection.getModel().get("hashCode"));
	}
}