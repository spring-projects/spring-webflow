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
package org.springframework.webflow.execution.repository.snapshot;

import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;

/**
 * A factory that creates new flow execution snapshot instances that simply wraps an underlying
 * {@link FlowExecution flow execution} instance.
 * 
 * Note: this class is generally only suitable for use with a repository that has maxSnapshots set to 1, since no actual
 * copies of the execution data are made by this factory. This class exists to support the use case where taking copies
 * of flow execution state is not needed.
 * 
 * @author Keith Donald
 */
public class SimpleFlowExecutionSnapshotFactory implements FlowExecutionSnapshotFactory {

	private FlowExecutionFactory flowExecutionFactory;

	private FlowDefinitionLocator flowDefinitionLocator;

	/**
	 * Creates a new simple flow execution snapshot factory
	 * @param flowDefinitionLocator the flow definition locator
	 * @param flowExecutionFactory the flow execution factory
	 */
	public SimpleFlowExecutionSnapshotFactory(FlowExecutionFactory flowExecutionFactory,
			FlowDefinitionLocator flowDefinitionLocator) {
		Assert.notNull(flowExecutionFactory, "The FlowExecutionFactory to restore transient flow state is required");
		Assert.notNull(flowDefinitionLocator, "The FlowDefinitionLocator to restore FlowDefinitions is required");
		this.flowExecutionFactory = flowExecutionFactory;
		this.flowDefinitionLocator = flowDefinitionLocator;
	}

	public FlowExecutionSnapshot createSnapshot(FlowExecution flowExecution) throws SnapshotCreationException {
		return new SimpleFlowExecutionSnapshot(flowExecution);
	}

	public FlowExecution restoreExecution(FlowExecutionSnapshot snapshot, String flowId, FlowExecutionKey key,
			MutableAttributeMap conversationScope, FlowExecutionKeyFactory keyFactory)
			throws FlowExecutionRestorationFailureException {
		SimpleFlowExecutionSnapshot snapshotImpl = (SimpleFlowExecutionSnapshot) snapshot;
		FlowDefinition def = flowDefinitionLocator.getFlowDefinition(flowId);
		FlowExecution execution = snapshotImpl.getFlowExecution();
		flowExecutionFactory.restoreFlowExecution(execution, def, key, conversationScope, flowDefinitionLocator);
		return execution;
	}

	private static class SimpleFlowExecutionSnapshot extends FlowExecutionSnapshot {
		private FlowExecution flowExecution;

		public SimpleFlowExecutionSnapshot(FlowExecution flowExecution) {
			this.flowExecution = flowExecution;
		}

		public FlowExecution getFlowExecution() {
			return flowExecution;
		}
	}

}