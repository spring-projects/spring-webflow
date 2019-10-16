/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.execution.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecutionListener;

/**
 * Unit tests for {@link ConditionalFlowExecutionListenerLoader}.
 */
public class ConditionalFlowExecutionListenerLoaderTests {

	private FlowExecutionListenerCriteriaFactory criteriaFactory;
	private ConditionalFlowExecutionListenerLoader loader;

	@BeforeEach
	public void setUp() {
		loader = new ConditionalFlowExecutionListenerLoader();
		criteriaFactory = new FlowExecutionListenerCriteriaFactory();
	}

	@Test
	public void testAddConditionalListener() {
		FlowExecutionListener listener = new FlowExecutionListener() {};
		loader.addListener(listener, criteriaFactory.allFlows());
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(1, listeners.length);
		assertSame(listener, listeners[0]);
	}

	@Test
	public void testAddMultipleListeners() {
		FlowExecutionListener listener1 = new FlowExecutionListener() {};
		FlowExecutionListener listener2 = new FlowExecutionListener() {};
		loader.addListener(listener1, criteriaFactory.allFlows());
		loader.addListener(listener2, criteriaFactory.allFlows());
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(2, listeners.length);
		assertSame(listener1, listeners[0]);
		assertSame(listener2, listeners[1]);
	}

	@Test
	public void testAddListenerButNoMatch() {
		FlowExecutionListener listener = new FlowExecutionListener() {};
		loader.addListener(listener, criteriaFactory.flow("bar"));
		Flow flow = new Flow("foo");
		FlowExecutionListener[] listeners = loader.getListeners(flow);
		assertEquals(0, listeners.length);
	}
}
