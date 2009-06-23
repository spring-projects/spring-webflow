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
package org.springframework.webflow.execution.factory;

import junit.framework.TestCase;

import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;

/**
 * Unit tests for {@link ConditionalFlowExecutionListenerLoader}.
 */
public class ConditionalFlowExecutionListenerLoaderTests extends TestCase {

	private FlowExecutionListenerCriteriaFactory criteriaFactory;
	private ConditionalFlowExecutionListenerLoader loader;

	protected void setUp() {
		loader = new ConditionalFlowExecutionListenerLoader();
		criteriaFactory = new FlowExecutionListenerCriteriaFactory();
	}

	public void testAddConditionalListener() {
		FlowExecutionListenerAdapter listener = new FlowExecutionListenerAdapter() {
		};
		loader.addListener(listener, criteriaFactory.allFlows());
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(1, listeners.length);
		assertSame(listener, listeners[0]);
	}

	public void testAddMultipleListeners() {
		FlowExecutionListenerAdapter listener = new FlowExecutionListenerAdapter() {
		};
		FlowExecutionListenerAdapter listener2 = new FlowExecutionListenerAdapter() {
		};
		loader.addListener(listener, criteriaFactory.allFlows());
		loader.addListener(listener2, criteriaFactory.allFlows());
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(2, listeners.length);
		assertSame(listener, listeners[0]);
		assertSame(listener2, listeners[1]);
	}

	public void testAddListenerButNoMatch() {
		FlowExecutionListenerAdapter listener = new FlowExecutionListenerAdapter() {
		};
		loader.addListener(listener, criteriaFactory.flow("bar"));
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(0, listeners.length);
	}
}
