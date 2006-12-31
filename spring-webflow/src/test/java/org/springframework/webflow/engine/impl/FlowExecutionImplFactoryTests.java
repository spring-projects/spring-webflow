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
package org.springframework.webflow.engine.impl;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.SimpleFlow;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.test.MockExternalContext;

public class FlowExecutionImplFactoryTests extends TestCase {
	private FlowExecutionImplFactory factory = new FlowExecutionImplFactory();

	private Flow flowDefinition = new SimpleFlow();

	private boolean starting;
	
	public void testDefaultFactory() {
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertFalse(execution.isActive());
	}
	
	public void testFactoryWithExecutionAttributes() {
		MutableAttributeMap attributes = new LocalAttributeMap();
		attributes.put("foo", "bar");
		factory.setExecutionAttributes(attributes);
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertFalse(execution.isActive());
		assertEquals(attributes, execution.getAttributes());
	}
	
	public void testFactoryWithListener() {
		FlowExecutionListener listener1 = new FlowExecutionListenerAdapter() {
			public void sessionStarting(RequestContext context, FlowDefinition definition, MutableAttributeMap input) {
				starting = true;
			}
		};
		factory.setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(listener1));
		FlowExecution execution = factory.createFlowExecution(flowDefinition);
		assertFalse(execution.isActive());
		execution.start(null, new MockExternalContext());
		assertTrue(starting);
	}
}