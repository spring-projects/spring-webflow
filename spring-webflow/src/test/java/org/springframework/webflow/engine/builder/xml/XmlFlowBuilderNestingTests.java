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
package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.builder.DefaultFlowServiceLocator;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Test case for XML flow builder, testing flow nesting.
 * 
 * @see org.springframework.webflow.engine.builder.xml.XmlFlowBuilder
 * 
 * @author Erwin Vervaet
 */
public class XmlFlowBuilderNestingTests extends TestCase {

	private BeanFactory parentBeanFactory;

	private Flow flow;

	private TestService testService;

	protected void setUp() throws Exception {
		ClassPathXmlApplicationContext parentContext = new ClassPathXmlApplicationContext(
				"/org/springframework/webflow/engine/builder/xml/testFlow2ParentContext.xml");
		this.parentBeanFactory = parentContext.getBeanFactory();
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("testFlow2.xml", getClass()));
		builder
				.setFlowServiceLocator(new DefaultFlowServiceLocator(new FlowDefinitionRegistryImpl(), parentBeanFactory));
		this.flow = new FlowAssembler("testFlow2", builder).assembleFlow();
		this.testService = (TestService)parentContext.getBean("testService");
	}

	public void testBuildResult() {
		assertEquals("testFlow2", flow.getId());
		assertEquals(3, flow.getStateCount());
		assertEquals("actionState1", flow.getStartState().getId());

		TestAction action1 = (TestAction)((ActionState)flow.getState("actionState1")).getActionList().getAnnotated(0)
				.getTargetAction();
		BeanFactory testFlow2BeanFactory = action1.getBeanFactory();
		assertNotNull(testFlow2BeanFactory);
		assertSame(testService, action1.getTestService());
		assertSame(action1, testFlow2BeanFactory.getBean("action1"));
		assertSame(parentBeanFactory, ((HierarchicalBeanFactory)testFlow2BeanFactory).getParentBeanFactory());
		assertEquals(2, BeanFactoryUtils.countBeansIncludingAncestors(((ListableBeanFactory)testFlow2BeanFactory)));

		Flow subFlow1 = ((SubflowState)flow.getState("subFlowState1")).getSubflow();
		assertNotSame(flow, subFlow1);
		assertEquals("subFlow1", subFlow1.getId());
		assertEquals(2, subFlow1.getStateCount());
		assertEquals("subActionState1", subFlow1.getStartState().getId());

		AnnotatedAction[] actions = ((ActionState)subFlow1.getState("subActionState1")).getActionList()
				.toAnnotatedArray();
		assertEquals(2, actions.length);
		SubTestAction subAction1 = null;
		if (action1 == actions[0].getTargetAction()) {
			subAction1 = (SubTestAction)actions[1].getTargetAction();
		}
		else {
			subAction1 = (SubTestAction)actions[0].getTargetAction();
			assertSame(action1, actions[1].getTargetAction());
		}
		BeanFactory testFlow2SubFlow1BeanFactory = subAction1.getBeanFactory();
		assertNotNull(testFlow2SubFlow1BeanFactory);
		assertSame(testService, subAction1.getTestService());
		assertNull(subAction1.getTestAction()); // autowire by name should have
		// failed
		assertSame(action1, subAction1.getAction1());
		assertSame(subAction1, testFlow2SubFlow1BeanFactory.getBean("subAction1"));
		assertSame(testFlow2BeanFactory, ((HierarchicalBeanFactory)testFlow2SubFlow1BeanFactory).getParentBeanFactory());
		assertEquals(1, ((ListableBeanFactory)testFlow2SubFlow1BeanFactory).getBeanDefinitionCount()); // only
		// subAction1
	}

	public static class TestService {
		public void doIt() {
		}
	}

	public static class TestAction extends AbstractAction implements BeanFactoryAware {
		private TestService testService;

		private BeanFactory beanFactory;

		public TestService getTestService() {
			return testService;
		}

		public void setTestService(TestService testService) {
			this.testService = testService;
		}

		public BeanFactory getBeanFactory() {
			return beanFactory;
		}

		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			this.beanFactory = beanFactory;
		}

		protected Event doExecute(RequestContext context) throws Exception {
			testService.doIt();
			return success();
		}
	}

	public static class SubTestAction extends AbstractAction implements BeanFactoryAware {
		private TestService testService;

		private TestAction testAction;

		private TestAction action1;

		private BeanFactory beanFactory;

		public TestService getTestService() {
			return testService;
		}

		public void setTestService(TestService testService) {
			this.testService = testService;
		}

		public TestAction getTestAction() {
			return testAction;
		}

		public void setTestAction(TestAction testAction) {
			this.testAction = testAction;
		}

		public TestAction getAction1() {
			return action1;
		}

		public void setAction1(TestAction action1) {
			this.action1 = action1;
		}

		public BeanFactory getBeanFactory() {
			return beanFactory;
		}

		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			this.beanFactory = beanFactory;
		}

		protected Event doExecute(RequestContext context) throws Exception {
			testService.doIt();
			return testAction.execute(context);
		}
	}
}