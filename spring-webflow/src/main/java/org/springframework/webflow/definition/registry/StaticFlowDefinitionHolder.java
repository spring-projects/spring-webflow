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
package org.springframework.webflow.definition.registry;

import org.springframework.webflow.definition.FlowDefinition;

/**
 * A simple flow definition holder that just holds a constant singleton reference to a flow definition.
 * @author Keith Donald
 */
class StaticFlowDefinitionHolder implements FlowDefinitionHolder {

	/**
	 * The held flow definition.
	 */
	private final FlowDefinition flowDefinition;

	/**
	 * Creates the static flow definition holder.
	 * @param flowDefinition the flow to hold
	 */
	public StaticFlowDefinitionHolder(FlowDefinition flowDefinition) {
		this.flowDefinition = flowDefinition;
	}

	public String getFlowDefinitionId() {
		return flowDefinition.getId();
	}

	public FlowDefinition getFlowDefinition() throws FlowDefinitionConstructionException {
		return flowDefinition;
	}

	public void refresh() throws FlowDefinitionConstructionException {
		// nothing to do
	}

	public boolean equals(Object o) {
		if (!(o instanceof StaticFlowDefinitionHolder)) {
			return false;
		}
		StaticFlowDefinitionHolder other = (StaticFlowDefinitionHolder) o;
		return flowDefinition.equals(other.flowDefinition);
	}

	public int hashCode() {
		return flowDefinition.hashCode();
	}

}