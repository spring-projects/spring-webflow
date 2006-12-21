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

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.DefaultFlowServiceLocator;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;

public class MessageSourceAwareTests extends TestCase {

	private Flow flow;
	
	protected void setUp() throws Exception {
		GenericApplicationContext context = new GenericApplicationContext();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FooMessageSource.class);
		context.registerBeanDefinition("messageSource", builder.getBeanDefinition());
		context.refresh();
		DefaultFlowServiceLocator locator = new DefaultFlowServiceLocator(new FlowDefinitionRegistryImpl(), context);
		XmlFlowBuilder flowBuilder = new XmlFlowBuilder(new ClassPathResource("messageSourceAwareFlow.xml", getClass()), locator);
		flow = new FlowAssembler("flow", flowBuilder).assembleFlow();
	}
	
	private static class FooMessageSource extends StaticMessageSource {
		public FooMessageSource() {
			addMessage("foo", Locale.getDefault(), "bar");
		}		
	}
	
	public void testAwareAction() {
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(flow);
		execution.start(null, new MockExternalContext());
		assertFalse(execution.isActive());
	}

}
