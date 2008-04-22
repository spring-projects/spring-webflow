package org.springframework.webflow.execution;

import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * An outcome returned by a flow execution when it ends.
 * 
 * @author Keith Donald
 */
public class FlowExecutionOutcome {

	private String id;

	private AttributeMap output;

	/**
	 * Creates a new flow execution outcome
	 * @param id the outcome id
	 * @param output the output returned by the execution
	 */
	public FlowExecutionOutcome(String id, AttributeMap output) {
		super();
		this.id = id;
		this.output = (output != null ? output : CollectionUtils.EMPTY_ATTRIBUTE_MAP);
	}

	/**
	 * Return the outcome identifier.
	 * @return the outcome that was reached by the flow execution
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the output returned by the flow execution.
	 * @return the output that was returned
	 */
	public AttributeMap getOutput() {
		return output;
	}

	public String toString() {
		return new ToStringCreator(this).append("id", id).append("output", output).toString();
	}

}
