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
package org.springframework.webflow.engine.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;

/**
 * A factory for instances of the {@link FlowExecutionImpl default flow execution} implementation.
 * @author Keith Donald
 */
public class FlowExecutionImplFactory implements FlowExecutionFactory {

	private static final Log logger = LogFactory.getLog(FlowExecutionImplFactory.class);

	private AttributeMap executionAttributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;

	private FlowExecutionListenerLoader executionListenerLoader = StaticFlowExecutionListenerLoader.EMPTY_INSTANCE;

	private FlowExecutionKeyFactory executionKeyFactory = new SimpleFlowExecutionKeyFactory();

	/**
	 * Sets the attributes to apply to flow executions created by this factory. Execution attributes may affect flow
	 * execution behavior.
	 * @param executionAttributes flow execution system attributes
	 */
	public void setExecutionAttributes(AttributeMap executionAttributes) {
		this.executionAttributes = executionAttributes;
	}

	/**
	 * Sets the strategy for loading listeners that should observe executions of a flow definition. Allows full control
	 * over what listeners should apply for executions of a flow definition.
	 */
	public void setExecutionListenerLoader(FlowExecutionListenerLoader executionListenerLoader) {
		this.executionListenerLoader = executionListenerLoader;
	}

	/**
	 * Sets the strategy for generating flow execution keys for persistent flow executions.
	 */
	public void setExecutionKeyFactory(FlowExecutionKeyFactory executionKeyFactory) {
		this.executionKeyFactory = executionKeyFactory;
	}

	public FlowExecution createFlowExecution(FlowDefinition flowDefinition) {
		Assert.isInstanceOf(Flow.class, flowDefinition, "FlowDefinition is of the wrong type: ");
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new execution of '" + flowDefinition.getId() + "'");
		}
		FlowExecutionImpl execution = new FlowExecutionImpl((Flow) flowDefinition);
		execution.setAttributes(executionAttributes);
		execution.setListeners(executionListenerLoader.getListeners(execution.getDefinition()));
		execution.setKeyFactory(executionKeyFactory);
		return execution;
	}

	public FlowExecution restoreFlowExecution(FlowExecution flowExecution, FlowDefinition flowDefinition,
			FlowExecutionKey flowExecutionKey, MutableAttributeMap conversationScope,
			FlowDefinitionLocator subflowDefinitionLocator) {
		Assert.isInstanceOf(FlowExecutionImpl.class, flowExecution, "FlowExecution is of the wrong type: ");
		Assert.isInstanceOf(Flow.class, flowDefinition, "FlowDefinition is of the wrong type: ");
		FlowExecutionImpl execution = (FlowExecutionImpl) flowExecution;
		Flow flow = (Flow) flowDefinition;
		execution.setFlow(flow);
		if (execution.hasSessions()) {
			FlowSessionImpl rootSession = execution.getRootSession();
			rootSession.setFlow(flow);
			rootSession.setState(flow.getStateInstance(rootSession.getStateId()));
			if (execution.hasSubflowSessions()) {
				for (Iterator it = execution.getSubflowSessionIterator(); it.hasNext();) {
					FlowSessionImpl subflowSession = (FlowSessionImpl) it.next();
					Flow subflowDef = (Flow) subflowDefinitionLocator.getFlowDefinition(subflowSession.getFlowId());
					subflowSession.setFlow(subflowDef);
					subflowSession.setState(subflowDef.getStateInstance(subflowSession.getStateId()));
				}
			}
		}
		execution.setKey(flowExecutionKey);
		if (conversationScope == null) {
			conversationScope = new LocalAttributeMap();
		}
		execution.setConversationScope(conversationScope);
		execution.setAttributes(executionAttributes);
		execution.setListeners(executionListenerLoader.getListeners(execution.getDefinition()));
		execution.setKeyFactory(executionKeyFactory);
		return execution;
	}

	/**
	 * Simple key factory suitable for standalone usage and testing. Not expected to be used in a web environment.
	 */
	private static class SimpleFlowExecutionKeyFactory implements FlowExecutionKeyFactory {

		private int sequence;

		public FlowExecutionKey getKey(FlowExecution execution) {
			if (execution.getKey() == null) {
				return new SimpleFlowExecutionKey(nextSequence());
			} else {
				// keep the same key
				return execution.getKey();
			}
		}

		public void removeAllFlowExecutionSnapshots(FlowExecution execution) {
		}

		public void removeFlowExecutionSnapshot(FlowExecution execution) {
		}

		public void updateFlowExecutionSnapshot(FlowExecution execution) {
		}

		private synchronized int nextSequence() {
			return ++sequence;
		}

		private static class SimpleFlowExecutionKey extends FlowExecutionKey {

			private int value;

			public SimpleFlowExecutionKey(int value) {
				this.value = value;
			}

			public boolean equals(Object o) {
				if (!(o instanceof SimpleFlowExecutionKey)) {
					SimpleFlowExecutionKey key = (SimpleFlowExecutionKey) o;
					return value == key.value;
				}
				return false;
			}

			public int hashCode() {
				return value;
			}

			public String toString() {
				return String.valueOf(value);
			}
		}
	}
}