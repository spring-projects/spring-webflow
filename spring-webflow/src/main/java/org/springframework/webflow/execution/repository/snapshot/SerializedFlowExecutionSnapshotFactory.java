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
 * A factory that creates new instances of flow execution snapshots based on standard Java serialization.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class SerializedFlowExecutionSnapshotFactory implements FlowExecutionSnapshotFactory {

	private FlowExecutionFactory flowExecutionFactory;

	private FlowDefinitionLocator flowDefinitionLocator;

	private boolean compress = true;

	/**
	 * Creates a new serialized flow execution snapshot factory
	 * @param flowDefinitionLocator the flow definition locator
	 * @param flowExecutionFactory the flow execution factory
	 */
	public SerializedFlowExecutionSnapshotFactory(FlowExecutionFactory flowExecutionFactory,
			FlowDefinitionLocator flowDefinitionLocator) {
		Assert.notNull(flowExecutionFactory, "The FlowExecutionFactory to restore transient flow state is required");
		Assert.notNull(flowDefinitionLocator, "The FlowDefinitionLocator to restore FlowDefinitions is required");
		this.flowExecutionFactory = flowExecutionFactory;
		this.flowDefinitionLocator = flowDefinitionLocator;
	}

	/**
	 * Returns whether or not the snapshots should be compressed.
	 */
	public boolean getCompress() {
		return compress;
	}

	/**
	 * Set whether or not the snapshots should be compressed.
	 */
	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public FlowExecutionSnapshot createSnapshot(FlowExecution flowExecution) throws SnapshotCreationException {
		return new SerializedFlowExecutionSnapshot(flowExecution, compress);
	}

	public FlowExecution restoreExecution(FlowExecutionSnapshot snapshot, String flowId, FlowExecutionKey key,
			MutableAttributeMap conversationScope, FlowExecutionKeyFactory keyFactory)
			throws FlowExecutionRestorationFailureException {
		SerializedFlowExecutionSnapshot snapshotImpl = (SerializedFlowExecutionSnapshot) snapshot;
		FlowDefinition def = flowDefinitionLocator.getFlowDefinition(flowId);
		FlowExecution execution;
		try {
			execution = snapshotImpl.unmarshal(def.getClassLoader());
		} catch (SnapshotUnmarshalException e) {
			throw new FlowExecutionRestorationFailureException(key, e);
		}
		flowExecutionFactory.restoreFlowExecution(execution, def, key, conversationScope, flowDefinitionLocator);
		return execution;
	}

}