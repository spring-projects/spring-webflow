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
package org.springframework.webflow.execution.factory;

import junit.framework.TestCase;

import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;

/**
 * Unit tests for {@link StaticFlowExecutionListenerLoader}.
 */
public class StaticFlowExecutionListenerLoaderTests extends TestCase {

	private FlowExecutionListenerLoader loader = StaticFlowExecutionListenerLoader.EMPTY_INSTANCE;

	public void testEmptyListenerArray() {
		assertEquals(0, loader.getListeners(new Flow("foo")).length);
		assertEquals(0, loader.getListeners(null).length);
	}

	public void testStaticListener() {
		final FlowExecutionListener listener1 = new FlowExecutionListenerAdapter() {
		};
		loader = new StaticFlowExecutionListenerLoader(listener1);
		assertEquals(listener1, loader.getListeners(new Flow("foo"))[0]);
	}

	public void testStaticListeners() {
		final FlowExecutionListener listener1 = new FlowExecutionListenerAdapter() {
		};
		final FlowExecutionListener listener2 = new FlowExecutionListenerAdapter() {
		};

		loader = new StaticFlowExecutionListenerLoader(new FlowExecutionListener[] { listener1, listener2 });
		assertEquals(listener1, loader.getListeners(new Flow("foo"))[0]);
		assertEquals(listener2, loader.getListeners(new Flow("foo"))[1]);
	}

}