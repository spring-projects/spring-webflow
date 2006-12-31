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
package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.executor.mvc.FlowController;

/**
 * Test case that illustrates configuration of a FlowController and its
 * associated artefacts using classic spring bean configuration information.
 * This test case does not really test much but serves more as an example.
 * 
 * @author Erwin Vervaet
 */
public class BeanConfigTests extends TestCase {

	private BeanFactory beanFactory;
	
	protected void setUp() throws Exception {
		beanFactory = new ClassPathXmlApplicationContext("webflow-config-classic.xml", BeanConfigTests.class);
	}
	
	public void testFlowControllerConfig() {
		FlowController flowController = (FlowController)beanFactory.getBean("flowController");
		assertEquals("test-flow", flowController.getArgumentHandler().getDefaultFlowId());
	}
	
	public void testFlowControllerBeanConfig() {
		FlowController flowController = (FlowController)beanFactory.getBean("flowController-bean");
		assertEquals("test-flow", flowController.getArgumentHandler().getDefaultFlowId());
	}
}
