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
package org.springframework.webflow.engine.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;

/**
 * A factory for instances of the {@link FlowExecutionImpl default flow execution} implementation.
 * @author Keith Donald
 */
public class FlowExecutionImplFactory extends FlowExecutionImplServicesConfigurer implements FlowExecutionFactory {

	private static final Log logger = LogFactory.getLog(FlowExecutionImplFactory.class);

	/**
	 * The factory used to assign keys to flow executions that need to be persisted.
	 */
	private FlowExecutionKeyFactory executionKeyFactory = new SimpleFlowExecutionKeyFactory();

	/**
	 * Sets the strategy for generating flow execution keys for persistent flow executions.
	 */
	public void setExecutionKeyFactory(FlowExecutionKeyFactory executionKeyFactory) {
		this.executionKeyFactory = executionKeyFactory;
	}

	public FlowExecution createFlowExecution(FlowDefinition flowDefinition) {
		Assert.isInstanceOf(Flow.class, flowDefinition, "Flow definition is of wrong type: ");
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new execution of '" + flowDefinition.getId() + "'");
		}
		FlowExecutionImpl execution = new FlowExecutionImpl((Flow) flowDefinition);
		configureServices(execution);
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
				return value * 29;
			}

			public String toString() {
				return String.valueOf(value);
			}
		}
	}
}