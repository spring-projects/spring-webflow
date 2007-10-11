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