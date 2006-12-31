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

import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Named action tests.
 * 
 * @author Erwin Vervaet
 */
public class NamedActionXmlFlowBuilderTests extends AbstractXmlFlowExecutionTests {
	
	protected FlowDefinitionResource getFlowDefinitionResource() {
		return new FlowDefinitionResource(
				new ClassPathResource("namedActionFlow.xml", NamedActionXmlFlowBuilderTests.class));
	}
	
	private int executionOrderCounter = 0;
	
	private Action aAction;
	private int aActionExecutionCount = 0;
	private int aActionExecutionOrder;
	private Object bBean;
	private int bBeanExecutionCount = 0;
	private int bBeanExecutionOrder;
	private Action cAction;
	private int cActionExecutionCount = 0;
	private int cActionExecutionOrder;
	
	protected void setUp() throws Exception {
		aAction = new AbstractAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				aActionExecutionCount++;
				aActionExecutionOrder = executionOrderCounter++;
				return success();
			}
		};
		bBean = new TestBean(this);
		cAction = new AbstractAction() {
			protected Event doExecute(RequestContext context) throws Exception {
				cActionExecutionCount++;
				cActionExecutionOrder = executionOrderCounter++;
				return success();
			}
		};
	}
	
	protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
		serviceRegistry.registerBean("aAction", aAction);
		serviceRegistry.registerBean("bBean", bBean);
		serviceRegistry.registerBean("cAction", cAction);
	}
	
	public void testActionExecutionOrder() {
		startFlow();
		assertFlowExecutionEnded();
		assertEquals(1, aActionExecutionCount);
		assertEquals(0, aActionExecutionOrder);
		assertEquals(1, bBeanExecutionCount);
		assertEquals(1, bBeanExecutionOrder);
		assertEquals(1, cActionExecutionCount);
		assertEquals(2, cActionExecutionOrder);
	}

	public static class TestBean {
		private NamedActionXmlFlowBuilderTests testCase;
		
		public TestBean(NamedActionXmlFlowBuilderTests testCase) {
			this.testCase = testCase;
		}
		public void b() {
			testCase.bBeanExecutionCount++;
			testCase.bBeanExecutionOrder = testCase.executionOrderCounter++;
		}
	}
}
